package com.geecommerce.coupon.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CouponFilterOperator extends Model {

    public CouponFilterOperator setId(Id id);

    public ContextObject<String> getName();

    public CouponFilterOperator setName(ContextObject<String> name);

    public String getOperator();

    public CouponFilterOperator setOperator(String operator);

    static final class Column {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String OPERATOR = "operator";
    }
}
