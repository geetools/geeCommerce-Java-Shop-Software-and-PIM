package com.geecommerce.customer.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.customer.model.Customer;

@Widget(name = "customer_profile", js = true, css = true)
public class CustomerProfileWidget extends AbstractWidgetController implements WidgetController {

    private final String IS_LOGGED_IN = "isLoggedIn";
    private final String LOGGED_IN_CUSTOMER_FORENAME = "customerForename";
    private final String LOGGED_IN_CUSTOMER_SURNAME = "customerSurname";
    private final String LOGGED_IN_CUSTOMER_LOGIN = "customerLogin";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {

        boolean loggedIn = app.isCustomerLoggedIn();
        widgetCtx.setParam(IS_LOGGED_IN, loggedIn);

        if (loggedIn) {
            Customer customer = app.getLoggedInCustomer();
            if (customer != null) {
                widgetCtx.setParam(LOGGED_IN_CUSTOMER_FORENAME, customer.getForename());
                widgetCtx.setParam(LOGGED_IN_CUSTOMER_SURNAME, customer.getSurname());
                widgetCtx.setParam(LOGGED_IN_CUSTOMER_LOGIN, customer.getEmail());
            }
        }

        widgetCtx.render();
    }
}
