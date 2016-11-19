package com.geecommerce.checkout.helper;

import com.geecommerce.checkout.configuration.Key;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.google.inject.Inject;

@Helper
public class DefaultPaymentHelper implements PaymentHelper {
    @Inject
    protected App app;

    @Override
    public String errorResponseUrl() {
        return app.cpStr_(Key.CHECKOUT_FLOW_PAYMENT_ERROR, app.cpStr_(Key.CHECKOUT_FLOW));
    }

    @Override
    public String backResponseUrl() {
        return app.cpStr_(Key.CHECKOUT_FLOW_PAYMENT_ERROR, app.cpStr_(Key.CHECKOUT_FLOW));
    }

    @Override
    public String successResponseUrl() {
        return app.cpStr_(Key.CHECKOUT_FLOW_SUCCESS, app.cpStr_(Key.CHECKOUT_FLOW) + "success");
    }
}
