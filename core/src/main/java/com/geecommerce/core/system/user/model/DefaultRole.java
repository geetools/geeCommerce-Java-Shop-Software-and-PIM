package com.geecommerce.core.system.user.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.user.repository.Permissions;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("_roles")
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultRole extends AbstractModel implements Role {
    private static final long serialVersionUID = -2596280772466416633L;

    @XmlElement
    private Id id = null;

    @XmlElement
    private String code = null;

    @XmlElement
    private ContextObject<String> name = null;

    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    private List<Id> permissionIds = new ArrayList<>();

    // Lazy loaded list of permissions.
    private List<Permission> permissionsList = null;

    // Repository for lazy loading permissions.
    private final Permissions permissions;

    private DefaultRole() {
	this(i(Permissions.class));
    }

    @Inject
    private DefaultRole(Permissions permissions) {
	this.permissions = permissions;
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public Role setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public Role setCode(String code) {
	this.code = code;
	return this;
    }

    @Override
    public ContextObject<String> getName() {
	return name;
    }

    @Override
    public Role setName(ContextObject<String> name) {
	this.name = name;
	return this;
    }

    @Override
    public List<Permission> getPermissions() {
	if (permissionsList == null && permissionIds != null && permissionIds.size() > 0) {
	    permissionsList = permissions.findByIds(Permission.class, permissionIds.toArray(new Id[permissionIds.size()]));
	}

	return permissionsList;
    }

    @Override
    public List<Id> getPermissionIds() {
	return permissionIds;
    }

    @Override
    public Role setPermissionIds(List<Id> permissionIds) {
	this.permissionIds = permissionIds;
	return this;
    }

    @Override
    public Role addPermission(Permission permission) {
	if (permission == null)
	    return this;

	if (!permissionIds.contains(permission.getId())) {
	    permissionIds.add(permission.getId());

	    permissionsList = null;
	}

	return this;
    }

    @Override
    public Role removePermission(Id permissionId) {
	if (permissionId == null || permissionIds == null || permissionIds.size() == 0)
	    return this;

	if (permissionIds.contains(permissionId)) {
	    permissionIds.remove(permissionId);

	    permissionsList = null;
	}

	return this;
    }

    @Override
    public Role removePermission(Permission permission) {
	if (permission == null || permission.getId() == null || permissionIds == null || permissionIds.size() == 0)
	    return this;

	if (permissionIds.contains(permission.getId())) {
	    permissionIds.remove(permission.getId());

	    permissionsList = null;
	}

	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.code = str_(map.get(Column.CODE));
	this.name = ctxObj_(map.get(Column.NAME));
	this.permissionIds = idList_(map.get(Column.PERMISSONS));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());

	m.put(Column.ID, getId());
	m.put(Column.CODE, getCode());
	m.put(Column.NAME, getName());
	m.put(Column.PERMISSONS, getPermissionIds());

	return m;
    }

    @Override
    public String toString() {
	return "DefaultRole [id=" + id + ", code=" + code + ", name=" + name + ", permissionIds=" + permissionIds + ", permissions=" + permissions + "]";
    }
}
