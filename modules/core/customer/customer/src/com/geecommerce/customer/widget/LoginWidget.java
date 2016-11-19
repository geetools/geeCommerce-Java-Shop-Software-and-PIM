package com.geecommerce.customer.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "login_form")
public class LoginWidget extends AbstractWidgetController implements WidgetController {
    private final String POST_LOGIN_REDIREXT = "postLoginRedirect";
    private final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        widgetCtx.setParam(POST_LOGIN_REDIREXT, widgetCtx.getParam(POST_LOGIN_REDIREXT));
        widgetCtx.setParam(IS_LOGGED_IN, app.isCustomerLoggedIn());
        widgetCtx.render("customer/account/login_form");
    }
}
