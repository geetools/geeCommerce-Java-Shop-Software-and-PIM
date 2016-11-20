package com.geecommerce.core.system.user.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.type.Id;

public interface Roles extends Repository {
    List<Role> findExceptIds(List<Id> ids);
}
