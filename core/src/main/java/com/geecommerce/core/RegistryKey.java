package com.geecommerce.core;

public enum RegistryKey {
    APPLICATION_MODE("Application.Mode"), 
    APPLICATION_CONTEXT("Application.Context"), 
    REFLECTIONS_EXT_CLASSPATH("Reflections.Ext.Classpath"), 
    LOGGER_INITIALIZED("Logger.Initialized"), 
    SERVLET_REQUEST("Servlet.Request"), 
    SERVLET_RESPONSE("Servlet.Response"), 
    SERVLET_CONTEXT("Servlet.Context"), 
    ORIGINAL_URI("Original.URI"), 
    ORIGINAL_QUERY_STRING("Original.QueryString"), 
    REWRITTEN_URI("Rewritten.URI"), 
    REQUEST_TYPE("Request.Type"), 
    VIEW_PATH("View.Path"), 
    ACTION_URI("Action.URI"), 
    CURRENT_MODULE("Current.Module"), 
    UNIT_TEST("Unit.Test");

    private String key = null;

    RegistryKey(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }
}
