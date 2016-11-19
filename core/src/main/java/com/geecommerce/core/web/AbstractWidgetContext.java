package com.geecommerce.core.web;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.api.WidgetContext;
import com.geemvc.view.GeemvcKey;
import com.google.inject.Inject;

public abstract class AbstractWidgetContext implements WidgetContext {
    @Inject
    protected App app;

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ServletContext servletContext;

    protected static final String BASE_PRODUCT_URI = "/catalog/product/";
    protected static final String BASE_PRODUCT_LIST_URI = "/catalog/product-list/";

    protected void init(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public RequestContext getRequestContext() {
        ApplicationContext appCtx = app.getApplicationContext();
        return appCtx.getRequestContext();
    }

    public Store getStore() {
        ApplicationContext appCtx = app.getApplicationContext();
        return appCtx.getStore();
    }

    public Merchant getMerchant() {
        ApplicationContext appCtx = app.getApplicationContext();
        return appCtx.getMerchant();
    }

    public <T> T getLoggedInCustomer() {
        return app.getLoggedInCustomer();
    }

    public boolean isCustomerLoggedIn() {
        return app.isCustomerLoggedIn();
    }

    public String cookieGet(String key) {
        return app.cookieGet(key);
    }

    public <T> T sessionGet(String key) {
        return app.sessionGet(key);
    }

    public Locale currentLocale() {
        return (Locale) request.getAttribute(GeemvcKey.CURRENT_LOCALE);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public Id getProductId() {
        String resolvedPath = (String) getRequest().getAttribute(GeemvcKey.RESOLVED_PATH);

        Id id = null;

        if (resolvedPath != null && resolvedPath.startsWith(BASE_PRODUCT_URI)) {
            id = app.getModelIdIfExists();
        }

        return id;
    }

    public Id getNavigationItemId() {
        String resolvedPath = (String) getRequest().getAttribute(GeemvcKey.RESOLVED_PATH);

        Id id = null;

        if (resolvedPath != null && resolvedPath.startsWith(BASE_PRODUCT_LIST_URI)) {
            id = app.getModelIdIfExists();
        }

        return id;
    }
}
