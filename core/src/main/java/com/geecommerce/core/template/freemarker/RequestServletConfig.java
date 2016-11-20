package com.geecommerce.core.template.freemarker;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.Iterators;

public class RequestServletConfig implements ServletConfig {
    private final ServletContext servletContext;

    private Map<String, String> initParams = new HashMap<>();

    public RequestServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public String getServletName() {
        return "";
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getInitParameterNames() {
        return Iterators.asEnumeration(initParams.keySet().iterator());
    }
}
