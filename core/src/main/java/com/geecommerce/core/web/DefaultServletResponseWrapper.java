package com.geecommerce.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.geecommerce.core.App;
import com.geecommerce.core.system.model.RequestContext;

public class DefaultServletResponseWrapper extends HttpServletResponseWrapper {
    private static final String APPEND_SESSION_ID_CONFIG_KEY = "general/web/append_session_id";
    private static final boolean APPEND_SESSION_ID_DEFAULT_VALUE = false; // better
                                                                          // for
                                                                          // SEO.

    @SuppressWarnings("unused")
    private RequestContext requestCtx = null;
    private int httpStatus;

    public DefaultServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public DefaultServletResponseWrapper(HttpServletResponse response, RequestContext requestCtx) {
        super(response);
        this.requestCtx = requestCtx;
    }

    @Override
    public String encodeURL(String url) {
        if (isAppendSessionIdEnabled()) {
            return super.encodeURL(url);
        } else {
            return url;
        }
    }

    @Override
    public String encodeUrl(String url) {
        if (isAppendSessionIdEnabled()) {
            return super.encodeUrl(url);
        } else {
            return url;
        }
    }

    @Override
    public String encodeRedirectURL(String url) {
        if (isAppendSessionIdEnabled()) {
            return super.encodeRedirectURL(url);
        } else {
            return url;
        }
    }

    @Override
    public String encodeRedirectUrl(String url) {
        if (isAppendSessionIdEnabled()) {
            return super.encodeRedirectUrl(url);
        } else {
            return url;
        }
    }

    private boolean isAppendSessionIdEnabled() {
        App app = App.get();

        Boolean sessionIdEnabled = app.registryGet(APPEND_SESSION_ID_CONFIG_KEY);

        if (sessionIdEnabled == null) {
            sessionIdEnabled = app.cpBool_(APPEND_SESSION_ID_CONFIG_KEY, APPEND_SESSION_ID_DEFAULT_VALUE);
            app.registryPut(APPEND_SESSION_ID_CONFIG_KEY, sessionIdEnabled);
        }

        return sessionIdEnabled;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.httpStatus = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void setStatus(int sc) {
        this.httpStatus = sc;
        super.setStatus(sc);
    }

    public int getStatus() {
        return httpStatus;
    }

    public boolean hasErrorStatus() {
        return httpStatus > 399;
    }
}
