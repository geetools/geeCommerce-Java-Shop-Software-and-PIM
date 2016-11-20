package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationData;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.core.payment.AbstractPaymentMethod;
import com.geecommerce.core.payment.PaymentHelper;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.shipping.converter.ShippingPackageConverter;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.service.ShippingService;
import com.google.inject.Inject;

@Model("checkouts")
public class DefaultCheckout extends AbstractModel implements Checkout, ShippingPackageConverter {
    private static final long serialVersionUID = -4947750595010802597L;
    private Id id = null;
    private Id requestContextId = null;
    private Id cartId = null;
    private Id customerId = null;
    private Boolean active = null;

    private String salutation = null;
    private String firstName = null;
    private String lastName = null;
    private String phone = null;
    private String phoneCode = null;
    private String email = null;

    private CheckoutAddress deliveryAddress = null;
    private CheckoutAddress invoiceAddress = null;

    private String paymentMethod = null;
    private Double paymentRateAmount = null;

    private String shippingCarrier = null;
    private String shippingOption = null;
    private String shippingOptionName = null;
    private Double shippingAmount = null;

    private List<String> additionalShippingOptions = null;
    private List<String> additionalShippingAmounts = null;

    private Date createdOn = null;
    private Date modifiedOn = null;

    private CouponCode couponCode = null;
    private Id couponCodeId = null;
    private Id storeId = null;
    private String note = null;

    Map<String, String> paymentParameters = new HashMap<>();

    private CalculationResult cartTotals = null;

    private final CalculationService calculationService;
    private final CalculationHelper calculationHelper;
    private final CouponService couponService;
    private final CartService cartService;
    private final ShippingService shippingService;

    @Inject
    public DefaultCheckout(CalculationService calculationService, CalculationHelper calculationHelper,
        CouponService couponService, CartService cartService, ShippingService shippingService) {
        this.calculationService = calculationService;
        this.calculationHelper = calculationHelper;
        this.couponService = couponService;
        this.cartService = cartService;
        this.shippingService = shippingService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Checkout setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public Checkout fromRequestContext(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            throw new IllegalStateException("RequestContext cannot be null");

        this.requestContextId = requestContext.getId();
        return this;
    }

    @Override
    public Id getCartId() {
        return cartId;
    }

    private Cart getCart() {
        return cartService.getCart(getCartId());
    }

    @Override
    public Checkout fromCart(Cart cart) {
        if (cart == null || cart.getId() == null)
            throw new IllegalStateException("Cart cannot be null");
        this.setCouponCode(cart.getCouponCode());
        this.cartId = cart.getId();
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Checkout belongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            throw new IllegalStateException("Customer cannot be null");

        this.customerId = customer.getId();
        return this;
    }

    public String getSalutation() {
        return salutation;
    }

    public Checkout setSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public Checkout setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Checkout setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public Checkout setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String getPhoneCode() {
        return phoneCode;
    }

    @Override
    public Checkout setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
        return this;
    }

    @Override
    public String getFullPhone() {
        if (!StringUtils.isBlank(getPhoneCode()))
            return getPhoneCode() + getPhone();
        return getPhone();
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Checkout setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public CheckoutAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    @Override
    public Checkout setDeliveryAddress(CheckoutAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    @Override
    public CheckoutAddress getInvoiceAddress() {
        return invoiceAddress;
    }

    @Override
    public Checkout setInvoiceAddress(CheckoutAddress invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
        return this;
    }

    @Override
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public String getPaymentMethodName() {
        AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(getPaymentMethod());
        if (paymentMethod != null) {
            return paymentMethod.getLabel();
        }
        return "";
    }

    @Override
    public Checkout setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    @Override
    public Double getPaymentRateAmount() {
        if (paymentRateAmount == null)
            paymentRateAmount = 0.0;
        return paymentRateAmount;
    }

    @Override
    public Checkout setPaymentRateAmount(Double paymentRateAmount) {
        this.paymentRateAmount = paymentRateAmount;
        return this;
    }

    @Override
    public Map<String, String> getPaymentParameters() {
        return paymentParameters;
    }

    @Override
    public Checkout setPaymentParameters(Map<String, String> paymentParameters) {
        this.paymentParameters = paymentParameters;
        return this;
    }

    @Override
    public String getShippingCarrier() {
        return shippingCarrier;
    }

    @Override
    public Checkout setShippingCarrier(String shippingCarrier) {
        this.shippingCarrier = shippingCarrier;
        return this;
    }

    @Override
    public String getShippingOption() {
        return shippingOption;
    }

    @Override
    public Checkout setShippingOption(String shippingOption) {
        this.shippingOption = shippingOption;
        return this;
    }

    @Override
    public String getShippingOptionName() {
        return shippingOptionName;
    }

    @Override
    public Checkout setShippingOptionName(String shippingOptionName) {
        this.shippingOptionName = shippingOptionName;
        return this;
    }

    @Override
    public Double getShippingAmount() {
        return shippingAmount;
    }

    @Override
    public Double getTotalShippingAmount() {
        return shippingAmount;
    }

    @Override
    public Checkout setShippingAmount(Double shippingAmount) {
        this.shippingAmount = shippingAmount;
        return this;
    }

    @Override
    public boolean isActive() {
        return active == null ? false : active;
    }

    @Override
    public Checkout activate() {
        this.active = true;
        return this;
    }

    @Override
    public Checkout deactivate() {
        this.active = false;
        return this;
    }

    @Override
    public Checkout setCouponCode(CouponCode couponCode) {
        this.couponCode = couponCode;
        if (couponCode == null)
            couponCodeId = null;
        else
            couponCodeId = couponCode.getId();
        return this;
    }

    @Override
    public CouponCode getCouponCode() {
        if (couponCode == null && couponCodeId != null) {
            CouponService couponService = app.service(CouponService.class);
            couponCode = couponService.getCouponCode(couponCodeId);
        }
        return couponCode;
    }

    @Override
    public Checkout setNote(String note) {
        this.note = note;
        return this;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public Checkout setStoreId(Id storeId) {
        this.storeId = storeId;
        return this;
    }

    @Override
    public Id getStoreId() {
        return storeId;
    }

    public Double getDeliveryEstimation() {
        return getCart().getDeliveryEstimation();
    }

    @Override
    public List<ShippingOption> getDeliveryEstimationOptions() {
        return getCart().getDeliveryEstimationOptions();
    }

    private CalculationResult calculateTotals() {
        Cart cart = getCart();

        CartAttributeCollection cartAttributeCollection = ((CouponData) cart).toCartAttributeCollection();
        CouponCode code = couponService.maintainCouponCodesList(cart.getCouponCode(), cartAttributeCollection,
            cart.getUseAutoCoupon());
        cart.setCouponCode(code);
        cartService.updateCart(cart);

        // Create new calculation context (with configuration properties and
        // cart items)
        CalculationContext calcCtx = calculationHelper.newCalculationContext((CalculationData) cart);

        Double shippingAmount = 0.0;
        if (getShippingAmount() == null) {
            shippingAmount = getDeliveryEstimation();
        } else {
            shippingAmount = getTotalShippingAmount();
        }

        calcCtx.addParameter(ParamKey.SHIPPING_AMOUNT, shippingAmount);

        Double paymentRateAmount = getPaymentRateAmount();
        calcCtx.addParameter(ParamKey.PAYMENT_RATE_AMOUNT, paymentRateAmount);

        couponService.applyDiscount(calcCtx, cart.getCouponCode(), cartAttributeCollection);

        // Now that we have all the information, we can pass it onto the
        // calculationService to calculate all the totals.
        CalculationResult cr = null;

        try {
            cr = calculationService.calculateTotals(calcCtx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cr;
    }

    @Override
    public CalculationResult getTotals() {
        // if(cartTotals == null)
        cartTotals = calculateTotals();
        return cartTotals;
    }

    @Override
    public void clearTotals() {
        cartTotals = null;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    private boolean isAllNull(CheckoutAddress address) {
        if (address.getFirstName() == null && address.getLastName() == null && address.getAddress1() == null
            && address.getAddress2() == null && address.getCity() == null && address.getCountry() == null
            && address.getZip() == null)
            return true;
        return false;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.requestContextId = id_(map.get(Column.REQUEST_CONTEXT_ID));
        this.cartId = id_(map.get(Column.CART_ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));

        this.phone = str_(map.get(Column.PHONE));
        this.phoneCode = str_(map.get(Column.PHONE_CODE));
        this.firstName = str_(map.get(Column.FIRST_NAME));
        this.lastName = str_(map.get(Column.LAST_NAME));
        this.email = str_(map.get(Column.EMAIL));

        CheckoutAddress cad = app.model(CheckoutAddress.class);
        cad.fromMap(map, "del_");
        if (!isAllNull(cad)) {
            this.deliveryAddress = cad;
        }

        CheckoutAddress cai = app.model(CheckoutAddress.class);
        cai.fromMap(map, "inv_");
        if (!isAllNull(cai)) {
            this.invoiceAddress = cai;
        }
        this.paymentMethod = str_(map.get(Column.PAYMENT_METHOD));
        this.paymentRateAmount = double_(map.get(Column.PAYMENT_RATE_AMOUNT), 0);
        this.paymentParameters = map_(map.get(Column.PAYMENT_PARAMETERS));

        this.shippingCarrier = str_(map.get(Column.SHIPPING_CARRIER));
        this.shippingCarrier = str_(map.get(Column.SHIPPING_OPTION));
        this.shippingAmount = double_(map.get(Column.SHIPPING_AMOUNT));
        this.shippingOptionName = str_(map.get(Column.SHIPPING_OPTION_NAME));

        this.couponCodeId = id_(map.get(Column.COUPON_CODE));
        this.storeId = id_(map.get(Column.STORE));
        this.note = str_(map.get(Column.NOTE));

        this.createdOn = date_(map.get(Column.CREATED_ON));
        this.modifiedOn = date_(map.get(Column.CREATED_ON));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.REQUEST_CONTEXT_ID, getRequestContextId());
        m.put(Column.CART_ID, getCartId());

        m.put(Column.PHONE, getPhone());
        m.put(Column.PHONE_CODE, getPhoneCode());
        m.put(Column.FIRST_NAME, getFirstName());
        m.put(Column.LAST_NAME, getLastName());
        m.put(Column.EMAIL, getEmail());

        if (getCustomerId() != null) {
            m.put(Column.CUSTOMER_ID, getCustomerId());
        }

        CheckoutAddress cad = getDeliveryAddress();
        if (cad != null) {
            Map<String, Object> md = cad.toMap("del_");
            m.putAll(md);
        }

        CheckoutAddress cai = getInvoiceAddress();
        if (cai != null) {
            Map<String, Object> mi = cai.toMap("inv_");
            m.putAll(mi);
        }

        m.put(Column.PAYMENT_METHOD, getPaymentMethod());
        m.put(Column.PAYMENT_RATE_AMOUNT, getPaymentRateAmount());
        m.put(Column.PAYMENT_PARAMETERS, getPaymentParameters());

        m.put(Column.SHIPPING_CARRIER, getShippingCarrier());
        m.put(Column.SHIPPING_OPTION, getShippingOption());
        m.put(Column.SHIPPING_OPTION_NAME, getShippingOptionName());
        m.put(Column.SHIPPING_AMOUNT, getShippingAmount());

        m.put(Column.COUPON_CODE, couponCodeId);
        m.put(Column.STORE, getStoreId());
        m.put(Column.NOTE, getNote());

        m.put(Column.ACTIVE, isActive());
        m.put(Column.MODIFIED_ON, getModifiedOn());
        m.put(Column.CREATED_ON, getCreatedOn());

        return m;
    }

    @Override
    public List<ShippingPackage> toShippingPackages() {
        return ((ShippingPackageConverter) getCart()).toShippingPackages();
    }
}
