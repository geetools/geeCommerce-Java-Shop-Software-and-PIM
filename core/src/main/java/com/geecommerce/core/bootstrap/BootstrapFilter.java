package com.geecommerce.core.bootstrap;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;

public class BootstrapFilter implements javax.servlet.Filter {
    FilterConfig filterConfig = null;

    private static final Logger log = LogManager.getLogger(BootstrapFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (log.isTraceEnabled()) {
            log.trace("Starting to bootstrap for URI: " + ((HttpServletRequest) request).getRequestURI());
        }

        Enumeration<String> initParameterNames = filterConfig.getInitParameterNames();

        while (initParameterNames.hasMoreElements()) {
            String initParameterName = initParameterNames.nextElement();
            String initParameterValue = filterConfig.getInitParameter(initParameterName);

            if (log.isTraceEnabled()) {
                log.trace("Adding bootstrap-init-param '" + initParameterName + "' with value '" + initParameterValue + "' to AppRegistry.");
            }

            App.get().registryPut(initParameterName, initParameterValue);
        }

        App.get().bootstrap((HttpServletRequest) request, (HttpServletResponse) response);
        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
}
