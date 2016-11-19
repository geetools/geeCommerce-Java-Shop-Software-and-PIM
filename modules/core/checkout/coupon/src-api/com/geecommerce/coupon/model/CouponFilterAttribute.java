package com.geecommerce.coupon.model;

import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.core.service.api.Model;

public interface CouponFilterAttribute extends Model {

    public CouponFilterAttribute setId(Id id);

    public CouponFilterAttributeType getType();

    public CouponFilterAttribute setType(CouponFilterAttributeType type);

    public String getCode();

    public CouponFilterAttribute setCode(String code);

    public ContextObject<String> getName();

    public CouponFilterAttribute setName(ContextObject<String> name);

    public String getExpression();

    public CouponFilterAttribute setExpression(String expression);

    static final class Column {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String CODE = "code";
	public static final String TYPE = "type";
	public static final String EXPRESSION = "expr";
    }
}
