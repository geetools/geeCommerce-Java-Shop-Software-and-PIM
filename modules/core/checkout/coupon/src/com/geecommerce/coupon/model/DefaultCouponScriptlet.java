package com.geecommerce.coupon.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("coupon_scriptlets")
public class DefaultCouponScriptlet extends AbstractMultiContextModel implements CouponScriptlet {
    private Id id = null;
    private String code = null;
    private ContextObject<String> label = null;
    private String body = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public CouponScriptlet setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public CouponScriptlet setCode(String code) {
	this.code = code;
	return this;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public CouponScriptlet setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public String getBody() {
	return body;
    }

    @Override
    public CouponScriptlet setBody(String body) {
	this.body = body;
	return this;
    }

    @Override
    public boolean isValid() {
	return code != null && !"".equals(code.trim()) && label != null && body != null && !"".equals(body.trim());
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.code = str_(map.get(Column.CODE));
	this.label = ctxObj_(map.get(Column.LABEL));
	this.body = str_(map.get(Column.BODY));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());
	map.put(Column.CODE, getCode());
	map.put(Column.LABEL, getLabel());
	map.put(Column.BODY, getBody());

	return map;
    }
}
