package com.geecommerce.core.security;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.PermissionType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.AbstractWebResource;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.pojo.ClientSession;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.ProductIdSupport;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.sun.jersey.api.client.ClientResponse.Status;

public class PermissionInterceptor implements MethodInterceptor {
    private static final String KEY_AUTHENTICATED_CLIENT = "authenticated.client";
    private static final String IGNORE_REALM_METHOD = "getUserForRealm";
    private static final List<String> IGNORE_CALLERS = new ArrayList<>();
    static {
        // IGNORE_CALLERS.add("DefaultUserService.getUserForRealm");
        // IGNORE_CALLERS.add("PermissionInterceptor");
        // IGNORE_CALLERS.add("com.geecommerce.core.security");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        App app = App.get();
        HttpServletRequest request = app.servletRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        boolean isAuthorizationEnabled = app.cpBool_("core/cpanel/authorization/enabled", false);

        if (!isAuthorizationEnabled) {
            return invocation.proceed();
        }

        Subject currentUser = SecurityUtils.getSubject();

        if ("public-web-user".equals(request.getHeader("X-Requested-By")) && uri.startsWith("/api/v1/web/")) {
            // Web user can only access web-resource.
            if (AbstractWebResource.class.isAssignableFrom(invocation.getThis().getClass())) {
                if (currentUser == null || !currentUser.isAuthenticated())
                    createWebSession();

                return invocation.proceed();
            }
        }

        if(currentUser.isAuthenticated()) {
            Id userId = (Id) currentUser.getPrincipal();
            User user = app.service(UserService.class).getUserForRealm(userId);

            // Web-User attempting to access admin-functionality.
            if (user != null && DefaultCredentialsMatcher.DEFAULT_WEB_USERNAME.equals(user.getUsername()) && uri.startsWith("/api/") && !uri.startsWith("/api/v1/web/")) {
                System.out.println("The current session (web-user) is not valid for the admin panel. Logging web-user out. Consider using a different browser when accessing the admin panel.");
                currentUser.logout();
            }            
        }

        if ((("GET".equals(method) || "POST".equals(method)) && uri.matches("^\\/api\\/v[0-9]+\\/sessions[\\/]?$"))
            || uri.matches("^\\/api\\/swagger.+")
            || ("GET".equals(method) && (uri.matches("^\\/api\\/v[0-9]+\\/settings[\\/]?$")
                || uri.matches("^\\/api\\/v[0-9]+\\/attributes[\\/]?$")
                || uri.matches("^\\/api\\/v[0-9]+\\/control\\-panels\\/[0-9]+\\/attribute\\-tabs[\\/]?$")
                || uri.matches(
                    "^\\/api\\/v[0-9]+\\/control\\-panels\\/[0-9]+\\/attribute\\-tabs\\/attributes[\\/]?$")
                || uri.matches("^\\/api\\/v[0-9]+\\/attributes/input\\-conditions[\\/]?$")
                || uri.matches("^\\/api\\/v[0-9]+\\/attribute-target-objects[\\/]?$")
                || uri.matches("^\\/api\\/v[0-9]+\\/products/media-types[\\/]?$")))) {
            // System.out.println("URI IS ALLOWED WITHOUT LOGIN: " + uri + " - "
            // + method);
            return invocation.proceed();
        } else if (currentUser == null || !currentUser.isAuthenticated()) {
            // return
            // Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
            throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            AbstractResource resource = (AbstractResource) invocation.getThis();

            ClassLoader classLoader = resource.getClass().getClassLoader();

            if (classLoader instanceof ModuleClassLoader) {
                ModuleClassLoader mcl = (ModuleClassLoader) classLoader;
                Module m = mcl.getModule();

                String vendor = m.getVendor();
                String name = m.getName();

                String neededModulePermission = new StringBuilder("module:use").append(Char.COLON)
                    .append(vendor.toLowerCase()).append(Char.COLON)
                    .append(name.toLowerCase().replace(Char.SPACE, Char.MINUS)).toString();

                // System.out.println("NEEDED MODULE PERMISSION: " +
                // neededModulePermission.toString());

                boolean isPermitted = SecurityUtils.getSecurityManager().isPermitted(currentUser.getPrincipals(),
                    neededModulePermission);

                if (isPermitted) {
                    return invocation.proceed();
                } else {
                    System.out.println(neededModulePermission + "  ==>  " + isPermitted);

                    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
                }
            }
            // Core classes
            else {
                String permissionURI = uri.replaceFirst("^\\/api\\/v[0-9]+\\/", Str.COLON).replace(Char.SLASH,
                    Char.COLON);

                String neededModulePermission = new StringBuilder("uri:").append(method.toLowerCase())
                    .append(permissionURI).toString();

                // System.out.println("NEEDED URI PERMISSION: " +
                // neededModulePermission.toString());

                boolean isPermitted = SecurityUtils.getSecurityManager().isPermitted(currentUser.getPrincipals(),
                    neededModulePermission);

                if (isPermitted) {
                    return invocation.proceed();
                } else {
                    System.out.println(neededModulePermission + "  ==>  " + isPermitted);

                    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
                }
            }
        }

        // Class<?> clazz = invocation.getMethod().getDeclaringClass();
        //
        // if (Object.class.equals(clazz))
        // return invocation.proceed();
        //
        // if (IGNORE_REALM_METHOD.equals(invocation.getMethod().getName()))
        // {
        // System.out.println("IGNORING METHOD: " +
        // invocation.getMethod().getName());
        // return invocation.proceed();
        // }
        //
        // StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        //
        // System.out.println(invocation.getMethod().getDeclaringClass() + " - "
        // + invocation.getMethod().getName());
        //
        // if (isInIgnoreList(trace))
        // return invocation.proceed();
        //
        // // System.out.println(Arrays.asList(trace));
        //
        // String permissionString = toPermission(invocation);
        //
        // Subject currentUser = SecurityUtils.getSubject();
        //
        // boolean isPermitted =
        // SecurityUtils.getSecurityManager().isPermitted(currentUser.getPrincipals(),
        // permissionString);
        //
        // System.out.println(permissionString + " ==> " + isPermitted);
    }

    public void createWebSession() {
        try {
            App app = App.get();
            HttpServletRequest request = app.servletRequest();
            request.setAttribute(DefaultSubjectContext.SESSION_CREATION_ENABLED, Boolean.TRUE);

            UsernamePasswordToken userPassToken = new UsernamePasswordToken(
                DefaultCredentialsMatcher.DEFAULT_WEB_USERNAME, (String) null);

            Subject subject = SecurityUtils.getSubject();
            subject.login(userPassToken);

            User user = null;

            if (subject.isAuthenticated()) {
                Id userId = (Id) subject.getPrincipal();

                user = app.service(UserService.class).getUserForRealm(userId);

                subject = SecurityUtils.getSubject();

                if (subject.isAuthenticated()) {

                    ClientSession clientSession = new ClientSession(user.getUsername(),
                        user.getForename() + " " + user.getSurname(), user.getScopeIds());

                    System.out.println("CREATING WEB SESSION ***** ClientSession: " + clientSession);

                    Session sess = subject.getSession(true);

                    System.out.println("CREATING WEB SESSION ***** Session: " + sess.getId());

                    sess.setAttribute(KEY_AUTHENTICATED_CLIENT, clientSession);
                }
            }

            Set<String> roleCodes = new LinkedHashSet<>();
            List<Role> roles = user.getRoles();

            for (Role role : roles) {
                roleCodes.add(role.getCode());
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    private boolean isInIgnoreList(StackTraceElement[] trace) {
        if (IGNORE_CALLERS.size() == 0)
            return false;

        int x = 0;
        for (StackTraceElement ste : trace) {
            for (String callerToIgnore : IGNORE_CALLERS) {
                if (ste.getClassName().indexOf(callerToIgnore) != -1) {
                    System.out.println("IGNORING: " + ste.getClassName() + " - " + x + "/" + trace.length);

                    return true;
                }
            }

            x++;
        }

        return false;
    }

    private String toPermission(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getName();

        StringBuilder permission = new StringBuilder("api:").append(className.replaceAll("\\.", ":")).append(":")
            .append(method.getName());

        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypes != null && paramTypes.length > 0) {
            permission.append(":");

            int x = 0;
            for (Class<?> paramType : paramTypes) {
                if (x > 0)
                    permission.append(";");

                permission.append(toParamShortName(paramType));

                x++;
            }
        }

        permission.append(":view");

        return permission.toString();
    }

    private static String toParamShortName(Class<?> paramType) {
        String name = paramType.getSimpleName();

        StringBuilder shortName = new StringBuilder();

        if (paramType.isAnnotation()) {
            shortName.append("@");
        } else if (paramType.isEnum()) {
            shortName.append("E_");
        }

        if (name.length() > 2) {
            shortName.append(name.substring(0, 3));
        } else {
            shortName.append(name);
        }

        if (paramType.isArray()) {
            shortName.append("[]");
        }

        if (paramType.isPrimitive()) {
            return shortName.toString().toLowerCase();
        } else {
            return shortName.toString().toUpperCase();
        }
    }

    public static void main(String[] args) {
        System.out.println(toParamShortName(String.class));
        System.out.println(toParamShortName(ProductIdSupport.class));
        System.out.println(toParamShortName(Id.class));
        System.out.println(toParamShortName(Integer.class));
        System.out.println(toParamShortName(Long[].class));
        System.out.println(toParamShortName(List.class));
        System.out.println(toParamShortName(String[].class));
        System.out.println(toParamShortName(PermissionType.class));
        System.out.println(toParamShortName(Status.class));
        System.out.println(toParamShortName(int.class));
        System.out.println(toParamShortName(int[].class));
        System.out.println(toParamShortName(boolean.class));
        System.out.println(toParamShortName(Model.class));
        System.out.println(toParamShortName(Map.class));
    }
}
