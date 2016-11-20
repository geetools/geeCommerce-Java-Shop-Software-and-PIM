package com.geecommerce.core.system.user.service;

import java.util.List;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.user.model.Permission;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.repository.Permissions;
import com.geecommerce.core.system.user.repository.Roles;
import com.geecommerce.core.system.user.repository.Users;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Service
public class DefaultUserService implements UserService {
    private final Users users;
    private final Roles roles;
    private final Permissions permissions;

    @Inject
    public DefaultUserService(Users users, Roles roles, Permissions permissions) {
        this.users = users;
        this.roles = roles;
        this.permissions = permissions;
    }

    // -------------------------------------------------------------
    // User
    // -------------------------------------------------------------

    @Override
    public List<User> getUsers() {
        return users.findAll(User.class);
    }

    @Override
    public User getUserForRealm(Id id) {
        if (id == null)
            return null;

        return users.findById(User.class, id);
    }

    @Override
    public User getUserForRealm(String username) {
        if (username == null)
            return null;

        return users.havingUsername(username);
    }

    @Override
    public User getUser(Id id) {
        if (id == null)
            return null;

        return users.findById(User.class, id);
    }

    @Override
    public User getUser(String username) {
        if (username == null)
            return null;

        return users.havingUsername(username);
    }

    @Override
    public User createUser(User user) {
        if (user == null)
            return null;

        return users.add(user);
    }

    @Override
    public void updateUser(User user) {
        if (user == null)
            return;

        users.update(user);
    }

    // -------------------------------------------------------------
    // Role
    // -------------------------------------------------------------

    @Override
    public List<Role> getRoles() {
        return roles.findAll(Role.class);
    }

    @Override
    public Role getRole(Id id) {
        if (id == null)
            return null;

        return roles.findById(Role.class, id);
    }

    @Override
    public Role createRole(Role role) {
        if (role == null)
            return null;

        return roles.add(role);
    }

    @Override
    public void updateRole(Role role) {
        if (role == null)
            return;

        roles.update(role);
    }

    // -------------------------------------------------------------
    // Permission
    // -------------------------------------------------------------

    @Override
    public List<Permission> getPermissions() {
        return permissions.findAll(Permission.class);
    }

    @Override
    public Permission getPermission(Id id) {
        if (id == null)
            return null;

        return permissions.findById(Permission.class, id);
    }

    @Override
    public Permission getPermission(String code) {
        if (code == null)
            return null;

        return permissions.havingCode(code);
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (permission == null)
            return null;

        return permissions.add(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        if (permission == null)
            return;

        permissions.update(permission);
    }
}
