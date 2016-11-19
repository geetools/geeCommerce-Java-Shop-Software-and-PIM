package com.geecommerce.core.system.user.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.geecommerce.core.enums.PermissionAction;
import com.geecommerce.core.enums.PermissionType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("_permissions")
@XmlRootElement(name = "permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultPermission extends AbstractModel implements Permission {
    private static final long serialVersionUID = -2596280772466416633L;

    private Id id = null;

    private String code = null;

    private ContextObject<String> name = null;

    private PermissionType type = null;

    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<PermissionAction> actions = null;

    private String rule = null;

    public Id getId() {
	return id;
    }

    public Permission setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public Permission setCode(String code) {
	this.code = code;
	return this;
    }

    public ContextObject<String> getName() {
	return name;
    }

    public Permission setName(ContextObject<String> name) {
	this.name = name;
	return this;
    }

    public PermissionType getType() {
	return type;
    }

    public Permission setType(PermissionType type) {
	this.type = type;
	return this;
    }

    public List<PermissionAction> getActions() {
	return actions;
    }

    public Permission setActions(List<PermissionAction> actions) {
	this.actions = actions;
	return this;
    }

    public String getRule() {
	return rule;
    }

    public Permission setRule(String rule) {
	this.rule = rule;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.code = str_(map.get(Column.CODE));
	this.name = ctxObj_(map.get(Column.NAME));
	this.type = enum_(PermissionType.class, map.get(Column.TYPE));
	this.actions = enumList_(PermissionAction.class, map.get(Column.ACTIONS));
	this.rule = str_(map.get(Column.RULE));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());

	m.put(Column.ID, getId());
	m.put(Column.CODE, getCode());
	m.put(Column.NAME, getName());
	m.put(Column.TYPE, getType());
	m.put(Column.ACTIONS, getActions());
	m.put(Column.RULE, getRule());

	return m;
    }
}
