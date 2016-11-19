package com.geecommerce.checkout.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.cart.CartConstant;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.configuration.Key;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.helper.CheckoutHelper;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.CheckoutAddress;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.checkout.service.PaymentService;
import com.geecommerce.core.App;
import com.geecommerce.core.payment.AbstractPaymentMethod;
import com.geecommerce.core.payment.PaymentHelper;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.country.service.CountryService;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.shipping.converter.ShippingPackageConverter;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.service.ShippingService;
import com.geecommerce.webmessage.service.WebMessageService;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
public abstract class BaseCheckoutController extends BaseController {
    @Inject
    protected App app;

    protected final CheckoutService checkoutService;
    protected final CheckoutHelper checkoutHelper;
    protected final CustomerService customerService;
    protected final ShippingService shippingService;
    protected final PaymentService paymentService;
    protected final CalculationService calculationService;
    protected final CalculationHelper calculationHelper;
    protected final CartService cartService;
    protected final CouponService couponService;
    protected final CountryService countryService;

    public BaseCheckoutController(CheckoutService checkoutService,
        CheckoutHelper checkoutHelper,
        CustomerService customerService,
        ShippingService shippingService,
        PaymentService paymentService,
        CalculationService calculationService,
        CalculationHelper calculationHelper,
        CartService cartService,
        CouponService couponService,
        CountryService countryService) {
        this.checkoutService = checkoutService;
        this.checkoutHelper = checkoutHelper;
        this.customerService = customerService;
        this.shippingService = shippingService;
        this.paymentService = paymentService;
        this.calculationService = calculationService;
        this.calculationHelper = calculationHelper;
        this.cartService = cartService;
        this.couponService = couponService;
        this.countryService = countryService;
    }

    @Request("process-login")
    public Result processLogin() {
        if (getCart() == null) {
            return redirect("/cart/view/");
        }

        if (isCustomerLoggedIn()) {
            return redirect("/cart/address/");
        }

        // return new
        // ForwardResolution("/customer/account/process-login/").addParameter("postLoginRedirect",
        // getCheckoutAction());
        return view("/customer/account/process-login/").bind("postLoginRedirect", getCheckoutAction());
    }

    protected boolean hasValidCart() {
        Cart c = getCart();

        if (c == null || c.getEnabled() == false)
            return false;

        if (c.getActiveCartItems() == null || c.getActiveCartItems().isEmpty())
            return false;

        return true;
    }

    public Map<String, String> copyToStringValueMap(Map<String, Object> input) {
        Map<String, String> ret = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            ret.put(entry.getKey(), (String) entry.getValue());
        }
        return ret;
    }

    protected boolean hasValidCheckout() {
        Checkout c = getCheckout();

        if (c == null || c.getId() == null)
            return false;

        return true;
    }

    protected boolean checkCoupon(Cart cart, Checkout checkout) {
        if (!isTheSameCoupon(cart, checkout)) {
            checkout.setCouponCode(getCart().getCouponCode());
            checkoutService.updateCheckout(checkout);
            WebMessageService webMessage = app.getService(WebMessageService.class);
            webMessage.storeInfo(app.message("Coupon in cart was changed during checkout"), "coupon");
            return false;
        }
        return true;
    }

    public String getCheckoutAction() {
        String checkoutFlowURL = app.cpStr_(Key.CHECKOUT_FLOW);

        if (checkoutFlowURL != null && !checkoutFlowURL.startsWith("http")) {
            checkoutFlowURL = new StringBuilder(app.getSecureBasePath()).append(checkoutFlowURL).toString();
        }

        return checkoutFlowURL;
    }

    protected boolean isTheSameCoupon(Cart cart, Checkout checkout) {
        if (cart.getCouponCode() == null && checkout.getCouponCode() == null)
            return true;
        else if (cart.getCouponCode() == null || checkout.getCouponCode() == null)
            return false;
        else if (!cart.getCouponCode().getId().equals(checkout.getCouponCode().getId()))
            return false;
        return true;
    }

    public Result processCheckout() throws Exception {
        Cart cart = getCart();

        if (cart == null || !isAddressDataValid()) {
            return redirect("/cart/view/");
        }

        List<AbstractPaymentMethod> paymentMethods = PaymentHelper.locatePaymentMethods();

        AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(getForm().getPaymentMethodCode());

        // fill checkout with payment method
        getCheckout().setPaymentMethod(getForm().getPaymentMethodCode());
        checkoutService.updateCheckout(getCheckout());

        Map<String, String[]> requestParameters = getRequest().getParameterMap();

        Map<String, Object> filteredRequestParameters = PaymentHelper.filterRequestParameters(paymentMethod.getFormFieldPrefix(), requestParameters);

        if (paymentMethod.isFormDataValid(filteredRequestParameters)) {
            Id orderId = app.nextId();
            Customer customer = getLoggedInCustomer();

            if (customer == null) {
                // Create new customer
                customer = app.getModel(Customer.class).setId(app.nextId()).setCustomerNumber(app.nextIncrementId("customer_number")).setForename(getForm().getDelivery().getFirstName())
                    .setSurname(getForm().getDelivery().getLastName())
                    .setEmail(getForm().getEmail());

                customer = customerService.createCustomer(customer);
            }

            // Convert cart to order
            Order order = checkoutHelper.convertCartToOrder(cart);
            order.fromRequestContext(getRequestContext()).belongsTo(customer).fromCheckout(getCheckout()).setId(orderId);

            // Add address
            checkoutHelper.addAddressesToOrder(order, getForm());

            // Add address to customer
            List<Address> customerAddresses = checkoutHelper.getCustomerAddresses(getForm());
            customerService.appendAddresses(customer, customerAddresses);

            // Add payment
            OrderPayment orderPayment = app.getModel(OrderPayment.class);
            orderPayment.setId(app.nextId()).setPaymentMethodCode(paymentMethod.getCode()).setCurrency(app.getBaseCurrency()).belongsTo(order);
            order.setOrderPayment(orderPayment);

            // Add shipping (only shipping, option codes and shippingAmount)
            checkoutHelper.addShipmentToOrder(order, getCheckout());

            // Calculate totals and add them the order object.
            CalculationResult totals = getCheckout().getTotals();
            checkoutHelper.addTotals(order, totals);

            // Finally, save the order.
            Order savedOrder;

            // try
            // {
            savedOrder = checkoutService.createOrder(order);

            // Id orderId = savedOrder.getId();

            resetSession();
            // }
            // catch (QuantityNotAvailableException e)
            // {
            // addValidationError(app.message("Quantity not available"));
            //
            // return redirect("/cart/view/");
            // }

            // ----------------------------------------------------------------------------
            // After order has been saved, we can go on with the *real* payment
            // stuff.
            // ----------------------------------------------------------------------------

            PaymentResponse response = null;
            if (useAuthorization() && paymentMethod.supportAuthorization()) {
                response = paymentMethod.authorizePayment(filteredRequestParameters, savedOrder, totals);
            } else {
                response = paymentMethod.processPayment(filteredRequestParameters, savedOrder, totals);
            }
            paymentService.processPayment(orderPayment, response);

            if (response.getUrl() == null) {
                if (response.getPaymentEventResponse().getErrorMessage() == null) {
                    return view("checkout/success");
                } else {
                    return view("checkout/unsuccess");
                }
            } else {
                return redirect(response.getUrl());
            }

        }
        return view("checkout/success");
    }

    protected void fillCheckoutWithPayment() {
        Checkout checkout = getCheckout(true);

        if (checkout != null && checkout.getId() != null) {
            if (isCustomerLoggedIn())
                checkout.belongsTo((Customer) getLoggedInCustomer());

            if (getForm().getPaymentMethodCode() != null && !getForm().getPaymentMethodCode().isEmpty()) {
                checkout.setPaymentMethod(getForm().getPaymentMethodCode());
                AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(getForm().getPaymentMethodCode());
                checkout.setPaymentRateAmount(paymentMethod.getRate());
                checkoutService.updateCheckout(checkout);
            }

        }
    }

    protected void fillCheckoutWithShipping(Double totalAmount) {
        Checkout checkout = getCheckout(true);

        if (checkout != null && checkout.getId() != null) {
            // Just in case customer logged on after checkout object was created
            if (isCustomerLoggedIn())
                checkout.belongsTo((Customer) getLoggedInCustomer());

            checkoutHelper.addShippingToCheckout(checkout, getCart(), getForm(), shippingService, totalAmount);
            checkoutService.updateCheckout(checkout);
        }
    }

    protected void fillCheckoutWithAddress() {
        Checkout checkout = getCheckout(true);

        if (checkout != null && checkout.getId() != null) {
            // Just in case customer logged on after checkout object was created
            if (isCustomerLoggedIn())
                checkout.belongsTo((Customer) getLoggedInCustomer());

            checkoutHelper.addAddressesToCheckout(checkout, getForm());
            checkoutService.updateCheckout(checkout);
        }
    }

    protected void fillFormWithAddress() {
        //
        if (isCustomerLoggedIn()) {
            Customer customer = getLoggedInCustomer();

            List<Address> addresses = customerService.getAddressesFor(customer);

            if (addresses != null && addresses.size() > 0) {
                CheckoutForm form = getForm();

                for (Address address : addresses) {
                    if (address.isDefaultDeliveryAddress()) {
                        form.setDelivery(checkoutHelper.fromAddress(address));
                    }

                    if (address.isDefaultInvoiceAddress()) {
                        form.setInvoice(checkoutHelper.fromAddress(address));
                    }
                }
            }
            if (getForm().getInvoice() == null)
                getForm().setCustomInvoice(false);
            else
                getForm().setCustomInvoice(true);
        }
    }

    public boolean useAuthorization() {
        return app.cpBool_(Key.USE_AUTHORIZATION, false);
    }

    protected boolean isAddressDataValid(CheckoutAddress address) {
        if (address == null)
            return false;

        if (address.getFirstName() == null || address.getLastName() == null || address.getZip() == null || address.getAddress1() == null || address.getCity() == null || address.getCountry() == null) {
            return false;
        }

        return true;
    }

    protected boolean isAddressDataValid() {
        Checkout checkout = getCheckout();

        if (checkout == null)
            return false;

        return isAddressDataValid(checkout.getInvoiceAddress()) && isAddressDataValid(checkout.getDeliveryAddress());
    }

    public void resetSession() {
        if (isCustomerLoggedIn()) {
            getCart().setEnabled(false);
            cartService.updateCart(getCart());
        }

        // Remove cart from cookie and session
        cookieUnset(CartConstant.COOKIE_KEY_CART_ID);
        sessionRemove(CartConstant.SESSION_KEY_CART);

        // Remove checkout session variables
        sessionRemove(CheckoutConstant.SESSION_KEY_CHECKOUT_FORM);
        sessionRemove(CheckoutConstant.SESSION_KEY_CHECKOUT);
    }

    public Checkout getCheckout() {
        return checkoutHelper.getCheckout(getCart(), false);
    }

    public Checkout getCheckout(boolean createIfNotExists) {
        return checkoutHelper.getCheckout(getCart(), createIfNotExists);
    }

    public abstract CheckoutForm getForm();

    public CalculationResult getCartTotals() throws Exception {
        return getCheckout().getTotals();
    }

    private void addCouponInfo(CalculationContext calcCtx) {
        Cart cart = getCart();

        CartAttributeCollection cartAttributeCollection = ((CouponData) cart).toCartAttributeCollection();
        CouponCode code = couponService.maintainCouponCodesList(cart.getCouponCode(), cartAttributeCollection, cart.getUseAutoCoupon());
        cart.setCouponCode(code);
        cartService.updateCart(cart);
        couponService.applyDiscount(calcCtx, cart.getCouponCode(), cartAttributeCollection);
    }

    protected void addCouponInfo(Order order, CalculationContext calcCtx) {

        Cart cart = getCart();
        CartAttributeCollection cartAttributeCollection = ((CouponData) order).toCartAttributeCollection();
        CouponCode code = couponService.maintainCouponCodesList(order.getCouponCode(), cartAttributeCollection, cart.getUseAutoCoupon());
        order.setCouponCode(code);
        couponService.applyDiscount(calcCtx, order.getCouponCode(), cartAttributeCollection);
    }

    public void setForm(CheckoutForm form) {
        getForm();
    }

    public Cart getCart() {
        return sessionGet(CartConstant.SESSION_KEY_CART);
    }

    public List<ShippingOption> getShippingOptions() {
        // TODO: now getting options only by one package
        // ShippingPackage shippingData = ((ShippingPackageConverter)
        // getCart()).toShippingData();
        ShippingPackage shippingPackage = ((ShippingPackageConverter) getCheckout()).toShippingPackages().get(0);
        // shippingData.setShippingAddress(shippingDataAddress.getShippingAddress());
        return shippingService.getShippingOptions(shippingPackage);
    }

    public List<AbstractPaymentMethod> getPaymentMethods() {
        // TODO : check performance
        // if (paymentMethods == null)
        // paymentMethods = PaymentHelper.locatePaymentMethods();
        // return paymentMethods;

        return PaymentHelper.locatePaymentMethods();
    }

    public Map<String, String> getAllowedCountries() {
        List<Country> countries = countryService.getAll();
        Map<String, String> allowedCountries = new LinkedHashMap<>();
        String cntr = app.cpStr_(Key.ALLOWED_COUNTRIES);
        if (cntr != null) {
            String[] keys = cntr.split(",");
            for (String key : keys)
                countries.stream().filter(country -> country.getCode().equals(key)).forEach(country -> allowedCountries.put(key, country.getName().getStr()));
        }
        return allowedCountries;
    }
}
