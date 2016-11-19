package com.geecommerce.customer.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CustomerGroup extends Model {
    public Id getId();

    public CustomerGroup setId(Id id);

    public String getCode();

    public CustomerGroup setCode(String code);

    public ContextObject<String> getLabel();

    public CustomerGroup setLabel(ContextObject<String> label);

    public int getPosition();

    public CustomerGroup setPosition(int position);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    static final class Col {
	public static final String ID = "_id";
	public static final String CODE = "code";
	public static final String LABEL = "label";
	public static final String POSITION = "pos";
	public static final String ENABLED = "enabled";
    }
}
