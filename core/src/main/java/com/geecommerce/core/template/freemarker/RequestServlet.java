package com.geecommerce.core.template.freemarker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.geecommerce.core.App;

public class RequestServlet extends HttpServlet {
    private static final long serialVersionUID = -621496883800043732L;
    private final RequestServletConfig servletConfig;

    public RequestServlet() {
        try {
            servletConfig = new RequestServletConfig(App.get().servletContext());
            init(servletConfig);
        } catch (ServletException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
