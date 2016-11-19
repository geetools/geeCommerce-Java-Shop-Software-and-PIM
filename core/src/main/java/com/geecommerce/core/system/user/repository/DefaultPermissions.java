package com.geecommerce.core.system.user.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.system.user.model.Permission;

public class DefaultPermissions extends AbstractRepository implements Permissions {
    @Override
    public Permission havingCode(String code) {
        if (code == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(Permission.Column.CODE, code);

        return findOne(Permission.class, filter);
    }
}
