package com.geecommerce.coupon.model;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.Map;

@Model("coupon_operators")
public class DefaultCouponFilterOperator extends AbstractModel implements CouponFilterOperator {

    private Id id;
    private ContextObject<String> name;
    private String operator;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public CouponFilterOperator setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public ContextObject<String> getName() {
	return name;
    }

    @Override
    public CouponFilterOperator setName(ContextObject<String> name) {
	this.name = name;
	return this;
    }

    @Override
    public String getOperator() {
	return operator;
    }

    @Override
    public CouponFilterOperator setOperator(String operator) {
	this.operator = operator;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	this.id = id_(map.get(Column.ID));
	this.name = ctxObj_(map.get(Column.NAME));
	this.operator = str_(map.get(Column.OPERATOR));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
	m.put(Column.ID, getId());
	m.put(Column.OPERATOR, getOperator());
	m.put(Column.NAME, getName());

	return m;
    }
}
