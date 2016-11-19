package com.geecommerce.core.system.attribute.model;

import java.util.List;

import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AttributeGroup extends MultiContextModel {

    public Id getId();

    public AttributeGroup setId(Id id);

    public String getCode();

    public AttributeGroup setCode(String code);

    public ContextObject<String> getLabel();

    public AttributeGroup setLabel(ContextObject<String> label);

    public List<Id> getAttributeIds();

    public AttributeGroup setAttributeIds(List<Id> attributeIds);

    public int getPosition();

    public AttributeGroup setPosition(int position);

    public int getColumn();

    public AttributeGroup setColumn(int column);

    public Id getTargetObjectId();

    public AttributeGroup setTargetObjectId(Id targetObjectId);

    public List<AttributeGroupMapping> getItems();

    public AttributeGroup addItem(Id id, AttributeGroupMappingType type);

    public AttributeGroup removeItem(Id id);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String ATTRIBUTES = "attrs";
        public static final String ITEMS = "items";
        public static final String POSITION = "pos";
        public static final String COLUMN = "col";
        public static final String TARGET_OBJECT_ID = "tar_obj";
    }
}
