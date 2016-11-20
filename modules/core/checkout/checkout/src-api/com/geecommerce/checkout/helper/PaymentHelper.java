package com.geecommerce.checkout.helper;

import com.geecommerce.core.service.api.Helper;

public interface PaymentHelper extends Helper {
    public String errorResponseUrl();

    public String backResponseUrl();

    public String successResponseUrl();
}
