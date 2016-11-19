package com.geecommerce.checkout.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.geecommerce.checkout.enums.AddressType;
import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mysql.DefaultMySqlDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.IO;
import com.google.inject.Inject;

@Dao
@Persistence("mysql")
public class DefaultOrderAddressDao extends DefaultMySqlDao implements OrderAddressDao {
    private static final String TABLE_NAME = "sale_order_address";

    @Inject
    public DefaultOrderAddressDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getTableName(Class<T> modelClass) {
        return TABLE_NAME;
    }

    @Override
    public OrderAddress getLastInvoiceAddress(Id customerId) {
        StringBuilder sql = new StringBuilder("SELECT a.* FROM");
        sql.append(" sale_order o,").append(" sale_order_address a").append(" WHERE o.customer_fk=?").append(" AND a.`type`=2").append(" AND a.order_fk=o._id").append(" ORDER BY o.cr_on DESC")
            .append(" LIMIT 0,1");

        Connection conn = connection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            pstmt.setString(1, customerId.str());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                OrderAddress address = app.getModel(OrderAddress.class);

                address.setFirstName(rs.getString(OrderAddress.Column.FIRST_NAME));
                address.setLastName(rs.getString(OrderAddress.Column.LAST_NAME));
                address.setAddress1(rs.getString(OrderAddress.Column.ADDRESS1));
                address.setAddress2(rs.getString(OrderAddress.Column.ADDRESS2));
                address.setHouseNumber(rs.getString(OrderAddress.Column.HOUSE_NUMBER));
                address.setCity(rs.getString(OrderAddress.Column.CITY));
                address.setZip(rs.getString(OrderAddress.Column.ZIP));
                address.setCountry(rs.getString(OrderAddress.Column.COUNTRY));
                address.setAddressType(AddressType.fromId(rs.getInt(OrderAddress.Column.TYPE)));

                return address;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(rs);
            IO.closeQuietly(pstmt);
        }

        return null;
    }

    @Override
    public OrderAddress getLastTransferredInvoiceAddress(Id customerId) {
        StringBuilder sql = new StringBuilder("SELECT a.* FROM");
        sql.append(" sale_order o,").append(" sale_order_address a").append(" WHERE o.customer_fk=?").append(" AND o.transferred=1").append(" AND a.`type`=2").append(" AND a.order_fk=o._id")
            .append(" ORDER BY o.cr_on DESC").append(" LIMIT 0,1");

        Connection conn = connection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            pstmt.setString(1, customerId.str());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                OrderAddress address = app.getModel(OrderAddress.class);

                address.setOrderId(Id.valueOf(rs.getString(OrderAddress.Column.ORDER_ID)));
                address.setFirstName(rs.getString(OrderAddress.Column.FIRST_NAME));
                address.setLastName(rs.getString(OrderAddress.Column.LAST_NAME));
                address.setAddress1(rs.getString(OrderAddress.Column.ADDRESS1));
                address.setAddress2(rs.getString(OrderAddress.Column.ADDRESS2));
                address.setHouseNumber(rs.getString(OrderAddress.Column.HOUSE_NUMBER));
                address.setCity(rs.getString(OrderAddress.Column.CITY));
                address.setZip(rs.getString(OrderAddress.Column.ZIP));
                address.setCountry(rs.getString(OrderAddress.Column.COUNTRY));
                address.setAddressType(AddressType.fromId(rs.getInt(OrderAddress.Column.TYPE)));

                return address;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(rs);
            IO.closeQuietly(pstmt);
        }

        return null;
    }
}
