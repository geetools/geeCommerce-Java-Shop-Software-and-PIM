package com.geecommerce.checkout.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.flow.helper.CheckoutFlowHelper;
import com.geecommerce.checkout.flow.model.CheckoutFlowStep;
import com.geecommerce.checkout.flow.service.CheckoutFlowService;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.helper.CheckoutHelper;
import com.geecommerce.checkout.helper.FormHelper;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.CheckoutAddress;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.checkout.model.OrderShipmentOption;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.checkout.service.PaymentService;
import com.geecommerce.core.Str;
import com.geecommerce.core.authentication.Passwords;
import com.geecommerce.core.config.MerchantConfig;
import com.geecommerce.core.payment.AbstractPaymentMethod;
import com.geecommerce.core.payment.PaymentHelper;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.type.Id;
import com.geecommerce.country.service.CountryService;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.Payment;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.shipping.model.ShippingItem;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.service.ShippingService;
import com.geemvc.Bindings;
import com.geemvc.HttpMethod;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.validation.Errors;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/checkout/page")
public class PageCheckoutController extends BaseCheckoutController {

    private final FormHelper formHelper;
    private final CartHelper cartHelper;
    private final CheckoutFlowHelper flowHelper;
    private final CheckoutFlowService flowService;

    @Inject
    public PageCheckoutController(CheckoutService checkoutService, CheckoutHelper checkoutHelper,
        CustomerService customerService, ShippingService shippingService, PaymentService paymentService,
        CalculationService calculationService, CalculationHelper calculationHelper, CartService cartService,
        CouponService couponService, FormHelper formHelper, CartHelper cartHelper, CountryService countryService,
        CheckoutFlowHelper flowHelper, CheckoutFlowService flowService) {
        super(checkoutService, checkoutHelper, customerService, shippingService, paymentService, calculationService,
            calculationHelper, cartService, couponService, countryService);
        this.formHelper = formHelper;
        this.cartHelper = cartHelper;
        this.flowHelper = flowHelper;
        this.flowService = flowService;
    }

    @Request(value = "/address", method = HttpMethod.GET)
    public Result address() {
        if (!hasValidCart())
            return redirect("/cart/view/");

        if (!checkCoupon(getCart(), getCheckout()))
            return redirect("/cart/view/");

        formHelper.fillAddresses(getForm(), getCheckout());

        String actionURI = flowHelper.getOriginalURI(flowHelper.getFlowStep(getOriginalURI()));

        return view("checkout/page/address_form").bind("form", getForm()).bind("checkout", getCheckout())
            .bind("countries", getAllowedCountries()).bind("formAction", actionURI);
    }

    @Request(value = "/address", method = HttpMethod.POST)
    public Result processAddress(@Valid CheckoutForm form, Bindings bindings) {

        if (!hasValidCart() || !hasValidCheckout()) {
            return redirect("/cart/view/");
        }

        if (bindings.hasErrors())
            return Results.view("checkout/address_form").bind("form", form).bind("checkout", getCheckout())
                .bind("countries", getAllowedCountries()).bind("formAction", getOriginalURI());

        setForm(form);
        Checkout checkout = getCheckout();
        checkoutHelper.addAddressesToCheckout(checkout, form);
        checkoutService.updateCheckout(checkout);

        CheckoutFlowStep nextFlowStep = flowHelper.getNextActiveFlowStep(flowHelper.getFlowStep(getOriginalURI()),
            true);
        return redirect(nextFlowStep != null ? flowHelper.getOriginalURI(nextFlowStep)
            : flowHelper.getOriginalURI(flowHelper.getFirstActiveFlowStep())).bind("checkout", checkout);

    }

    @Request(value = "/payment", method = HttpMethod.GET)
    public Result payment() {

        if (!hasValidCart() || !hasValidCheckout()) {
            return redirect("/cart/view/");
        }

        if (!checkCoupon(getCart(), getCheckout()))
            return redirect("/cart/view/");

        // get default payment if there is no method set
        if (isCustomerLoggedIn()) {
            if (getCheckout().getPaymentMethod() == null) {
                Payment defaultPayment = customerService.getDefaultPayment(getLoggedInCustomer());
                if (defaultPayment != null) {
                    getCheckout().setPaymentMethod(defaultPayment.getPaymentCode());
                    getCheckout().setPaymentParameters(defaultPayment.getParameters());
                }
            }
        }

        // form value update
        getForm().setPaymentMethodCode(getCheckout().getPaymentMethod());

        return view("checkout/page/payment_form").bind("form", getForm()).bind("paymentMethods", getPaymentMethods())
            .bind("formAction", flowHelper.getOriginalURI(flowHelper.getFlowStep(getOriginalURI())));
    }

    @Request(value = "/payment", method = HttpMethod.POST)
    public Result processPayment(CheckoutForm form) throws Exception {

        if (!hasValidCart() || !hasValidCheckout()) {
            return redirect("/cart/view/");
        }

        getForm().setPaymentMethodCode(form.getPaymentMethodCode());

        Checkout checkout = getCheckout();
        checkout.setShippingAmount(getCheckout().getDeliveryEstimation());

        fillCheckoutWithPayment();
        AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(getForm().getPaymentMethodCode());
        Map<String, String[]> requestParameters = getRequest().getParameterMap();
        Map<String, Object> filteredRequestParameters = PaymentHelper
            .filterRequestParameters(paymentMethod.getFormFieldPrefix(), requestParameters);

        checkout.setPaymentParameters(copyToStringValueMap(filteredRequestParameters));
        checkoutService.updateCheckout(checkout);

        CheckoutFlowStep nextFlowStep = flowHelper.getNextActiveFlowStep(flowHelper.getFlowStep(getOriginalURI()),
            true);
        return redirect(nextFlowStep != null ? flowHelper.getOriginalURI(nextFlowStep)
            : flowHelper.getOriginalURI(flowHelper.getFirstActiveFlowStep())).bind("nextStep", nextFlowStep);
    }

    @Request(value = "/checkout", method = HttpMethod.GET)
    public Result checkout() throws Exception {

        if (isCustomerLoggedIn()) {

            // fill payment
            if (getCheckout().getPaymentMethod() == null) {
                Payment defaultPayment = customerService.getDefaultPayment(getLoggedInCustomer());
                if (defaultPayment != null) {
                    getCheckout().setPaymentMethod(defaultPayment.getPaymentCode());
                    getCheckout().setPaymentParameters(defaultPayment.getParameters());
                }
            }

            // fill customer address
            formHelper.fillAddresses(getForm(), getCheckout());
            Checkout checkout = getCheckout();
            checkoutHelper.addAddressesToCheckout(checkout, getForm());
            checkoutService.updateCheckout(checkout);
        }

        return view("checkout/page/preview").bind("secureBasePath", getSecureBasePath())
            .bind("previewCheckout", getPreviewCheckout()).bind("cartTotals", getCartTotals())
            .bind("previewCart", getPreviewCart()).bind("countries", getAllowedCountries())
            .bind("formAction", flowHelper.getOriginalURI(flowHelper.getFlowStep(getOriginalURI())));
    }

    @Request(value = "/checkout", method = HttpMethod.POST)
    public Result processCheckout(Bindings bindings, Errors errors) throws Exception {
        if (!hasValidCart() || !hasValidCheckout()) {
            return redirect("/cart/view/");
        }

        Cart cart = getCart();
        Checkout checkout = getCheckout();

        boolean completeErrors = false;
        if (!isCompleteCheckoutAddress()) {
            errors.add("checkout.error.noaddress");
            completeErrors = true;
        } else if (!isCompleteCheckoutPayment()) {
            errors.add("checkout.error.nopayment");
            completeErrors = true;
        }
        if (completeErrors)
            return view("checkout/page/preview").bind(bindings.typedValues())
                .bind("secureBasePath", getSecureBasePath()).bind("previewCheckout", getPreviewCheckout())
                .bind("cartTotals", getCartTotals()).bind("previewCart", getPreviewCart())
                .bind("countries", getAllowedCountries())
                .bind("formAction", flowHelper.getOriginalURI(flowHelper.getFlowStep(getOriginalURI())));

        if (bindings.hasErrors()) {
            errors.add("checkout.error.agree.terms");
            return view("checkout/page/preview").bind(bindings.typedValues());
        }

        if (!checkCoupon(getCart(), getCheckout()))
            return redirect("/cart/view/").bind("cart", cart).bind("checkout", checkout);

        Customer customer = getLoggedInCustomer();

        if (customer == null) {
            if (customerService.accountExists(checkout.getEmail())) {
                Account account = customerService.getAccountFor(checkout.getEmail());
                customer = customerService.getCustomer(account.getCustomerId());
                getCheckout().belongsTo(customer);
                checkoutService.updateCheckout(getCheckout());
            } else {
                // Creating user
                customer = app.model(Customer.class);
                customer.setId(app.nextId());
                customer.setCustomerNumber(app.nextIncrementId("customer_number"));

                CheckoutAddress invoiceAddress = checkout.getInvoiceAddress();
                if (invoiceAddress != null) {
                    customer.setSalutation(invoiceAddress.getSalutation());
                    customer.setForename(invoiceAddress.getFirstName());
                    customer.setSurname(invoiceAddress.getLastName());
                    customer.setPhone(invoiceAddress.getPhone());
                    customer.setPhoneCode(null);
                    customer.setEmail(getForm().getEmail());
                }

                customer = customerService.createCustomer(customer);

                if (customer != null && customer.getId() != null) {
                    getCheckout().belongsTo(customer);
                    checkoutService.updateCheckout(getCheckout());

                    String password = Passwords.random();

                    try {
                        byte[] randomSalt = Passwords.getRandomSalt();

                        Account account = app.model(Account.class);
                        account.belongsTo(customer).setUsername(checkout.getEmail())
                            .setPassword(encryptPassword(password, randomSalt)).setSalt(randomSalt).enableAccount();

                        account = customerService.createAccount(account);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                        errors.add("checkout.error.resetPassword");
                    }
                }
            }
        }

        customerService.updateCustomer(customer);
        checkoutService.updateCheckout(getCheckout());

        // Convert cart to order
        Order order = checkoutHelper.convertCartToOrder(cart);
        order.setNote(checkout.getNote());
        // order.setClubMember(checkout.isClubMember());

        // TODO : check later!!!
        order.fromRequestContext(getRequestContext()).belongsTo(customer).fromCheckout(getCheckout());
        // .setId(orderId);
        // order.fromRequestContext(getRequestContext()).belongsTo(customer).fromCheckout(getCheckout()).setId(id);

        // Add address
        checkoutHelper.addAddressesToOrder(order, getForm());

        // Add address to customer
        List<Address> customerAddresses = checkoutHelper.getCustomerAddresses(getForm());
        customerService.appendAddresses(customer, customerAddresses);

        // Add payment info for customer
        String paymentMethodCode = getCheckout().getPaymentMethod();
        Map<String, String> paymentParams = new HashMap<>();
        customerService.addPayment(customer, paymentMethodCode, paymentParams);

        // Add payment
        OrderPayment orderPayment = app.model(OrderPayment.class);
        orderPayment.setId(app.nextId()).setPaymentMethodCode(getCheckout().getPaymentMethod())
            .setCurrency(app.getBaseCurrency()).setRateAmount(getCheckout().getPaymentRateAmount())
            .belongsTo(order);

        orderPayment.setCustom(checkout.getPaymentParameters());
        order.setOrderPayment(orderPayment);

        setShippingPackages(order, checkout);

        // Calculate totals and add them the order object.
        CalculationResult totals = checkout.getTotals();
        checkoutHelper.setPrices(order, totals);
        checkoutHelper.addTotals(order, totals);
        if (order.getCouponCode() != null) {
            order.setDiscountCode(order.getCouponCode().getCode());
        }

        // Finally, save the order.
        Order savedOrder;
        Order oldOrder = null;
        Id oldOrderId = sessionGet(CheckoutConstant.SESSION_KEY_ORDER_ID);
        if (oldOrderId != null) {
            oldOrder = checkoutService.getOrder(oldOrderId);
            if ((oldOrder.getOrderStatus() == OrderStatus.NEW || oldOrder.getOrderStatus() == OrderStatus.PENDING)
                && (oldOrder.getOrderPayment() == null
                    || (oldOrder.getOrderPayment().getPaymentStatus() != PaymentStatus.AUTHORIZED
                        && oldOrder.getOrderPayment().getPaymentStatus() != PaymentStatus.PAID))) {
            } else {
                oldOrder = null;
            }
        }

        if (oldOrder == null) {
            order.setOrderNumber(app.nextIncrementId("order_number"));
            order.setOrderStatus(OrderStatus.NEW);
            savedOrder = (checkoutService).createOrder(order);
        } else {
            order.setOrderNumber(oldOrder.getOrderNumber());

            // order.setId(oldOrder.getId());
            // checkoutService.removeOrder(order);
            savedOrder = (checkoutService).createOrder(order);
        }
        sessionSet(CheckoutConstant.SESSION_KEY_ORDER_ID, savedOrder.getId());

        AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(paymentMethodCode);
        PaymentResponse response = null;
        if (useAuthorization() && paymentMethod.supportAuthorization()) {
            response = paymentMethod.authorizePayment(null, savedOrder, totals);
        } else {
            response = paymentMethod.processPayment(null, savedOrder, totals);
        }

        paymentService.processPayment(orderPayment, response);

        if (response.getUrl() == null) {
            if (response.getPaymentEventResponse().getErrorMessage() == null) {
                savedOrder = checkoutHelper.pendOrder(savedOrder);
                savedOrder = checkoutHelper.acceptOrder(savedOrder);
                checkoutHelper.clearSessionOfCheckout();
                checkoutHelper.putInfoForSuccessPage(savedOrder);
                checkoutHelper.sendConfirmationEmail(savedOrder);

                return redirect("/checkout/page/success");
            } else {
                return view("checkout/unsuccess");
            }
        } else {
            return redirect(response.getUrl());
        }
    }

    private boolean isCompleteCheckoutPayment() {
        return !Str.isEmpty(getCheckout().getPaymentMethod());
    }

    private boolean isCompleteCheckoutAddress() {
        if (getCheckout().getDeliveryAddress() == null)
            return false;

        if (getCheckout().getInvoiceAddress() == null)
            return false;

        return true;
    }

    private byte[] encryptPassword(String password, byte[] randomSalt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || randomSalt == null || randomSalt.length == 0)
            throw new NullPointerException("Password and/or random salt cannot be null");

        return Passwords.getEncryptedPassword(password, getSalt(randomSalt));
    }

    private byte[] getSalt(byte[] randomSalt) throws NoSuchAlgorithmException {
        if (randomSalt == null || randomSalt.length == 0)
            throw new NullPointerException("Random salt cannot be null");

        byte[] sugar = MerchantConfig.GET.val(MerchantConfig.FRONTEND_SECURITY_SUGAR).getBytes();

        if (sugar == null || sugar.length == 0)
            throw new NullPointerException("Merchant sugar cannot be null");

        return Passwords.merge(randomSalt, sugar);
    }

    private void setShippingPackages(Order order, Checkout checkout) {

        List<ShippingOption> shippingOptions = checkout.getDeliveryEstimationOptions();

        for (ShippingOption shippingOption : shippingOptions) {
            OrderShipment shipment = app.model(OrderShipment.class).belongsTo(order)
                .setCarrierCode(shippingOption.getCarrierCode()).setOptionCode(shippingOption.getOptionCode())
                .setShippingAmount(shippingOption.getRate()).setOptionName(shippingOption.getName());

            order.addOrderShipment(shipment);

            if (shippingOption.getShippingPackage() != null) {

                List<OrderShipmentItem> orderShipmentItemList = new ArrayList<>();
                for (ShippingItem shippingItem : shippingOption.getShippingPackage().getShippingItems()) {
                    OrderItem orderItem = order.getOrderItems().stream()
                        .filter(x -> x.getProductId().equals(shippingItem.getProductId())).findFirst().get();

                    OrderShipmentItem orderShipmentItem = app.model(OrderShipmentItem.class);
                    orderShipmentItem.setId(app.nextId());
                    orderShipmentItem.setOrderItemId(orderItem.getId());

                    orderShipmentItemList.add(orderShipmentItem);
                }
                shipment.setShipmentItems(orderShipmentItemList);
            }

            List<OrderShipmentOption> shippingOptionList = new ArrayList<>();
            OrderShipmentOption option = app.model(OrderShipmentOption.class);

            option.setName(shippingOption.getName());
            option.setCarrier(shippingOption.getCarrierCode());
            option.setOption(shippingOption.getOptionCode());
            option.setAmount(shippingOption.getRate());
            option.belongsTo(order.getOrderShipment());

            shippingOptionList.add(option);
            shipment.setShippingOptions(shippingOptionList);
        }

    }

    @Request("/success")
    public Result success() throws Exception {
        return view("checkout/page/success");
    }

    public Cart getPreviewCart() {
        return getCart();
    }

    public Checkout getPreviewCheckout() {
        return getCheckout(false);
    }

    @Override
    public CheckoutForm getForm() {
        return formHelper.getForm();
    }

    public void setForm(CheckoutForm form) {
        formHelper.setForm(form);
    }

}
