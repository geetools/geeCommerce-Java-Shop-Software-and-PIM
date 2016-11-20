package com.geecommerce.core.system.user.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface Role extends Model {
    public Id getId();

    public Role setId(Id id);

    public String getCode();

    public Role setCode(String code);

    public ContextObject<String> getName();

    public Role setName(ContextObject<String> name);

    @JsonIgnore
    public List<Permission> getPermissions();

    public List<Id> getPermissionIds();

    public Role setPermissionIds(List<Id> permissionIds);

    public Role addPermission(Permission permission);

    public Role removePermission(Id permissionId);

    public Role removePermission(Permission permission);

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String PERMISSONS = "permissions";
    }
}
