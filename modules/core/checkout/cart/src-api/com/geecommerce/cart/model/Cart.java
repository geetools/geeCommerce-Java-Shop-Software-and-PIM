package com.geecommerce.cart.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.shipping.model.ShippingOption;

public interface Cart extends Model {
    public Id getId();

    public Cart setId(Id id);

    public Id getRequestContextId();

    public Cart fromRequestContext(RequestContext requestContext);

    public Id getCustomerId();

    public Cart belongsTo(Customer customer);

    public List<CartItem> getCartItems();

    public List<CartItem> getActiveCartItems();

    public Date getCreatedOn();

    public Date getModifiedOn();

    public CartItem addProduct(Product product);

    public CartItem addProduct(Product product, String pickupStore, Boolean active);

    public int getTotalQuantity();

    public Boolean getEnabled();

    public Cart setEnabled(Boolean enabled);

    public Cart setCouponCode(CouponCode couponCode);

    public CouponCode getCouponCode();

    public boolean getUseAutoCoupon();

    public Cart setUseAutoCoupon(boolean useAutoCoupon);

    public CartItem getLast();

    public CalculationResult getTotals();

    public CalculationResult getTotalsNoShipping();

    public void clearTotals();

    public Double getDeliveryEstimation();

    public List<ShippingOption> getDeliveryEstimationOptions();

    static final class Column {
        public static final String ID = "_id";
        public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String ENABLED = "enabled";
        public static final String CART_ITEMS = "items";
        public static final String CREATED_ON = "cr_on";
        public static final String MODIFIED_ON = "mod_on";
        public static final String COUPON_CODE = "coupon";
        public static final String USE_AUTO_COUPON = "use_auto_coupon";
    }
}
