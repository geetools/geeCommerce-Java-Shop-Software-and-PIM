package com.geecommerce.checkout.helper;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.checkout.form.AddressForm;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.CheckoutAddress;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.customer.model.Address;
import com.geecommerce.shipping.service.ShippingService;

import java.util.List;

public interface FormHelper extends Helper {

    void fillAddresses(CheckoutForm form, Checkout checkout);

    AddressForm fromCheckoutAddress(CheckoutAddress checkoutAddress);

    AddressForm fromCustomerAddress(Address address);

    CheckoutForm getForm();

    void setForm(CheckoutForm form);
}
