package com.geecommerce.checkout.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationItemResult;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.ResultItemKey;
import com.geecommerce.calculation.model.ResultKey;
import com.geecommerce.cart.CartConstant;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.form.AddressForm;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.CheckoutAddress;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.repository.Checkouts;
import com.geecommerce.checkout.repository.Orders;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.core.App;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;
import com.geecommerce.inventory.repository.Stocks;
import com.geecommerce.mailer.service.MailerService;
import com.geecommerce.shipping.converter.ShippingPackageConverter;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.service.ShippingService;
import com.google.inject.Inject;

@Helper
public class DefaultCheckoutHelper implements CheckoutHelper {
    @Inject
    protected App app;

    protected final Checkouts checkouts;
    protected final CheckoutService checkoutService;
    protected final Orders orders;
    protected final CouponService couponService;
    protected final MailerService mailerService;
    protected final CartService cartService;
    protected final Stocks stocks;

    @Inject
    public DefaultCheckoutHelper(Checkouts checkouts, CheckoutService checkoutService, Orders orders,
        CouponService couponService, MailerService mailerService, CartService cartService, Stocks stocks) {
        this.checkouts = checkouts;
        this.checkoutService = checkoutService;
        this.orders = orders;
        this.couponService = couponService;
        this.mailerService = mailerService;
        this.cartService = cartService;
        this.stocks = stocks;
    }

    public Order convertCartToOrder(Cart cart) throws Exception {
        Order order = app.model(Order.class);

        List<CartItem> cartItems = cart.getActiveCartItems();

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = app.model(OrderItem.class);
            orderItem.setId(app.nextId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setArticleNumber(cartItem.getProduct().getArticleNumber());
            orderItem.setName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getProductPrice());
            orderItem.setPriceTypeId(cartItem.getProductPriceType().getId());
            orderItem.setTaxRate(cartItem.getProductTaxRate());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setCouponCode(cart.getCouponCode());

        return order;
    }

    @Override
    public void addTotals(Order order, CalculationResult totals) {
        order.setTotalAmount(totals.getDouble(ResultKey.GROSS_GRAND_TOTAL));
        order.setCalculationResult(totals);

        Map<Id, CalculationItemResult> orderItemResult = totals.getItemResults();
        List<OrderItem> items = order.getOrderItems();

        for (OrderItem orderItem : items) {
            CalculationItemResult orderItemTotals = orderItemResult.get(orderItem.getProductId());
            orderItem.setTotalRowPrice(orderItemTotals.getDouble(ResultItemKey.ITEM_GROSS_SUBTOTAL));
        }
    }

    public AddressForm formAddressFromCheckoutAddress(CheckoutAddress checkoutAddress) {
        AddressForm addressForm = new AddressForm();
        addressForm.setSalutation(checkoutAddress.getSalutation());
        addressForm.setFirstName(checkoutAddress.getFirstName());
        addressForm.setLastName(checkoutAddress.getLastName());
        addressForm.setAddress1(checkoutAddress.getAddress1());
        addressForm.setAddress2(checkoutAddress.getAddress2());
        addressForm.setHouseNumber(checkoutAddress.getHouseNumber());
        addressForm.setZip(checkoutAddress.getZip());
        addressForm.setCity(checkoutAddress.getCity());
        addressForm.setCountry(checkoutAddress.getCountry());
        addressForm.setPhone(checkoutAddress.getPhone());
        return addressForm;
    }

    public OrderAddress orderAddressFromFormAddress(AddressForm addressForm) {
        OrderAddress address = app.model(OrderAddress.class);
        address.setSalutation(addressForm.getSalutation());
        address.setFirstName(addressForm.getFirstName());
        address.setLastName(addressForm.getLastName());
        address.setAddress1(addressForm.getAddress1());
        address.setAddress2(addressForm.getAddress2());
        address.setHouseNumber(addressForm.getHouseNumber());
        address.setZip(addressForm.getZip());
        address.setCity(addressForm.getCity());
        address.setCountry(addressForm.getCountry());
        address.setPhone(addressForm.getPhone());
        return address;
    }

    public CheckoutAddress checkoutAddressFromFormAddress(AddressForm addressForm) {
        CheckoutAddress address = app.model(CheckoutAddress.class);
        address.setSalutation(addressForm.getSalutation());
        address.setFirstName(addressForm.getFirstName());
        address.setLastName(addressForm.getLastName());
        address.setAddress1(addressForm.getAddress1());
        address.setAddress2(addressForm.getAddress2());
        address.setHouseNumber(addressForm.getHouseNumber());
        address.setZip(addressForm.getZip());
        address.setCity(addressForm.getCity());
        address.setCountry(addressForm.getCountry());
        address.setPhone(addressForm.getPhone());
        address.setEmail(addressForm.getEmail());
        return address;
    }

    public Address customerAddressFromFormAddress(AddressForm addressForm) {
        Address address = app.model(Address.class);
        address.setSalutation(addressForm.getSalutation());
        address.setForename(addressForm.getFirstName());
        address.setSurname(addressForm.getLastName());
        address.setAddressLines(addressForm.getAddress1(), addressForm.getAddress2());
        address.setHouseNumber(addressForm.getHouseNumber());
        address.setZip(addressForm.getZip());
        address.setCity(addressForm.getCity());
        address.setCountry(addressForm.getCountry());
        address.setTelephone(addressForm.getPhone());
        return address;
    }

    public void addAddressesToOrder(Order order, CheckoutForm form) {
        OrderAddress address = orderAddressFromFormAddress(form.getInvoice());
        order.setInvoiceOrderAddress(address);

        if (form.getCustomDelivery()) {
            address = orderAddressFromFormAddress(form.getDelivery());
        } else {
            address = orderAddressFromFormAddress(form.getInvoice());
        }

        order.setDeliveryOrderAddress(address);
    }

    public List<Address> getCustomerAddresses(CheckoutForm form) {
        List<Address> addresses = new ArrayList<>();
        addresses.add(customerAddressFromFormAddress(form.getInvoice()));
        if (form.getCustomDelivery())
            addresses.add(customerAddressFromFormAddress(form.getDelivery()));
        return addresses;
    }

    @Override
    public Checkout getCheckout(Cart cart) {
        return getCheckout(cart, false);
    }

    @Override
    public Checkout getCheckout(Cart cart, boolean createIfNotExists) {
        Checkout checkout = app.sessionGet(CheckoutConstant.SESSION_KEY_CHECKOUT);

        if (checkout == null) {

            checkout = app.model(Checkout.class);
            checkout.fromRequestContext(app.context().getRequestContext()).fromCart(cart);

            if (app.isCustomerLoggedIn()) {
                Customer customer = (Customer) app.getLoggedInCustomer();
                checkout.belongsTo(customer);
                checkout.setSalutation(customer.getSalutation());
                checkout.setFirstName(customer.getForename());
                checkout.setLastName(customer.getSurname());
                checkout.setPhone(customer.getPhone());
                checkout.setEmail(customer.getEmail());
            }

            checkout = checkoutService.createCheckout(checkout);

            if (checkout != null && checkout.getId() != null) {
                app.sessionSet(CheckoutConstant.SESSION_KEY_CHECKOUT, checkout);
            }
        }

        return checkout;
    }

    public void addAddressesToCheckout(Checkout checkout, CheckoutForm form) {
        CheckoutAddress address = checkoutAddressFromFormAddress(form.getInvoice());
        checkout.setInvoiceAddress(address);

        if (form.getCustomDelivery()) {
            address = checkoutAddressFromFormAddress(form.getDelivery());
        } else {
            address = checkoutAddressFromFormAddress(form.getInvoice());
        }

        checkout.setDeliveryAddress(address);
    }

    @Override
    public void addShippingToCheckout(Checkout checkout, Cart cart, CheckoutForm form, ShippingService shippingService,
        Double totalAmount) {
        String[] codes = form.getCarrierCode().split("\\|");
        checkout.setShippingCarrier(codes[0]).setShippingOption(codes[1]);

        // TODO: now supports only one shipping package
        // ShippingPackage shippingData = ((ShippingPackageConverter)
        // cart).toShippingData();
        ShippingPackage shippingData = ((ShippingPackageConverter) checkout).toShippingPackages().get(0);
        // shippingData.setShippingAddress(shippingDataAddress.getShippingAddress());

        ShippingOption shippingOption = shippingService.getShippingOption(shippingData, checkout.getShippingCarrier(),
            checkout.getShippingOption());
        checkout.setShippingAmount(shippingOption.getRate());
        checkout.setShippingOptionName(shippingOption.getName());
    }

    @Override
    public void addShipmentToOrder(Order order, Checkout checkout) {
        OrderShipment shipment = app.model(OrderShipment.class).belongsTo(order)
            .setCarrierCode(checkout.getShippingCarrier()).setOptionCode(checkout.getShippingOption())
            .setShippingAmount(checkout.getTotalShippingAmount()).setOptionName(checkout.getShippingOptionName());

        order.addOrderShipment(shipment);
    }

    @Override
    public boolean canBePended(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.NEW)) {
            return true;
        }
        return false;
    }

    @Override
    public Order pendOrder(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.NEW)) {
            order.setOrderStatus(OrderStatus.PENDING);
            orders.update(order);
            return order;
        } else {
            throw new RuntimeException("Order has wrong status and can't be pended " + order.getId());
        }
    }

    @Override
    public boolean canBeAccepted(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.PENDING)
            && (order.getOrderPayment().getPaymentStatus().equals(PaymentStatus.PAID)
                || order.getOrderPayment().getPaymentStatus().equals(PaymentStatus.AUTHORIZED))) {
            return true;
        }
        return false;
    }

    @Override
    public void setPrices(Order order, CalculationResult calculationResult) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Id productId = orderItem.getProductId();
            Double price = calculationResult.getItemResult(productId).getDouble("cart_price");
            orderItem.setPrice(price);
        }
    }

    @Override
    public Order acceptOrder(Order order) {
        if (order.getCouponCode() != null) {
            couponService.useCoupon(order.getCouponCode(), order.getId(), order.getCustomerId());
        }
        order.setOrderStatus(OrderStatus.ACCEPTED);
        orders.update(order);

        try {
            List<OrderItem> orderItemsList = order.getOrderItems();

            if (orderItemsList != null && orderItemsList.size() > 0) {
                for (OrderItem orderItem : orderItemsList) {
                    // decrement quantity from inventory stock
                    stocks.decrementQty(orderItem.getProductId(), app.context().getStore(),
                        orderItem.getQuantity());
                }
            }
        } catch (QuantityNotAvailableException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public Order rejectOrderPayment(Order order) {
        order.setOrderStatus(OrderStatus.REJECTED);
        orders.update(order);
        return order;
    }

    @Override
    public void sendConfirmationEmail(Order order) {
        try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("order", order);
            mailerService.sendMail("first_order_confirmation", order.getInvoiceOrderAddress().getEmail(),
                templateParams);
        }
        // We do not want the order to fail just because there was a problem
        // sending an email. Improve.
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void clearSessionOfCheckout() {

        if (app.isCustomerLoggedIn()) {
            Cart cart = app.sessionGet(CartConstant.SESSION_KEY_CART);
            if (cart != null) {
                cart.setEnabled(false);
                cartService.updateCart(cart);
            }
        }
        app.cookieUnset(CartConstant.COOKIE_KEY_CART_ID);
        app.sessionRemove(CartConstant.SESSION_KEY_CART);

        app.sessionRemove(CheckoutConstant.SESSION_KEY_CHECKOUT_FORM);
        app.sessionRemove(CheckoutConstant.SESSION_KEY_CHECKOUT);

        app.sessionRemove(CheckoutConstant.SESSION_KEY_ORDER_ID);
    }

    @Override
    public void putInfoForSuccessPage(Order order) {
        app.sessionSet(CheckoutConstant.SESSION_KEY_SAVED_ORDER, order.getId());
    }

    @Override
    public AddressForm fromAddress(Address address) {
        AddressForm addressForm = new AddressForm();
        addressForm.setSalutation(address.getSalutation());
        addressForm.setFirstName(address.getForename());
        addressForm.setLastName(address.getSurname());
        List<String> lines = address.getAddressLines();
        if (lines != null && lines.size() >= 1)
            addressForm.setAddress1(lines.get(0));
        if (lines != null && lines.size() >= 2)
            addressForm.setAddress2(lines.get(1));
        addressForm.setHouseNumber(address.getHouseNumber());
        addressForm.setZip(address.getZip());
        addressForm.setCity(address.getCity());
        addressForm.setCountry(address.getCountry());
        return addressForm;
    }
}
