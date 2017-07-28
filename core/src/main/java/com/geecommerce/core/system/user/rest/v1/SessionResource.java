package com.geecommerce.core.system.user.rest.v1;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.ResponseWrapper;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/sessions")
public class SessionResource extends AbstractResource {
    private final UserService userService;

    @Inject
    public SessionResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response currentSession() {
        HttpServletRequest request = app.servletRequest();
        boolean isAuthorizationEnabled = app.cpBool_("core/cpanel/authorization/enabled", false);

        if (!isAuthorizationEnabled) {
            Set<String> roleCodes = new LinkedHashSet<>();
            roleCodes.add("admin");

            return created(ResponseWrapper.builder().set("name", "Administrator").set("roles", roleCodes).build());
        }

        Subject currentUser = SecurityUtils.getSubject();

        if (currentUser != null && currentUser.isAuthenticated()) {
            Id userId = (Id) currentUser.getPrincipal();

            User user = userService.getUserForRealm(userId);

            if (user != null) {
                Set<String> roleCodes = new LinkedHashSet<>();
                List<Role> roles = user.getRoles();

                for (Role role : roles) {
                    System.out.println("Adding role: " + role);

                    roleCodes.add(role.getCode());
                }

                HttpSession session = request.getSession(false);

                long currentTime = System.currentTimeMillis();
                long sessionTimesOutAt = session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000);
                long sessionTimesOutIn = sessionTimesOutAt - currentTime;

                return created(ResponseWrapper.builder().set("name", user.getForename() + " " + user.getSurname())
                    .set("roles", roleCodes).set("currentDate", new Date(currentTime))
                    .set("currentTimeMillis", currentTime).set("timeoutAtDate", new Date(sessionTimesOutAt))
                    .set("timeoutAtMillis", sessionTimesOutAt).set("timeoutInMillis", sessionTimesOutIn).build());
            } else {
                return notFound();
            }
        } else {
            return notFound();
        }
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response deleteSession() {
        Subject currentUser = SecurityUtils.getSubject();

        if (currentUser != null && currentUser.isAuthenticated()) {
            System.out.println("LOGOUT: Logging out: " + currentUser.getPrincipal());
            currentUser.logout();
            System.out.println("LOGOUT: Logged out: " + currentUser.getPrincipal());
            return deleted();
        } else {
            System.out.println("LOGOUT: Not found: " + currentUser);
            return deleted();
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createSession(@FormParam("username") String email, @FormParam("password") String password) {
        try {
            System.out.println("username=" + email + ", password=" + password);
            
            HttpServletRequest request = app.servletRequest();
            request.setAttribute(DefaultSubjectContext.SESSION_CREATION_ENABLED, Boolean.TRUE);

            UsernamePasswordToken userPassToken = new UsernamePasswordToken(email, password);
            userPassToken.setRememberMe(true);

            Subject subject = SecurityUtils.getSubject();
            subject.login(userPassToken);

            User user = null;

            if (subject.isAuthenticated()) {
                Id userId = (Id) subject.getPrincipal();

                user = userService.getUserForRealm(userId);

                initClientSession(user);
            }

            Set<String> roleCodes = new LinkedHashSet<>();
            List<Role> roles = user.getRoles();

            for (Role role : roles) {
                roleCodes.add(role.getCode());
            }

            HttpSession session = request.getSession(false);

            long currentTime = System.currentTimeMillis();
            long sessionTimesOutAt = session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000);
            long sessionTimesOutIn = sessionTimesOutAt - currentTime;

            return created(ResponseWrapper.builder().set("name", user.getForename() + " " + user.getSurname())
                .set("roles", roleCodes).set("currentDate", new Date(currentTime))
                .set("currentTimeMillis", currentTime).set("timeoutAtDate", new Date(sessionTimesOutAt))
                .set("timeoutAtMillis", sessionTimesOutAt).set("timeoutInMillis", sessionTimesOutIn).build());
        } catch (AuthenticationException e) {
            return response(Status.UNAUTHORIZED, "Wrong credentials");
        }
    }
}
