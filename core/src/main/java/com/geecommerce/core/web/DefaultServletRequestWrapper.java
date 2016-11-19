package com.geecommerce.core.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.model.RequestContext;

public class DefaultServletRequestWrapper extends HttpServletRequestWrapper {
    protected RequestContext requestCtx = null;

    public DefaultServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public DefaultServletRequestWrapper(HttpServletRequest request, RequestContext requestCtx) {
        super(request);
        this.requestCtx = requestCtx;
    }

    @Override
    public Cookie[] getCookies() {
        return super.getCookies();
    }

    @Override
    public String getHeader(String name) {
        return Sanitizer.clean(super.getHeader(name));
    }

    public String getUncheckedHeader(String name) {
        return super.getHeader(name);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Enumeration getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);

        if (headers == null)
            return null;

        List<String> cleanValues = new ArrayList<>();

        while (headers.hasMoreElements()) {
            cleanValues.add(Sanitizer.clean(headers.nextElement()));
        }

        return Collections.enumeration(cleanValues);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getUncheckedHeaders(String name) {
        return super.getHeaders(name);
    }

    @Override
    public String getQueryString() {
        return super.getQueryString();
    }

    @Override
    public String getParameter(String name) {
        return Sanitizer.clean(super.getParameter(name));
    }

    public String getUncheckedParameter(String name) {
        return super.getParameter(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();

        if (parameterMap == null)
            return null;

        Map<String, String[]> sanitizedParameterMap = new HashMap<>();

        Set<String> keys = parameterMap.keySet();

        for (String key : keys) {
            String[] values = parameterMap.get(key);

            List<String> cleanValues = new ArrayList<>();

            for (String value : values) {
                cleanValues.add(Sanitizer.clean(value));
            }

            sanitizedParameterMap.put(key, cleanValues.toArray(new String[cleanValues.size()]));
        }

        return Collections.unmodifiableMap(sanitizedParameterMap);
    }

    @SuppressWarnings("rawtypes")
    public Map getUncheckedParameterMap() {
        return super.getParameterMap();
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] paramValues = super.getParameterValues(name);

        if (paramValues == null)
            return null;

        List<String> cleanValues = new ArrayList<>();

        for (String value : paramValues) {
            cleanValues.add(Sanitizer.clean(value));
        }

        return cleanValues.toArray(new String[cleanValues.size()]);
    }

    public String[] getUncheckedParameterValues(String name) {
        return super.getParameterValues(name);
    }

    @Override
    public String getRequestURI() {
        return super.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        // URL url = Requests.getURL((HttpServletRequest) super.getRequest());
        //
        // StringBuffer strippedURL = new
        // StringBuffer(url.getProtocol()).append(Str.PROTOCOL_SUFFIX).append(url.getHost());
        //
        // return strippedURL.append(getRequestURI()).append(getPathInfo() ==
        // null ? Str.EMPTY : getPathInfo());
        return super.getRequestURL();
    }

    @Override
    public String getServletPath() {
        // if (requestCtx != null &&
        // requestCtx.getUrlType().getUrlParser().isMatch(Requests.getURLWithoutPortAndContextPath((HttpServletRequest)
        // super.getRequest()), requestCtx)) {
        // return
        // requestCtx.getUrlType().getUrlParser().stripServletPath(super.getServletPath(),
        // requestCtx);
        // } else {
        // return super.getServletPath();
        // }

        return super.getServletPath();

    }

    @Override
    public Locale getLocale() {
        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            return new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        } else {
            return super.getLocale();
        }
    }
}
