package com.geecommerce.coupon.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;

@Cacheable
@Model("coupon_filter_attributes")
public class DefaultCouponFilterAttribute extends AbstractModel implements CouponFilterAttribute {

    private Id id;
    private ContextObject<String> name;
    private CouponFilterAttributeType type;
    private String code;
    private String expression;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public CouponFilterAttribute setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public CouponFilterAttributeType getType() {
	return type;
    }

    @Override
    public CouponFilterAttribute setType(CouponFilterAttributeType type) {
	this.type = type;
	return this;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public CouponFilterAttribute setCode(String code) {
	this.code = code;
	return this;
    }

    @Override
    public ContextObject<String> getName() {
	return name;
    }

    @Override
    public CouponFilterAttribute setName(ContextObject<String> name) {
	this.name = name;
	return this;
    }

    @Override
    public String getExpression() {
	return expression;
    }

    @Override
    public CouponFilterAttribute setExpression(String expression) {
	this.expression = expression;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	this.id = id_(map.get(Column.ID));
	this.name = ctxObj_(map.get(Column.NAME));
	this.code = str_(map.get(Column.CODE));
	this.expression = str_(map.get(Column.EXPRESSION));
	this.type = CouponFilterAttributeType.fromId(int_(map.get(Column.TYPE)));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
	m.put(Column.ID, getId());
	m.put(Column.CODE, getCode());
	m.put(Column.NAME, getName());
	m.put(Column.EXPRESSION, getExpression());
	m.put(Column.TYPE, getType().toId());

	return m;
    }

}
