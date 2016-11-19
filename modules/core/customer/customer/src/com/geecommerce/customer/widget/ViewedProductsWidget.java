package com.geecommerce.customer.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.customer.helper.ViewedProductHelper;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.google.inject.Inject;

@Widget(name = "viewed_products")
public class ViewedProductsWidget extends AbstractWidgetController implements WidgetController {
    private final CustomerService customerService;
    private final ViewedProductHelper viewedProductHelper;
    private final String CURRENT_PRODUCT_LIST_NAME = "lvp";

    private static final String VIEWED_PRODUCT_IDS = "viewedProductIds";

    @Inject
    public ViewedProductsWidget(CustomerService customerService, ViewedProductHelper viewedProductHelper) {
        this.customerService = customerService;
        this.viewedProductHelper = viewedProductHelper;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        if (widgetCtx.isCustomerLoggedIn()) {
            widgetCtx.setParam(VIEWED_PRODUCT_IDS, customerService.getViewedProductIds((Customer) widgetCtx.getLoggedInCustomer()));
            app.sessionSet(CURRENT_PRODUCT_LIST_NAME, customerService.getViewedProductIds((Customer) widgetCtx.getLoggedInCustomer()));
        } else {
            widgetCtx.setParam(VIEWED_PRODUCT_IDS, viewedProductHelper.getViewedProductIds());
            app.sessionSet(CURRENT_PRODUCT_LIST_NAME, viewedProductHelper.getViewedProductIds());
        }

        widgetCtx.render("customer/viewed_products");
    }
}
