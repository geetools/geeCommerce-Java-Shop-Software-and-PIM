package com.geecommerce.core.security;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.PermissionAction;
import com.geecommerce.core.system.user.model.Permission;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Strings;

/**
 * Administrator All (read-only) Product Manager Customer Service
 * 
 * :view (view all)
 * 
 * module:feature:view module:feature:create module:feature:update
 * module:feature:delete module:feature (manage feature) module (manage complete
 * module)
 * 
 * @author Michael
 * 
 */

public class DefaultRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null)
            throw new AuthorizationException("PrincipalCollection cannot be null");

        Id userId = (Id) principals.getPrimaryPrincipal();

        if (userId == null)
            throw new AuthorizationException("Failed to get userId out of principalCollection");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        UserService service = App.get().getService(UserService.class);
        User user = service.getUserForRealm(userId);

        List<Role> roles = user.getRoles();

        if (roles != null && roles.size() > 0) {
            for (Role role : roles) {
                info.addRole(role.getName().getString());

                List<Permission> permissions = role.getPermissions();

                if (permissions != null && permissions.size() > 0) {
                    for (Permission permission : permissions) {
                        String stringPermission = toStringPermission(permission);

                        // System.out.println("Adding string permission: " +
                        // stringPermission);

                        if (!Str.isEmpty(stringPermission))
                            info.addStringPermission(stringPermission);
                    }
                }
            }
        }

        return info;
    }

    private String toStringPermission(Permission permission) {
        if (permission == null || permission.getRule() == null)
            return null;

        List<PermissionAction> actions = permission.getActions();

        // Permission for super user - allows everything.
        if (Str.ASTERIX.equals(permission.getRule()) && permission.getType() == null && (actions == null || actions.isEmpty())) {
            return permission.getRule();
        }

        // Concatenate permission for rule using type and actions.
        StringBuilder builder = new StringBuilder(permission.getType().name().toLowerCase());

        if (actions != null && actions.size() > 0) {
            builder.append(":");

            int x = 0;
            for (PermissionAction action : actions) {
                if (x > 0)
                    builder.append(",");

                builder.append(action.name().toLowerCase());

                x++;
            }
        } else {
            builder.append(":*");
        }

        builder.append(":").append(permission.getRule());

        return builder.toString();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        UserService service = App.get().getService(UserService.class);
        User user = service.getUserForRealm(upToken.getUsername());

        if (user == null)
            throw new UnknownAccountException("User '" + Strings.maskEmail(upToken.getUsername()) + "' not found");

        return new SimpleAuthenticationInfo(user.getId(), user.getPassword(), new SimpleByteSource(user.getSalt()), getName());
    }
}
