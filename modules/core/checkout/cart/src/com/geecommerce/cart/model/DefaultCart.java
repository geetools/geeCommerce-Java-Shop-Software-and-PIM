package com.geecommerce.cart.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationData;
import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.calculation.model.ResultItemKey;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.cart.configuration.Key;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.cart.shipping.PackageSplitter;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.price.helper.PriceHelper;
import com.geecommerce.price.pojo.PricingContext;
import com.geecommerce.shipping.converter.ShippingPackageConverter;
import com.geecommerce.shipping.model.ShippingItem;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.service.ShippingService;
import com.google.inject.Inject;

@Model("carts")
public class DefaultCart extends AbstractModel implements Cart, CalculationData, ShippingPackageConverter, CouponData {
    private static final long serialVersionUID = -8507651238634426904L;
    private Id id = null;
    private Id requestContextId = null;
    private Id customerId = null;
    private Date createdOn = null;
    private Date modifiedOn = null;
    private Boolean enabled = null;
    private CouponCode couponCode = null;
    private Id couponCodeId = null;
    private boolean useAutoCoupon = true;
    private CalculationResult cartTotals = null;
    private CalculationResult cartTotalsNoShipping = null;
    // private boolean needToRecalculating = true;

    private List<CartItem> cartItems = new ArrayList<>();

    private final CalculationService calculationService;
    private final CalculationHelper calculationHelper;
    private final CouponService couponService;
    private final CartService cartService;
    private final ShippingService shippingService;
    private final PriceHelper priceHelper;

    @Inject
    public DefaultCart(CalculationService calculationService, CalculationHelper calculationHelper,
                       CouponService couponService, CartService cartService, ShippingService shippingService, PriceHelper priceHelper) {
        this.calculationService = calculationService;
        this.calculationHelper = calculationHelper;
        this.couponService = couponService;
        this.cartService = cartService;
        this.shippingService = shippingService;
        this.priceHelper = priceHelper;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Cart setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public Cart fromRequestContext(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            throw new IllegalStateException("RequestContext cannot be null");

        this.requestContextId = requestContext.getId();
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Cart belongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            throw new IllegalStateException("Customer cannot be null");

        this.customerId = customer.getId();
        return this;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public CartItem addProduct(Product product, Product bundle) {
        return addProduct(product, bundle, null, true);
    }

    @Override
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @Override
    public List<CartItem> getActiveCartItems() {
        if (cartItems == null)
            return null;
        return cartItems.stream().filter(item -> item.isActive()).collect(Collectors.toList());
    }

    @Override
    public int getTotalQuantity() {
        int totalQty = 0;

        for (CartItem item : cartItems) {
            totalQty += item.getQuantity();
        }

        return totalQty;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public Cart setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Cart setCouponCode(CouponCode couponCode) {
        this.couponCode = couponCode;
        if (couponCode == null)
            couponCodeId = null;
        else
            couponCodeId = couponCode.getId();

        clearTotals();

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
    public boolean getUseAutoCoupon() {
        return useAutoCoupon;
    }

    @Override
    public Cart setUseAutoCoupon(boolean useAutoCoupon) {
        this.useAutoCoupon = useAutoCoupon;

        clearTotals();

        return this;
    }

    @Override
    public CartItem getLast() {
        if (cartItems == null)
            return null;

        if (cartItems.size() == 1)
            return cartItems.get(0);

        Optional<CartItem> o = cartItems.stream().filter(item -> item.isLast()).findFirst();

        return o.isPresent() ? o.get() : null;
    }

    // private
    /*
     * public Double getDeliveryEstimation(){ Double shippingAmount = 0.0;
     * 
     * ShippingPackage shippingData = ((ShippingPackageConverter)
     * getCart()).toShippingData(); ShippingOption shippingOption =
     * shippingService.getEstimatedShippingOptionForDefaultAddress(shippingData)
     * ;
     * 
     * if (shippingOption != null) shippingAmount = shippingOption.getRate();
     * 
     * return shippingAmount; }
     */public Double getDeliveryEstimation() {
        Double shippingAmount = 0.0;
        if (app.cpBool_(Key.INCLUDE_ESTIMATED_SHIPPING_AMOUNT, false)) {
            List<ShippingPackage> packages = toShippingPackages();
            if (packages != null) {
                for (ShippingPackage shippingPackage : packages) {
                    if (shippingPackage.getCalculateShipping()) {
                        ShippingOption shippingOption = shippingService
                            .getEstimatedShippingOptionForDefaultAddress(shippingPackage);

                        if (shippingOption != null)
                            shippingAmount += shippingOption.getRate();
                    }
                }
            }
            // shippingAmount = getDeliveryEstimation();
        }

        return shippingAmount;
    }

    @Override
    public List<ShippingOption> getDeliveryEstimationOptions() {
        List<ShippingOption> shippingOptions = new ArrayList<>();
        if (app.cpBool_(Key.INCLUDE_ESTIMATED_SHIPPING_AMOUNT, false)) {
            List<ShippingPackage> packages = toShippingPackages();
            for (ShippingPackage shippingPackage : packages) {

                ShippingOption shippingOption = shippingService
                    .getEstimatedShippingOptionForDefaultAddress(shippingPackage);

                if (shippingOption != null) {
                    shippingOptions.add(shippingOption);
                    shippingOption.setShippingPackage(shippingPackage);
                }

                if (!shippingPackage.getCalculateShipping()) {
                    shippingOption.setRate(0.0);
                }
            }
        }
        return shippingOptions;
    }

    private CalculationResult calculateTotals(Double shippingAmount) {
        CartAttributeCollection cartAttributeCollection = toCartAttributeCollection();

        CouponCode code = couponService.maintainCouponCodesList(getCouponCode(), cartAttributeCollection,
            getUseAutoCoupon());
        setCouponCode(code);

        if (getId() != null)
            cartService.updateCart(this);

        CalculationContext calcCtx = calculationHelper.newCalculationContext(this);

        // Double shippingAmount = getDeliveryEstimation();

        calcCtx.addParameter(ParamKey.SHIPPING_AMOUNT, shippingAmount);
        couponService.applyDiscount(calcCtx, getCouponCode(), cartAttributeCollection);

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

        if (cartTotals == null)
            cartTotals = calculateTotals(getDeliveryEstimation());
        return cartTotals;
    }

    @Override
    public CalculationResult getTotalsNoShipping() {
        if (cartTotalsNoShipping == null)
            cartTotalsNoShipping = calculateTotals(0.0);
        return cartTotalsNoShipping;
    }

    @Override
    public void clearTotals() {
        cartTotals = null;
        cartTotalsNoShipping = null;
    }

    @Override
    public CartItem addProduct(Product product) {
        return addProduct(product, null, null, true);
    }

    @Override
    public CartItem addProduct(Product product, Product bundle, String pickupStoreId, Boolean active) {
         //TODO: bundle support

        if (product == null || product.getId() == null)
            return null;
        
        CartItem cartItem = findItem(product.getId());

        if (cartItems != null) {
            cartItems.forEach((CartItem item) -> item.setLast(false));

            cartItems.forEach((CartItem item) -> item.setBundle(bundle));
        }

        if (cartItem == null) {
            cartItem = app.model(CartItem.class).setProduct(product).setBundle(bundle);

            cartItems.add(cartItem);
        }

        cartItem.incrementQty();
        cartItem.setLast(true);

        clearTotals();
        // needToRecalculating = true;
        return cartItem;
    }

    protected CartItem findItem(Id productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.requestContextId = id_(map.get(Column.REQUEST_CONTEXT_ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.createdOn = date_(map.get(Column.CREATED_ON));
        this.modifiedOn = date_(map.get(Column.MODIFIED_ON));
        this.enabled = bool_(map.get(Column.ENABLED));
        this.couponCodeId = id_(map.get(Column.COUPON_CODE));
        this.useAutoCoupon = bool_(map.get(Column.USE_AUTO_COUPON), true);

        List<Map<String, Object>> items = list_(map.get(Column.CART_ITEMS));

        if (items != null && items.size() > 0) {
            for (Map<String, Object> item : items) {
                CartItem cartItem = app.model(CartItem.class);
                cartItem.fromMap(item);
                this.cartItems.add(cartItem);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.ID, getId());
        m.put(Column.REQUEST_CONTEXT_ID, getRequestContextId());

        if (getCustomerId() != null) {
            m.put(Column.CUSTOMER_ID, getCustomerId());
        }

        m.put(Column.CREATED_ON, getCreatedOn());
        m.put(Column.MODIFIED_ON, getCreatedOn());
        m.put(Column.ENABLED, getEnabled());
        m.put(Column.COUPON_CODE, couponCodeId);
        m.put(Column.USE_AUTO_COUPON, useAutoCoupon);
        // m.put(Column.NEED_RECALCULATING, true);

        if (!cartItems.isEmpty()) {
            List<Map<String, Object>> l = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                l.add(cartItem.toMap());
            }

            m.put(Column.CART_ITEMS, l);
        } else {
            m.put(Column.CART_ITEMS, null);
        }

        return m;
    }

    @Override
    public Map<String, Object> toCalculationData() {
        Map<String, Object> calculationData = new HashMap<>();

        List<Id> products = new ArrayList<>();
        for(CartItem cartItem: getCartItems()){
            products.add(cartItem.getProductId());
        }
        Id[] withProducts = products.toArray(new Id[products.size()]);
        PricingContext pricingContext = priceHelper.getPricingContext(true);
        for (Id productId: products){
            pricingContext.setLinkedProductIds(productId, withProducts);
        }

        if (!getActiveCartItems().isEmpty()) {
            List<Map<String, Object>> items = new ArrayList<>();

            for (CartItem cartItem : getActiveCartItems()) {
                items.add(((CalculationItem) cartItem).toCalculationItem(pricingContext));
            }

            calculationData.put(CalculationData.FIELD.ITEMS, items);
        }

        return calculationData;
    }

    @Override
    public List<ShippingPackage> toShippingPackages() {
        if (cartItems == null || cartItems.size() == 0)
            return null;

        PackageSplitter packageSplitter = app.injectable(PackageSplitter.class);
        List<ShippingPackage> packages = packageSplitter.toShippingPackages(this);

        CalculationResult calculationResult = getTotalsNoShipping();

        for (ShippingPackage shippingPackage : packages) {
            Double totalAmount = 0.0;
            for (ShippingItem shippingItem : shippingPackage.getShippingItems()) {
                if (calculationResult.getItemResult(shippingItem.getProductId()).getInteger(ResultItemKey.ITEM_QUANTITY)
                    .equals(shippingItem.getQuantity())) {
                    totalAmount += calculationResult.getItemResult(shippingItem.getProductId())
                        .getDouble(ResultItemKey.ITEM_GROSS_SUBTOTAL);
                } else {
                    throw new UnsupportedOperationException("Doesn't support splitting shipping items");
                }
            }
            shippingPackage.setTotalAmount(totalAmount);
        }
        return packages;
    }

    @Override
    public CartAttributeCollection toCartAttributeCollection() {
        CartAttributeCollection cartAttributeCollection = app.model(CartAttributeCollection.class);
        CouponService couponService = app.service(CouponService.class);
        cartAttributeCollection.setCartAttributes(couponService.getCartAttributes(this));

        Map<Id, Map<String, AttributeValue>> cartItemAttributes = new HashMap<>();
        Map<Id, Map<String, AttributeValue>> productAttributes = new HashMap<>();

        for (CartItem cartItem : getActiveCartItems()) {
            Id key = cartItem.getProduct().getId();

            cartItemAttributes.put(key, couponService.getCartItemAttributes(cartItem));
            productAttributes.put(key, couponService.getProductAttributes(cartItem.getProduct()));
        }

        cartAttributeCollection.setCartItemAttributes(cartItemAttributes);
        cartAttributeCollection.setProductAttributes(productAttributes);

        return cartAttributeCollection;
    }
}
