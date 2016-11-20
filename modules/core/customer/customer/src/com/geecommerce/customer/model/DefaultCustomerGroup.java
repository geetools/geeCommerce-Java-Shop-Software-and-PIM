package com.geecommerce.customer.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customerGroups")
@Model(collection = "_customer_groups", fieldAccess = true)
public class DefaultCustomerGroup extends AbstractMultiContextModel implements CustomerGroup {
    private static final long serialVersionUID = 90399598657590579L;
    private Id id = null;
    private String code = null;
    private ContextObject<String> label = null;
    private int position = 0;
    private boolean enabled = false;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CustomerGroup setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public CustomerGroup setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public CustomerGroup setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public CustomerGroup setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.code = str_(map.get(Col.CODE));
        this.label = ctxObj_(map.get(Col.LABEL));
        this.position = int_(map.get(Col.POSITION), 0);
        this.enabled = bool_(map.get(Col.ENABLED), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
        m.put(Col.ID, getId());
        m.put(Col.CODE, getCode());
        m.put(Col.LABEL, getLabel());
        m.put(Col.POSITION, getPosition());
        m.put(Col.ENABLED, isEnabled());

        return m;
    }
}
