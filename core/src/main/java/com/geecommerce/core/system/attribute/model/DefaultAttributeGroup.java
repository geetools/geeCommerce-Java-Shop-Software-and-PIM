package com.geecommerce.core.system.attribute.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable
@Model(collection = "attribute_groups")
public class DefaultAttributeGroup extends AbstractMultiContextModel implements AttributeGroup {

    private static final long serialVersionUID = -4134855355218367102L;
    @Column(Col.ID)
    protected Id id = null;
    @Column(Attribute.Col.CODE)
    protected String code = null;
    @Column(Col.LABEL)
    protected ContextObject<String> label = null;
    @Column(Col.ATTRIBUTES)
    protected List<Id> attributeIds = null;
    @Column(Col.POSITION)
    protected Integer position = null;
    @Column(Col.COLUMN)
    protected Integer column = null;
    @Column(Col.TARGET_OBJECT_ID)
    protected Id targetObjectId = null;

    @Column(name = Col.ITEMS, autoPopulate = false)
    protected List<AttributeGroupMapping> items = new ArrayList<>();;

    @Override
    public AttributeGroup setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public AttributeGroup setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public AttributeGroup setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public List<Id> getAttributeIds() {
        return attributeIds;
    }

    @Override
    public AttributeGroup setAttributeIds(List<Id> attributeIds) {
        this.attributeIds = attributeIds;
        return this;
    }

    @Override
    public int getPosition() {
        if (position == null)
            position = 999;
        return position;
    }

    @Override
    public AttributeGroup setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public int getColumn() {
        if (column == null)
            column = 1;
        return column;
    }

    @Override
    public AttributeGroup setColumn(int column) {
        this.column = column;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public AttributeGroup setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public List<AttributeGroupMapping> getItems() {
        return items;
    }

    @Override
    public AttributeGroup addItem(Id id, AttributeGroupMappingType type) {
        AttributeGroupMapping mapping = app.model(AttributeGroupMapping.class);
        mapping.setId(id);
        mapping.setType(type);
        this.items.add(mapping);
        return this;
    }

    @Override
    public AttributeGroup removeItem(Id id) {
        items.removeIf(s -> s.getId().equals(id));
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.attributeIds = idList_(map.get(Col.ATTRIBUTES));

        List<Map<String, Object>> itemsList = list_(map.get(Col.ITEMS));
        if (itemsList != null && itemsList.size() > 0) {
            this.items = new ArrayList<>();
            for (Map<String, Object> item : itemsList) {
                AttributeGroupMapping mapping = app.model(AttributeGroupMapping.class);
                mapping.fromMap(item);
                this.items.add(mapping);
            }
        } else {
            if (this.attributeIds != null && this.attributeIds.size() > 0) {
                int index = 0;
                for (Id attrId : this.attributeIds) {
                    AttributeGroupMapping mapping = app.model(AttributeGroupMapping.class);
                    mapping.setId(attrId);
                    mapping.setType(AttributeGroupMappingType.ATTRIBUTE);
                    mapping.setPosition(index);
                    this.items.add(mapping);
                    index++;
                }

            }
        }

        Collections.sort(this.items, new Comparator<AttributeGroupMapping>() {
            @Override
            public int compare(AttributeGroupMapping agm1, AttributeGroupMapping agm2) {
                return (agm1.getPosition() < agm2.getPosition() ? -1
                    : (agm1.getPosition() > agm2.getPosition() ? 1 : 0));
            }
        });
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (getItems() != null) {
            for (AttributeGroupMapping item : getItems()) {
                itemsList.add(item.toMap());
            }
            map.put(Col.ITEMS, itemsList);
        }

        return map;
    }
}
