package com.geecommerce.core.url.parser;

import com.geecommerce.core.system.model.RequestContext;

public interface URLParser {
    public boolean isMatch(String requestURL, RequestContext requestCtx);

    public String stripServletPath(String servletPath, RequestContext requestCtx);

}
