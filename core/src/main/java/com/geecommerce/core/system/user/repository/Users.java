package com.geecommerce.core.system.user.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.user.model.User;

public interface Users extends Repository {
    public User havingEmail(String email);

    public User havingUsername(String username);
}
