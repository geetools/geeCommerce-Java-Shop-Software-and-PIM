package com.geecommerce.core.system.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable(repository = true)
@XmlRootElement(name = "configuration_definition")
@Model(collection = "configuration_definitions", readCount = true, optimisticLocking = true)
public class DefaultConfigurationDefinition extends AbstractMultiContextModel implements ConfigurationDefinition {
    private static final long serialVersionUID = 2012276802129778322L;
    protected Id id = null;
    protected BackendType backendType = null;
    protected ContextObject<String> label = null;
    protected ContextObject<?> options = null;
    protected Object defaultValue = null;
    protected Id groupId = null;
    protected List<Id> editRoles = null;
    protected boolean optional = true;
    protected boolean visibleInAdmin = true;
    protected boolean editable = true;
    protected boolean system = false;
    protected FrontendInput frontendInput = null;

    public DefaultConfigurationDefinition() {
        super();
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ConfigurationDefinition setId(Id id) {
        this.id = id;
        return this;
    }

    public BackendType getBackendType() {
        return backendType;
    }

    public ConfigurationDefinition setBackendType(BackendType backendType) {
        this.backendType = backendType;
        return this;
    }

    public ContextObject<String> getLabel() {
        return label;
    }

    public ConfigurationDefinition setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    public ContextObject<?> getOptions() {
        return options;
    }

    public ConfigurationDefinition setOptions(ContextObject<?> options) {
        this.options = options;
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public ConfigurationDefinition setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Id getGroupId() {
        return groupId;
    }

    public ConfigurationDefinition setGroupId(Id groupId) {
        this.groupId = groupId;
        return this;
    }

    public List<Id> getEditRoles() {
        return editRoles;
    }

    public ConfigurationDefinition setEditRoles(List<Id> editRoles) {
        this.editRoles = editRoles;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    public ConfigurationDefinition setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean isVisibleInAdmin() {
        return visibleInAdmin;
    }

    public ConfigurationDefinition setVisibleInAdmin(boolean visibleInAdmin) {
        this.visibleInAdmin = visibleInAdmin;
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public ConfigurationDefinition setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public boolean isSystem() {
        return system;
    }

    public ConfigurationDefinition setSystem(boolean system) {
        this.system = system;
        return this;
    }

    public FrontendInput getFrontendInput() {
        return frontendInput;
    }

    public ConfigurationDefinition setFrontendInput(FrontendInput frontendInput) {
        this.frontendInput = frontendInput;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null || map.size() == 0)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.backendType = null;
        this.label = null;
        this.options = null;
        this.defaultValue = null;
        this.groupId = null;
        this.editRoles = null;
        this.optional = true;
        this.visibleInAdmin = true;
        this.editable = true;
        this.system = false;
        this.frontendInput = null;

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());

        return map;
    }
}
