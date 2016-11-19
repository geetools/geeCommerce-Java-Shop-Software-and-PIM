package com.geecommerce.core.bootstrap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractBootstrap {
    private HttpServletRequest request = null;

    private HttpServletResponse response = null;

    public HttpServletRequest getRequest() {
	return request;
    }

    protected void setRequest(final HttpServletRequest request) {
	this.request = request;
    }

    public HttpServletResponse getResponse() {
	return response;
    }

    protected void setResponse(final HttpServletResponse response) {
	this.response = response;
    }

    public abstract void init();
}
