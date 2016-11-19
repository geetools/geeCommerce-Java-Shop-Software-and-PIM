package com.geecommerce.core.url.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.system.model.RequestContext;

public class VC_L_2 extends AbstractURLParser implements URLParser {
    private static final Logger log = LogManager.getLogger(VC_L_2.class);

    @Override
    public boolean isMatch(String requestURL, RequestContext requestCtx) {
	if (requestURL == null)
	    throw new NullPointerException("Cannot parse null URL");

	if (requestURL.startsWith(addProtocolToUrlPrefix(requestCtx.getUrlPrefix(), requestURL))) {
	    if (log.isTraceEnabled()) {
		log.trace(requestURL + " matches url-prefix " + requestCtx.getUrlPrefix() + " using " + this.getClass().getSimpleName());
	    }

	    return true;
	}

	return false;
    }

    @Override
    public String stripServletPath(String servletPath, RequestContext requestCtx) {
	// Find the first slash after the language part
	int pos = servletPath.indexOf('/', 1);

	return new StringBuilder(servletPath.substring(pos)).toString();
    }
}
