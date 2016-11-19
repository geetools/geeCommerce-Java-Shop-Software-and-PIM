package com.geecommerce.core.web.api;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

import freemarker.template.TemplateModel;

public interface WidgetContext {
    public <T> T getParam(String name);

    public <T> T getParam(String name, Class<?> type);

    public <T> T getParam(String name, Class<?> type, Object defaultValue);

    public void setParam(String name, Object value);

    public void setJsParam(String name, Object value);

    public Map<String, Object> getJsParams();

    public Writer getOut();

    public void render();

    public void render(TemplateModel data);

    public void render(String path);

    public void render(String path, TemplateModel data);

    public void renderContent(String content);

    public void renderContent(String content, TemplateModel data);

    public void invokeBody(Writer writer);

    public RequestContext getRequestContext();

    public Store getStore();

    public Merchant getMerchant();

    public <T> T getLoggedInCustomer();

    public boolean isCustomerLoggedIn();

    public String cookieGet(String key);

    public <T> T sessionGet(String key);

    public Locale currentLocale();

    public HttpServletRequest getRequest();

    public HttpServletResponse getResponse();

    public ServletContext getServletContext();

    public Id getProductId();

    public Id getNavigationItemId();
}
