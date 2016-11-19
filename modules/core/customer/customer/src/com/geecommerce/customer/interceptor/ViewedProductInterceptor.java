package com.geecommerce.customer.interceptor;

import com.geecommerce.core.App;
import com.geecommerce.core.interceptor.AbstractMethodInterceptor;
import com.geecommerce.core.interceptor.annotation.Intercept;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.helper.ViewedProductHelper;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.google.inject.Inject;

@Intercept(name = ".controller.ProductAction", method = "view")
public class ViewedProductInterceptor extends AbstractMethodInterceptor {
    @Inject
    protected App app;

    private final CustomerService customerService;
    private final ViewedProductHelper viewedProductHelper;

    @Inject
    public ViewedProductInterceptor(CustomerService customerService, ViewedProductHelper viewedProductHelper) {
        this.customerService = customerService;
        this.viewedProductHelper = viewedProductHelper;
    }

    public void onAfter(Object[] args) {
        Id productId = app.getModelIdIfExists();

        if (productId != null) {
            if (app.isCustomerLoggedIn()) {
                // If customer is logged in we store viewed product in database.
                customerService.rememberViewedProduct((Customer) app.getLoggedInCustomer(), productId);
            } else {
                // If customer is not logged in we store viewed product in
                // cookie.
                viewedProductHelper.rememberViewedProduct(productId);
            }
        }
    }
}
