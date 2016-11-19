package com.geecommerce.core.system.cpanel.attribute.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface AttributeTabMapping extends Model {
    public Id getId();

    public AttributeTabMapping setId(Id id);

    public Id getTabId();

    public AttributeTabMapping setTabId(Id tabId);

    public Id getAttributeId();

    public AttributeTabMapping setAttributeId(Id attributeId);

    public int getPosition();

    public AttributeTabMapping setPosition(int position);

    static final class Col {
	public static final String ID = "_id";
	public static final String TAB_ID = "tab_id";
	public static final String ATTRIBUTE_ID = "attr_id";
	public static final String POSITION = "pos";
    }
}
