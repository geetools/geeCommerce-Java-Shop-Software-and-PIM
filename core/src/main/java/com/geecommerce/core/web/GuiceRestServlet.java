package com.geecommerce.core.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Injector;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceRestServlet extends GuiceContainer {
    private static final long serialVersionUID = 8264859709024682040L;

    public GuiceRestServlet(Injector injector) {
        super(injector);
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
}
