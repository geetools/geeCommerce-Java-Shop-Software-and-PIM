package com.geecommerce.checkout.helper;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.checkout.form.AddressForm;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.customer.model.Address;
import com.geecommerce.shipping.service.ShippingService;
import net.sourceforge.stripes.action.Resolution;

import java.util.List;

public interface PaymentHelper extends Helper {
    public String errorResponseUrl();

    public String backResponseUrl();

    public String successResponseUrl();
}
