package com.geecommerce.core.system.user.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.system.user.model.User;

public class DefaultUsers extends AbstractRepository implements Users {
    @Override
    public User havingEmail(String email) {
        if (email == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(User.Col.EMAIL, email);
        filter.put(User.Col.ENABLED, true);

        return findOne(User.class, filter);
    }

    @Override
    public User havingUsername(String username) {
        if (username == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(User.Col.USERNAME, username);

        return findOne(User.class, filter);
    }
}
