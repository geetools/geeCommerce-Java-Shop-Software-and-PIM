package com.geecommerce.core.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.template.Templates;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Requests;
import com.geemvc.Results;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

public class BaseController {
    @Inject
    protected App app;

    public HttpServletRequest getRequest() {
        return app.servletRequest();
    }

    public HttpServletResponse getResponse() {
        return app.servletResponse();
    }

    public String getOriginalURI() {
        return app.getOriginalURI();
    }

    public String getOriginalQueryString() {
        return app.getOriginalQueryString();
    }

    public String getBaseTemplatesPath() {
        return Templates.getBaseTemplatesPath();
    }

    public final String getPagesPath() {
        return Templates.getPagesPath();
    }

    public final String getSlicesPath() {
        return Templates.getSlicesPath();
    }

    public final String getIncludesPath() {
        return Templates.getIncludesPath();
    }

    protected Result json(Object object) {
        String str = Json.toJson(object);
        // InputStream stream = new
        // ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        return Results.stream("json", str);
    }

    protected Result view(String path) {
        return view(path, null);
    }

    protected Result view(String path, String cacheFor) {
        app.setViewPath(path);
        app.setActionURI(app.servletRequest().getRequestURI());

        if (cacheFor != null) {
            // Set cache header for caching server.
            app.servletResponse().setHeader("X-CB-Cache-Page", cacheFor);
        }

        return Results.view(path);
    }

    protected Result redirect(String path) {
        if (!StringUtils.isBlank(path) && path.indexOf("http") == 0) {
            return Results.redirect(path);
        }

        if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
            UrlRewrite urlRewrite = app.repository(UrlRewrites.class).forTargetURI(path);

            if (urlRewrite != null) {
                String requestPath = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());

                if (!Str.isEmpty(requestPath))
                    path = requestPath;
            }

            // Do not redirect back to HTTP if we are in a secure context.
            if (app.isSecureRequest()) {
                // Make sure that the HttpsFilter knows that we have come from a
                // secure post request.
                if (app.isPostRequest())
                    Requests.rememberSecurePostGetRedirect(path);

                path = new StringBuilder(getSecureBasePath()).append(path).toString();
            }
        }

        return Results.redirect(path);
    }

    protected RequestContext getRequestContext() {
        return app.context().getRequestContext();
    }

    protected Store getStore() {
        return app.context().getStore();
    }

    public String getSecureBasePath() {
        return app.getSecureBasePath();
    }

    public boolean isCustomerLoggedIn() {
        Boolean isCustomerLoggedIn = (Boolean) getRequest().getAttribute("isCustomerLoggedIn");
        if (isCustomerLoggedIn != null)
            return isCustomerLoggedIn.booleanValue();
        else
            return false;
    }

    public <T> T getLoggedInCustomer() {
        return (T) getRequest().getAttribute("loggedInCustomer");
    }

    protected <T> void setLoggedInCustomer(T customer) {
        app.setLoggedInCustomer(customer);
    }

    /**
     * Renew session after login etc. to prevent session hijacking.
     */
    protected void renewSession() {
        HttpSession session = app.servletRequest().getSession(false);

        if (session != null && session.getAttributeNames() != null) {
            Map<String, Object> tmp = new HashMap<>();

            Enumeration<String> names = session.getAttributeNames();

            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                tmp.put(name, session.getAttribute(name));
            }

            sessionInvalidate();

            if (tmp.size() > 0) {
                Set<String> tmpNames = tmp.keySet();

                for (String name : tmpNames) {
                    sessionSet(name, tmp.get(name));
                }
            }
        }
    }

    protected void sessionInvalidate() {
        app.sessionInvalidate();
    }

    protected void sessionSet(String key, Object value) {
        app.sessionSet(key, value);
    }

    protected void sessionRemove(String key) {
        app.sessionRemove(key);
    }

    @SuppressWarnings("unchecked")
    protected <T> T sessionGet(String key) {
        return (T) app.sessionGet(key);
    }

    protected void cookieSet(String key, Object value) {
        app.cookieSet(key, value);
    }

    protected void cookieSet(String key, Object value, Integer maxAge) {
        app.cookieSet(key, value, maxAge);
    }

    protected void cookieUnset(String key) {
        app.cookieUnset(key);
    }

}
