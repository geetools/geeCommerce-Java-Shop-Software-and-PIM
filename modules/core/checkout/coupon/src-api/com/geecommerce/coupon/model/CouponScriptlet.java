package com.geecommerce.coupon.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CouponScriptlet extends Model {

    public Id getId();

    public CouponScriptlet setId(Id id);

    public String getCode();

    public CouponScriptlet setCode(String code);

    public ContextObject<String> getLabel();

    public CouponScriptlet setLabel(ContextObject<String> label);

    public String getBody();

    public CouponScriptlet setBody(String body);

    public boolean isValid();

    static final class Column {
	public static final String ID = "_id";
	public static final String CODE = "code";
	public static final String LABEL = "label";
	public static final String BODY = "body";
    }
}
