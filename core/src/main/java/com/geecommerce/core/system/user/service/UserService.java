package com.geecommerce.core.system.user.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.user.model.Permission;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;

public interface UserService extends Service {
    public List<User> getUsers();

    public User getUserForRealm(Id id);

    public User getUserForRealm(String username);

    public User getUser(Id id);

    public User getUser(String username);

    public User createUser(User user);

    public void updateUser(User user);

    public List<Role> getRoles();

    public Role getRole(Id id);

    public Role createRole(Role role);

    public void updateRole(Role role);

    public List<Permission> getPermissions();

    public Permission getPermission(Id id);

    public Permission getPermission(String code);

    public Permission createPermission(Permission permission);

    public void updatePermission(Permission permission);
}
