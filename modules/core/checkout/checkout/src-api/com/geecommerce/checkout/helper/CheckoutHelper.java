package com.geecommerce.checkout.helper;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.checkout.form.AddressForm;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.customer.model.Address;
import com.geecommerce.shipping.service.ShippingService;
import java.util.List;

public interface CheckoutHelper extends Helper {
    public Order convertCartToOrder(Cart cart) throws Exception;

    public void addAddressesToOrder(Order order, CheckoutForm form);

    public void addAddressesToCheckout(Checkout checkout, CheckoutForm form);

    public void addShippingToCheckout(Checkout checkout, Cart cart, CheckoutForm form, ShippingService shippingService, Double totalAmount);

    public void addTotals(Order order, CalculationResult totals);

    public void addShipmentToOrder(Order order, Checkout checkout);

    public boolean canBePended(Order order);

    public Order pendOrder(Order order);

    public boolean canBeAccepted(Order order);

    public Order acceptOrder(Order order);

    public Order rejectOrderPayment(Order order);

    public void sendConfirmationEmail(Order order);

    public void clearSessionOfCheckout();

    public void putInfoForSuccessPage(Order order);

    public AddressForm fromAddress(Address address);

    public List<Address> getCustomerAddresses(CheckoutForm form);

    public void setPrices(Order order, CalculationResult calculationResult);

    // ////////////////////////

    public Checkout getCheckout(Cart cart);

    public Checkout getCheckout(Cart cart, boolean createIfNotExists);

}
