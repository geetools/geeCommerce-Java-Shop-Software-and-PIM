package com.geecommerce.core.system.attribute.model;

import java.util.Map;

import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model
public class DefaultAttributeGroupMapping extends AbstractModel implements AttributeGroupMapping {
    private static final long serialVersionUID = 5996844565715870568L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.TYPE)
    private AttributeGroupMappingType type;

    public DefaultAttributeGroupMapping() {
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AttributeGroupMapping setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public AttributeGroupMappingType getType() {
        return type;
    }

    @Override
    public AttributeGroupMapping setType(AttributeGroupMappingType type) {
        this.type = type;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public AttributeGroupMapping setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.position = int_(map.get(Col.POSITION));
        this.type = enum_(AttributeGroupMappingType.class, map.get(Col.TYPE));

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());
        map.put(Col.ID, getId());
        map.put(Col.POSITION, getPosition());
        map.put(Col.TYPE, getType().toId());
        return map;
    }
}
