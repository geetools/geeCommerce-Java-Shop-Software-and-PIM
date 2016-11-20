package com.geecommerce.price.model;

import java.util.Date;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface Price extends Model {
    public Id getId();

    public Price setId(Id id);

    public String getId2();

    public Price setId2(String id2);

    public Id getProductId();

    public Price setProductId(Id productId);

    public String getCountry();

    public Price setCountry(String country);

    public String getCurrency();

    public Price setCurrency(String currency);

    public Id getStoreId();

    public Price setStoreId(Id storeId);

    public Id getCustomerId();

    public Price setCustomerId(Id customerId);

    public Id getCustomerGroupId();

    public Price setCustomerGroupId(Id customerGroupId);

    public Integer getQtyFrom();

    public Price setQtyFrom(Integer qtyFrom);

    public Id getTypeId();

    public Price setTypeId(Id typeId);

    public PriceType getPriceType();

    public Id getTypeObjId();

    public Price setTypeObjId(Id typeObjId);

    public Double getPrice();

    public Price setPrice(Double price);

    public Date getValidFrom();

    public Price setValidFrom(Date validFrom);

    public Date getValidTo();

    public Price setValidTo(Date validTo);

    public Double getFinalPrice();

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String PRODUCT_ID = "prd_id";
        public static final String STORE_ID = "store_id";
        public static final String CUSTOMER_ID = "cust_id";
        public static final String CUSTOMER_GROUP_ID = "cust_grp_id";
        public static final String CURRENCY = "currency";
        public static final String COUNTRY = "country";
        public static final String QTY_FROM = "qty_from";
        public static final String TYPE_ID = "type_id";
        public static final String TYPE_OBJECT_ID = "type_obj_id";
        public static final String PRICE = "price";
        public static final String VALID_FROM = "valid_from";
        public static final String VALID_TO = "valid_to";
    }
}
