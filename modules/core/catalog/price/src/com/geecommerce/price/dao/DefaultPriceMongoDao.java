package com.geecommerce.price.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.annotation.Implements;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.DefaultMongoDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.google.inject.Inject;

@Dao
@Persistence(name = "mongodb", model = Price.class)
@Implements(PriceDao.class)
public class DefaultPriceMongoDao extends DefaultMongoDao implements PriceDao {
    private static final String TABLE_NAME = "price";

    @Inject
    public DefaultPriceMongoDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getCollectionName(Class<T> modelClass) {
        return TABLE_NAME;
    }

    @Override
    public Map<String, Object> getPriceData(Id productId, Id requestCtxId) {
        StringBuilder sql = new StringBuilder()
            .append(" select\n")
            .append(" /* price */ \n")
            .append(" ifnull(tbl_context_variant_price.price, ifnull(tbl_base_variant_price.price, ifnull(tbl_context_price.price, tbl_base_price.price))) as price,\n")
            .append(" -- special-price-start\n")
            .append(" ifnull(if(tbl_context_variant_price.special_price is not null \n")
            .append(" and tbl_context_variant_price.special_price_from is not null \n")
            .append(
                " and now() between tbl_context_variant_price.special_price_from and if(tbl_context_variant_price.special_price_to is null or tbl_context_variant_price.special_price_to='0000-00-00 00:00:00', now(), tbl_context_variant_price.special_price_to),\n")
            .append(" tbl_context_variant_price.special_price, null),\n")
            .append(" -- special-price-base-variant\n")
            .append(" ifnull(if(tbl_base_variant_price.special_price is not null \n")
            .append(" and tbl_base_variant_price.special_price_from is not null \n")
            .append(
                " and now() between tbl_base_variant_price.special_price_from and if(tbl_base_variant_price.special_price_to is null or tbl_base_variant_price.special_price_to='0000-00-00 00:00:00', now(), tbl_base_variant_price.special_price_to),\n")
            .append(" tbl_base_variant_price.special_price, null),\n").append(" -- special-price-context\n").append(" ifnull(if(tbl_context_price.special_price is not null \n")
            .append(" and tbl_context_price.special_price_from is not null \n")
            .append(
                " and now() between tbl_context_price.special_price_from and if(tbl_context_price.special_price_to is null or tbl_context_price.special_price_to='0000-00-00 00:00:00', now(), tbl_context_price.special_price_to),\n")
            .append(" tbl_context_price.special_price, null),\n").append(" -- special-price-base\n").append(" if(tbl_base_price.special_price is not null \n")
            .append(" and tbl_base_price.special_price_from is not null \n")
            .append(
                " and now() between tbl_base_price.special_price_from and if(tbl_base_price.special_price_to is null or tbl_base_price.special_price_to='0000-00-00 00:00:00', now(), tbl_base_price.special_price_to),\n")
            .append(" tbl_base_price.special_price, null)\n").append(" ))) as special_price,\n").append(" -- special-price-end\n").append(" \n").append(" -- price\n")
            .append(" tbl_base_price.price as base_price,\n")
            .append(" tbl_base_variant_price.price as base_variant_price,\n").append(" tbl_context_price.price as base_context_price,\n")
            .append(" tbl_context_variant_price.price as base_context_variant_price,\n").append(" \n")
            .append(" -- special_price\n").append(" tbl_base_price.special_price as base_special_price,\n").append(" tbl_base_variant_price.special_price as base_variant_special_price,\n")
            .append(" tbl_context_price.special_price as base_context_special_price,\n").append(" tbl_context_variant_price.special_price as base_context_variant_special_price,\n").append(" \n")
            .append(" -- special_price_from\n")
            .append(" tbl_base_price.special_price_from as base_special_price_from,\n").append(" tbl_base_variant_price.special_price_from as base_variant_special_price_from,\n")
            .append(" tbl_context_price.special_price_from as base_context_special_price_from,\n").append(" tbl_context_variant_price.special_price_from as base_context_variant_special_price_from,\n")
            .append(" \n")
            .append(" -- special_price_to\n").append(" tbl_base_price.special_price_to as base_special_price_to,\n")
            .append(" tbl_base_variant_price.special_price_to as base_variant_special_price_to,\n")
            .append(" tbl_context_price.special_price_to as base_context_special_price_to,\n").append(" tbl_context_variant_price.special_price_to as base_context_variant_special_price_to\n")
            .append(" \n").append(" from\n").append(" (\n")
            .append("   select * from ").append(TABLE_NAME).append("\n").append("   where product_id = ?\n") // productId
            .append("   and variant_id is null\n").append("   and req_ctx_id is null\n").append("   limit 0,1\n").append(" ) as tbl_base_price\n");
        sql.append(" left join\n").append(" (\n").append("   select * from ").append(TABLE_NAME).append("\n").append("   where product_id = -1\n") // productId
            .append("   and variant_id = -1\n") // variantId
            .append("   and req_ctx_id = -1\n").append("   limit 0,1\n").append(" ) as tbl_base_variant_price\n").append(" on tbl_base_price.product_id=tbl_base_variant_price.product_id\n");

        if (requestCtxId != null) {
            sql.append(" left join\n").append(" (\n").append("   select * from ").append(TABLE_NAME).append("\n").append("   where product_id = ?\n") // productId
                .append("   and variant_id is null\n").append("   and req_ctx_id = ?\n") // reqCtx
                .append("   limit 0,1\n").append(" ) as tbl_context_price\n").append(" on tbl_base_price.product_id=tbl_context_price.product_id\n");
        } else {
            sql.append(" left join\n").append(" (\n").append("   select * from ").append(TABLE_NAME).append("\n").append("   where product_id = -1\n") // productId
                .append("   and variant_id = -1\n").append("   and req_ctx_id = -1\n") // reqCtx
                .append("   limit 0,1\n").append(" ) as tbl_context_price\n").append(" on tbl_base_price.product_id=tbl_context_price.product_id\n");
        }

        sql.append(" left join\n").append(" (\n").append("   select * from ").append(TABLE_NAME).append("\n").append("   where product_id = -1\n") // productId
            .append("   and variant_id = -1\n") // variantId
            .append("   and req_ctx_id = -1\n") // reqCtx
            .append("   limit 0,1\n").append(" ) as tbl_context_variant_price\n").append(" on tbl_base_price.product_id=tbl_context_variant_price.product_id\n");

        Connection conn = (Connection) connections.getConnection(Price.class);
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            int i = 0;

            pstmt.setString(++i, productId.str());

            if (requestCtxId != null) {
                pstmt.setString(++i, productId.str());
                pstmt.setString(++i, requestCtxId.str());
            }

            ResultSet rs = pstmt.executeQuery();

            Map<String, Object> productPriceData = new HashMap<>();

            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();

                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    Object o = rs.getObject(j);
                    String columnName = metaData.getColumnLabel(j);

                    if (o instanceof java.sql.Timestamp) {
                        productPriceData.put(columnName, new Date(rs.getTimestamp(j).getTime()));
                    } else {
                        productPriceData.put(columnName, rs.getDouble(j));
                    }
                }
            }

            return productPriceData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}