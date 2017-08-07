package com.geecommerce.core.url.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.system.model.RequestContext;

public class DOMAIN_ONLY_WITHOUT_LANGUAGE extends AbstractURLParser implements URLParser {
    private static final Logger log = LogManager.getLogger(DOMAIN_ONLY_WITHOUT_LANGUAGE.class);

    @Override
    public boolean isMatch(String requestURL, RequestContext requestCtx) {
        if (requestURL == null)
            throw new NullPointerException("Cannot parse null URL");

        if (requestURL.startsWith(prepareUrlPrefix(requestCtx.getUrlPrefix(), requestURL))) {
            if (log.isTraceEnabled()) {
                log.trace(requestURL + " matches url-prefix " + requestCtx.getUrlPrefix() + " using "
                    + this.getClass().getSimpleName());
            }

            return true;
        }

        return false;
    }

    @Override
    public String stripServletPath(String servletPath, RequestContext requestCtx) {
        return servletPath;
    }
}
