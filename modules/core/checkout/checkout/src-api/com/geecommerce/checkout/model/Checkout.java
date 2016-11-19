package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.shipping.model.ShippingOption;

public interface Checkout extends Model {
    public Id getId();

    public Checkout setId(Id id);

    public Id getRequestContextId();

    public Checkout fromRequestContext(RequestContext requestContext);

    public Id getCartId();

    public Checkout fromCart(Cart cart);

    public Id getCustomerId();

    public Checkout belongsTo(Customer customer);

    public String getSalutation();

    public Checkout setSalutation(String salutation);

    public String getFirstName();

    public Checkout setFirstName(String firstName);

    public String getLastName();

    public Checkout setLastName(String lastName);

    public String getPhone();

    public Checkout setPhone(String phone);

    public String getPhoneCode();

    public Checkout setPhoneCode(String phoneCode);

    public String getFullPhone();

    public String getEmail();

    public Checkout setEmail(String email);

    public CheckoutAddress getDeliveryAddress();

    public Checkout setDeliveryAddress(CheckoutAddress deliveryAddress);

    public CheckoutAddress getInvoiceAddress();

    public Checkout setInvoiceAddress(CheckoutAddress invoiceAddress);

    public String getPaymentMethod();

    public String getPaymentMethodName();

    public Checkout setPaymentMethod(String paymentMethod);

    public Double getPaymentRateAmount();

    public Checkout setPaymentRateAmount(Double paymentRateAmount);

    public Map<String, String> getPaymentParameters();

    public Checkout setPaymentParameters(Map<String, String> paymentParameters);

    public Double getShippingAmount();

    public Double getTotalShippingAmount();

    public Checkout setShippingAmount(Double shippingAmount);

    public String getShippingCarrier();

    public Checkout setShippingCarrier(String shippingCarrier);

    public String getShippingOption();

    public Checkout setShippingOption(String shippingOption);

    public String getShippingOptionName();

    public Checkout setShippingOptionName(String shippingOptionName);

    public Date getCreatedOn();

    public Date getModifiedOn();

    public boolean isActive();

    public Checkout activate();

    public Checkout deactivate();

    public Checkout setCouponCode(CouponCode couponCode);

    public CouponCode getCouponCode();

    public Checkout setNote(String note);

    public String getNote();

    public Checkout setStoreId(Id storeId);

    public Id getStoreId();

    public CalculationResult getTotals();

    public void clearTotals();

    public Double getDeliveryEstimation();

    public List<ShippingOption> getDeliveryEstimationOptions();

    static final class Column {
	public static final String ID = "_id";
	public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
	public static final String CART_ID = "cart_id";
	public static final String CUSTOMER_ID = "customer_id";
	public static final String ACTIVE = "active";

	public static final String SALUTATION = "salutation";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PHONE = "phone";
	public static final String PHONE_CODE = "phone_code";
	public static final String EMAIL = "email";

	public static final String PAYMENT_METHOD = "pay_method";
	public static final String PAYMENT_RATE_AMOUNT = "pay_rate_amount";
	public static final String PAYMENT_PARAMETERS = "pay_params";

	public static final String SHIPPING_CARRIER = "ship_carrier";
	public static final String SHIPPING_OPTION = "ship_option";
	public static final String SHIPPING_AMOUNT = "ship_amount";
	public static final String SHIPPING_OPTION_NAME = "ship_option_name";

	public static final String CREATED_ON = "cr_on";
	public static final String MODIFIED_ON = "mod_on";

	public static final String COUPON_CODE = "coupon";
	public static final String STORE = "store";
	public static final String NOTE = "note";
    }
}
