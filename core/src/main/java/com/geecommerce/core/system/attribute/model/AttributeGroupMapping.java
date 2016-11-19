package com.geecommerce.core.system.attribute.model;

import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface AttributeGroupMapping extends Model {
    public Id getId();

    public AttributeGroupMapping setId(Id id);

    public AttributeGroupMappingType getType();

    public AttributeGroupMapping setType(AttributeGroupMappingType type);

    public int getPosition();

    public AttributeGroupMapping setPosition(int position);

    static final class Col {
        public static final String ID = "item_id";
        public static final String TYPE = "type";
        public static final String POSITION = "pos";
    }
}
