package com.geecommerce.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;

public class UrlRewriteFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        App app = App.get();

        app.setOriginalURI(path);
        app.setOriginalQueryString(httpRequest.getQueryString());

        UrlRewriteHelper helper = app.helper(UrlRewriteHelper.class);

        if (!helper.isExcludedFromURLRewriting(path)) {
            // See if URL-rewrite has been initialized from DB.
            UrlRewrite urlRewrite = app.getUrlRewrite(path);

            if (urlRewrite != null && urlRewrite.getTargetURL() != null) {
                request.getRequestDispatcher(urlRewrite.getTargetURL()).forward(request, response);
            }
            // Continue normally if no URL-rewrite could be located
            else {
                filterChain.doFilter(request, response);
            }
        }
        // Skip URL-rewriting for this URL (according to web.xml setting)
        else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
