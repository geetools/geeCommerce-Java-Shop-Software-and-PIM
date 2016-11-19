package com.geecommerce.core.system.user.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.user.model.Permission;

public interface Permissions extends Repository {
    public Permission havingCode(String code);
}
