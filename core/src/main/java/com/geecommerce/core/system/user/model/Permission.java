package com.geecommerce.core.system.user.model;

import java.util.List;

import com.geecommerce.core.enums.PermissionAction;
import com.geecommerce.core.enums.PermissionType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface Permission extends Model {
    public Id getId();

    public Permission setId(Id id);

    public String getCode();

    public Permission setCode(String code);

    public ContextObject<String> getName();

    public Permission setName(ContextObject<String> name);

    public PermissionType getType();

    public Permission setType(PermissionType type);

    public List<PermissionAction> getActions();

    public Permission setActions(List<PermissionAction> actions);

    public String getRule();

    public Permission setRule(String rule);

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String ACTIONS = "actions";
        public static final String RULE = "rule";
    }
}
