package com.geecommerce.core.system.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable(repository = true)
@Model(collection = "context_messages", readCount = true, optimisticLocking = true)
public class DefaultContextMessage extends AbstractMultiContextModel implements ContextMessage {
    private static final long serialVersionUID = -8289471121468282461L;
    private Id id = null;
    private String key = null;
    private ContextObject<String> value = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ContextMessage setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ContextMessage setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getValue() {
        return value;
    }

    @Override
    public ContextMessage setValue(ContextObject<String> value) {
        this.value = value;
        return this;
    }

    @Override
    public String getMessage() {
        return value.getString();
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null || map.size() == 0)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.key = str_(map.get(Column.KEY));
        this.value = ctxObj_(map.get(Column.VALUE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.KEY, getKey());
        map.put(Column.VALUE, getValue());

        return map;
    }
}
