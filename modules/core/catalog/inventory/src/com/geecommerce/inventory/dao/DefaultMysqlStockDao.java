package com.geecommerce.inventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.annotation.Implements;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mysql.DefaultMySqlDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.IO;
import com.geecommerce.core.util.Strings;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;
import com.geecommerce.inventory.model.InventoryItem;
import com.google.inject.Inject;

@Dao
@Persistence(name = "mysql", model = InventoryItem.class)
@Implements(StockDao.class)
public class DefaultMysqlStockDao extends DefaultMySqlDao implements StockDao {
    private static final String TABLE_NAME = "inventory_stock";

    private static final String KEY_ALLOW_BACKORDER = "inventory/stock/allow_backorder";
    private static final String KEY_DECREASE_ORDERED_QTY = "inventory/stock/decrease_ordered_qty";
    private static final String KEY_ALLOW_QTY_BELOW_ZERO = "inventory/stock/allow_qty_below_zero";

    private static final String REGISTRY_KEY_PRELOADED_STOCK_DATA = "preloaded.stock.data";

    @Inject
    public DefaultMysqlStockDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getTableName(Class<T> modelClass) {
        return TABLE_NAME;
    }

    @Override
    public void decrementQty(Id productId, Id storeId, int decrementByQty) throws QuantityNotAvailableException {
        boolean isDecreaseOrderedQty = app.cpBool_(KEY_DECREASE_ORDERED_QTY, true);

        if (!isDecreaseOrderedQty)
            return;

        boolean isBackorderAllowed = app.cpBool_(KEY_ALLOW_BACKORDER, false);
        boolean isQtyBelowZeroAllowed = app.cpBool_(KEY_ALLOW_QTY_BELOW_ZERO, false);

        Connection conn = connection();

        StringBuilder sql = new StringBuilder().append(" select * from ").append(TABLE_NAME)
            .append(" where prd_id = ?\n").append(" and store_id = ?\n");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean updatedQty = false;

        try {
            pstmt = conn.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            int i = 0;

            pstmt.setString(++i, productId.str());
            pstmt.setString(++i, storeId.str());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                if (rs.next())
                    throw new RuntimeException("Unable to update quantity for [productId=" + productId + ", storeId="
                        + storeId + "]. More than one row to update found.");

                if (!rs.isFirst())
                    rs.first();

                int qty = rs.getInt("qty");
                boolean isItemBackorderAllowed = rs.getBoolean("backorder");

                if (!rs.wasNull()) {
                    int newQty = qty - decrementByQty;

                    if (newQty < 0 && !isBackorderAllowed && !isItemBackorderAllowed)
                        throw new QuantityNotAvailableException("Unable to update quantity for [productId=" + productId
                            + ", storeId=" + storeId + "]. Not enough stock.");

                    if (newQty < 0 && !isQtyBelowZeroAllowed)
                        newQty = 0;

                    rs.updateInt("qty", newQty);

                    rs.updateRow();

                    // app.publish("product:update", productId);

                    updatedQty = true;
                } else {
                    rs.cancelRowUpdates();
                    throw new RuntimeException("Unable to update quantity for [productId=" + productId + ", storeId="
                        + storeId + "]. Initial qty has NULL value.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (!updatedQty) {
            sql = new StringBuilder().append(" select * from ").append(TABLE_NAME)
                .append(" where prd_id = ? and store_id is null\n");

            pstmt = null;
            rs = null;

            try {
                pstmt = conn.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

                int i = 0;

                pstmt.setString(++i, productId.str());

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    if (rs.next())
                        throw new RuntimeException("Unable to update quantity for [productId=" + productId
                            + "]. More than one row to update found.");

                    if (!rs.isFirst())
                        rs.first();

                    int qty = rs.getInt("qty");
                    boolean isItemBackorderAllowed = rs.getBoolean("backorder");

                    if (!rs.wasNull()) {
                        int newQty = qty - decrementByQty;

                        if (newQty < 0 && !isBackorderAllowed && !isItemBackorderAllowed)
                            throw new RuntimeException(
                                "Unable to update quantity for [productId=" + productId + "]. Not enough stock.");

                        if (newQty < 0 && !isQtyBelowZeroAllowed)
                            newQty = 0;

                        rs.updateInt("qty", newQty);

                        rs.updateRow();

                        updatedQty = true;
                    } else {
                        rs.cancelRowUpdates();
                        throw new RuntimeException("Unable to update quantity for [productId=" + productId
                            + "]. Initial qty has NULL value.");
                    }
                } else {
                    throw new RuntimeException("Unable to update quantity for [productId=" + productId
                        + "]. No initial qty set to decrement from.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    rs.close();
                    pstmt.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getStockData(Id productId, Id storeId) {
        Map<Id, Map<String, Object>> preloadedStockData = app.registryGet(REGISTRY_KEY_PRELOADED_STOCK_DATA);

        if (preloadedStockData != null && preloadedStockData.get(productId) != null)
            return preloadedStockData.get(productId);

        StringBuilder sql = new StringBuilder("select\n");

        if (storeId != null) {
            sql.append("ifnull(stock_store.qty, stock_default.qty) as qty,\n");
            sql.append("ifnull(stock_store.backorder, stock_default.backorder) as backorder\n");
        } else {
            sql.append("stock_default.qty as qty,\n");
            sql.append("stock_default.backorder as backorder\n");
        }

        sql.append("from inventory_stock stock_all\n");
        sql.append("left join inventory_stock stock_default\n");
        sql.append("on stock_default.prd_id=stock_all.prd_id\n");
        sql.append("and stock_default.store_id is null\n");

        if (storeId != null) {
            sql.append("left join inventory_stock stock_store\n");
            sql.append("on stock_store.prd_id=stock_all.prd_id\n");
            sql.append("and stock_store.store_id=?\n");
        }

        sql.append("where stock_all.prd_id = ?\n");
        sql.append("limit 0,1\n");

        // System.out.println("getStockData: " + sql);
        long start = System.currentTimeMillis();

        Connection conn = connection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            int i = 0;

            if (storeId != null) {
                pstmt.setString(++i, storeId.str());
            }

            pstmt.setString(++i, productId.str());

            rs = pstmt.executeQuery();

            Map<String, Object> productStockData = new HashMap<>();

            if (rs.next()) {
                productStockData.put("qty", rs.getInt("qty"));
                productStockData.put("allow_backorder", rs.getBoolean("backorder"));
            }

            // System.out.println("getStockData in: " +
            // (System.currentTimeMillis() - start));

            return productStockData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(rs);
            IO.closeQuietly(pstmt);
        }
    }

    @Override
    public void preloadStockData(Collection<Id> productIds, Id storeId) {
        StringBuilder sql = new StringBuilder("select stock_all.prd_id as prd_id,\n");

        if (storeId != null) {
            sql.append("ifnull(stock_store.qty, stock_default.qty) as qty,\n");
            sql.append("ifnull(stock_store.backorder, stock_default.backorder) as backorder\n");
        } else {
            sql.append("stock_default.qty as qty,\n");
            sql.append("stock_default.backorder as backorder\n");
        }

        sql.append("from inventory_stock stock_all\n");
        sql.append("left join inventory_stock stock_default\n");
        sql.append("on stock_default.prd_id=stock_all.prd_id\n");
        sql.append("and stock_default.store_id is null\n");

        if (storeId != null) {
            sql.append("left join inventory_stock stock_store\n");
            sql.append("on stock_store.prd_id=stock_all.prd_id\n");
            sql.append("and stock_store.store_id=?\n");
        }

        sql.append("where stock_all.prd_id in (" + Strings.toCsvString(productIds) + ")\n");

        // System.out.println("preloadStockData: " + sql);

        long start = System.currentTimeMillis();

        // StringBuilder sql = new StringBuilder("select");
        // sql.append(" prd_id, qty, backorder\n")
        // .append(" from ").append(TABLE_NAME).append("\n")
        // .append(" where prd_id in (" +
        // Strings.toCsvString(Arrays.asList(productIds)) + ")\n");

        Connection conn = connection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            int i = 0;

            if (storeId != null) {
                pstmt.setString(++i, storeId.str());
            }

            // pstmt.setString(++i, productId.str());

            rs = pstmt.executeQuery();

            Map<Id, Map<String, Object>> preloadedStockData = new HashMap<>();

            while (rs.next()) {
                Map<String, Object> productStockData = new HashMap<>();
                productStockData.put("qty", rs.getInt("qty"));
                productStockData.put("allow_backorder", rs.getBoolean("backorder"));

                preloadedStockData.put(Id.valueOf(rs.getString("prd_id")), productStockData);
            }

            if (preloadedStockData.size() > 0)
                app.registryPut(REGISTRY_KEY_PRELOADED_STOCK_DATA, preloadedStockData);

            // System.out.println("preloadStockData in : " +
            // (System.currentTimeMillis() - start));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(rs);
            IO.closeQuietly(pstmt);
        }
    }
}