package com.geecommerce.coupon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.helper.CouponHelper;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponCodePattern;
import com.geecommerce.coupon.model.CouponFilterAttribute;
import com.geecommerce.coupon.model.CouponUsage;
import com.geecommerce.coupon.processor.CouponProcessor;
import com.geecommerce.coupon.processor.DefaultBuyXGetYCouponProcessor;
import com.geecommerce.coupon.processor.DefaultBuyXGetYSameCouponProcessor;
import com.geecommerce.coupon.processor.DefaultFixedCartCouponProcessor;
import com.geecommerce.coupon.processor.DefaultFixedProductCouponProcessor;
import com.geecommerce.coupon.processor.DefaultListFixedProductCouponProcessor;
import com.geecommerce.coupon.processor.DefaultListPercentProductCouponProcessor;
import com.geecommerce.coupon.processor.DefaultPercentCartCouponProcessor;
import com.geecommerce.coupon.processor.DefaultPercentProductCouponProcessor;
import com.geecommerce.coupon.processor.DefaultRangeFixedCartCouponProcessor;
import com.geecommerce.coupon.processor.DefaultRangePercentCartCouponProcessor;
import com.geecommerce.coupon.processor.DefaultShippingCouponProcessor;
import com.geecommerce.coupon.processor.DefaultSpendXGetYCouponProcessor;
import com.geecommerce.coupon.repository.CouponCodePatterns;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.coupon.repository.CouponFilterAttributes;
import com.geecommerce.coupon.repository.CouponScriplets;
import com.geecommerce.coupon.repository.Coupons;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.price.repository.PriceTypes;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import groovy.lang.GroovyClassLoader;

@Service
public class DefaultCouponService implements CouponService {
    @Inject
    protected App app;

    protected static final String CART_FQN = "com.geecommerce.cart.model.Cart";
    protected static final String CART_ITEM_FQN = "com.geecommerce.cart.model.CartItem";
    protected static final String ORDER_FQN = "com.geecommerce.checkout.model.Order";
    protected static final String ORDER_ITEM_FQN = "com.geecommerce.checkout.model.OrderItem";

    protected final Coupons coupons;
    protected final CouponCodes couponCodes;
    protected final CustomerService customerService;
    protected final CouponFilterAttributes couponFilterAttributes;
    protected final CouponScriplets couponScriplets;
    protected final FilterService filterService;
    protected final CouponHelper couponHelper;
    protected final CouponCodePatterns couponCodePatterns;
    protected final ProductService productService;
    protected final PriceTypes priceTypes;
    protected List<CouponProcessor> L;

    protected static final String CONTEXT_KEY = "ctx";

    protected static final GroovyClassLoader GCL = new GroovyClassLoader(CouponService.class.getClassLoader());

    protected static final Set<String> groovyImports = new HashSet<>();
    static {
        groovyImports.add("com.geecommerce.catalog.product.*\n");
    }

    @Inject
    public DefaultCouponService(Coupons coupons, CouponCodes couponCodes, CouponFilterAttributes couponFilterAttributes, CouponScriplets couponScriplets, FilterService filterService,
        CouponHelper couponHelper, CouponCodePatterns couponCodePatterns, CustomerService customerService, ProductService productService, PriceTypes priceTypes) {
        this.coupons = coupons;
        this.couponCodes = couponCodes;
        this.couponFilterAttributes = couponFilterAttributes;
        this.couponScriplets = couponScriplets;
        this.filterService = filterService;
        this.couponHelper = couponHelper;
        this.couponCodePatterns = couponCodePatterns;
        this.customerService = customerService;
        this.productService = productService;
        this.priceTypes = priceTypes;
    }

    @Override
    public Coupon getCoupon(Id couponId) {
        return coupons.findById(Coupon.class, couponId);
    }

    @Override
    public CouponCode getCouponCode(String code) {
        return couponCodes.byCode(code);
    }

    @Override
    public CouponCode getCouponCode(Id id) {
        return couponCodes.findById(CouponCode.class, id);
    }

    @Override
    public Boolean isCouponApplicableToCart(CouponCode couponCode, CartAttributeCollection cartAttributeCollection, boolean checkConditions) {

        // if path condition
        if (checkConditions) {
            if (!filterService.fitCondition(cartAttributeCollection, couponCode.getCoupon())) {
                return false;
            }
        } else {
            if (couponCode.getCoupon().getAuto() == null || couponCode.getCoupon().getAuto() == false)
                return false;
        }

        Coupon coupon = couponCode.getCoupon();
        if (coupon.getEnabled() == null || coupon.getEnabled().getVal() == null || !coupon.getEnabled().getVal())
            return false;

        Date date = new Date();

        Date fromDate = DateTimes.maxOfDates(coupon.getFromDate(), couponCode.getFromDate());
        Date toDate = DateTimes.minOfDates(coupon.getToDate(), couponCode.getToDate());

        if (fromDate != null && fromDate.after(date) || toDate != null && toDate.before(date))
            return false;

        if (!(coupon.getUsesPerCoupon() == null || coupon.getUsesPerCoupon() == 0)) {
            if (couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() >= coupon.getUsesPerCoupon())
                return false;
        }

        if (!(coupon.getUsesPerCustomer() == null || coupon.getUsesPerCustomer() == 0) && app.isCustomerLoggedIn()) {
            if (couponUsedByCustomer(couponCode, ((Customer) app.getLoggedInCustomer()).getId()) >= coupon.getUsesPerCustomer())
                return false;
        }

        if (coupon.getCouponAction().getType().equals(CouponActionType.PERCENT_CART) || coupon.getCouponAction().getType().equals(CouponActionType.FIXED_CART)
            || coupon.getCouponAction().getType().equals(CouponActionType.RANGE_PERCENT_CART) && coupon.getCouponAction().getFilter() == null
            || coupon.getCouponAction().getType().equals(CouponActionType.RANGE_FIXED_CART) && coupon.getCouponAction().getFilter() == null) {
            List<Id> priceTypeIds = new ArrayList<>();
            if (coupon.getPriceTypeIds() != null) {
                priceTypeIds.addAll(coupon.getPriceTypeIds());
            }
            if (coupon.getCouponAction().getPriceTypeId() != null) {
                priceTypeIds.add(coupon.getCouponAction().getPriceTypeId());
            }

            for (Id productId : cartAttributeCollection.getProductAttributes().keySet()) {
                if (!couponHelper.hasPriceTypes(productId, priceTypeIds))
                    return false;
            }
        }

        return couponCouldBeUsedCustomerWithGroups(coupon);
    }

    @Override
    public boolean couponCouldBeUsedCustomerWithGroups(Coupon coupon) {
        if (coupon == null)
            return false;

        if (coupon.getCustomerGroupIds() != null && coupon.getCustomerGroupIds().size() > 0) {
            List<Id> couponCustomerGroupIds = coupon.getCustomerGroupIds();
            List<Id> customerGroupIds = customerService.getCustomerGroupIds();
            if (customerGroupIds == null || customerGroupIds.size() == 0)
                return false;
            Set<Id> intersection = Sets.intersection(Sets.newHashSet(couponCustomerGroupIds), Sets.newHashSet(customerGroupIds));
            if (intersection == null || intersection.size() == 0)
                return false;
        }
        return true;
    }

    private Integer couponUsedByCustomer(CouponCode couponCode, Id customerId) {
        if (couponCode.getCouponUsages() == null || couponCode.getCouponUsages().size() == 0)
            return 0;
        Integer customerUsages = 0;
        for (CouponUsage couponUsage : couponCode.getCouponUsages()) {
            if (couponUsage.getCustomerId().equals(customerId))
                customerUsages++;
        }
        return customerUsages;
    }

    @Override
    public Map<String, AttributeValue> getProductAttributes(Product product) {
        Map<String, AttributeValue> productAttributes = new HashMap<>();
        for (AttributeValue attributeValue : product.getAttributes()) {
            productAttributes.put(attributeValue.getCode(), attributeValue);
        }

        List<CouponFilterAttribute> dynAttributes = couponFilterAttributes.byType(CouponFilterAttributeType.PRODUCT);
        if (dynAttributes == null || dynAttributes.size() == 0)
            return productAttributes;

        Map<String, Object> dynAttributesValues;

        try {
            dynAttributesValues = new HashMap<>();

            for (CouponFilterAttribute couponFilterAttribute : dynAttributes) {
                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("product", product);

                Object value = Groovy.eval(couponFilterAttribute.getExpression(), args, groovyImports, GCL);
                dynAttributesValues.put(couponFilterAttribute.getCode(), value);
            }

            for (String key : dynAttributesValues.keySet()) {
                AttributeValue attributeValue = app.getModel(AttributeValue.class);
                ContextObject<Object> val = new ContextObject<>();
                val.addGlobal(dynAttributesValues.get(key));
                attributeValue.setValue(val);
                productAttributes.put(key, attributeValue);
            }
        } catch (Exception e) {
            String error = e.getMessage();
            System.out.println(error);
            // throw e;
            // throw new Exception("Error in groovy-calculation-script '" +
            // (calculationStep.getScriptlet() != null ?
            // calculationStep.getScriptlet().getCode() :
            // calculationStep.getId() + " (" +
            // calculationStep.getSortOrder() + ")") + "'", e);
        }

        return productAttributes;
    }

    @Override
    public Map<String, AttributeValue> getCartAttributes(Model cart) {
        if (!Reflect.isOfType(cart.getClass(), CART_FQN))
            throw new IllegalArgumentException("The argument must be of type: " + CART_FQN);

        Map<String, AttributeValue> cartAttributes = new HashMap<>();

        List<CouponFilterAttribute> dynAttributes = couponFilterAttributes.byType(CouponFilterAttributeType.CART);
        if (dynAttributes == null || dynAttributes.size() == 0)
            return cartAttributes;

        try {
            Map<String, Object> dynAttributesValues = new HashMap<>();

            for (CouponFilterAttribute couponFilterAttribute : dynAttributes) {
                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("cart", cart);

                Object value = Groovy.eval(couponFilterAttribute.getExpression(), args, groovyImports, GCL);
                dynAttributesValues.put(couponFilterAttribute.getCode(), value);
            }

            for (String key : dynAttributesValues.keySet()) {
                AttributeValue attributeValue = app.getModel(AttributeValue.class);
                ContextObject<Object> val = new ContextObject<>();
                val.addGlobal(dynAttributesValues.get(key));
                attributeValue.setValue(val);
                cartAttributes.put(key, attributeValue);
            }
        } catch (Exception e) {
            e.printStackTrace();

            String error = e.getMessage();
            System.out.println(error);
            // throw e;
            // throw new Exception("Error in groovy-calculation-script '" +
            // (calculationStep.getScriptlet() != null ?
            // calculationStep.getScriptlet().getCode() :
            // calculationStep.getId() + " (" +
            // calculationStep.getSortOrder() + ")") + "'", e);
        }

        return cartAttributes;
    }

    @Override
    public Map<String, AttributeValue> getCartItemAttributes(Model cartItem) {
        if (!Reflect.isOfType(cartItem.getClass(), CART_ITEM_FQN))
            throw new IllegalArgumentException("The argument must be of type: " + CART_ITEM_FQN);

        Map<String, AttributeValue> cartItemAttributes = new HashMap<>();

        List<CouponFilterAttribute> dynAttributes = couponFilterAttributes.byType(CouponFilterAttributeType.CART_ITEM);
        if (dynAttributes == null || dynAttributes.size() == 0)
            return cartItemAttributes;

        try {
            Map<String, Object> dynAttributesValues = new HashMap<>();

            for (CouponFilterAttribute couponFilterAttribute : dynAttributes) {
                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("cartItem", cartItem);

                Object value = Groovy.eval(couponFilterAttribute.getExpression(), args, groovyImports, GCL);
                dynAttributesValues.put(couponFilterAttribute.getCode(), value);
            }

            for (String key : dynAttributesValues.keySet()) {
                AttributeValue attributeValue = app.getModel(AttributeValue.class);
                ContextObject<Object> val = new ContextObject<>();
                val.addGlobal(dynAttributesValues.get(key));
                attributeValue.setValue(val);
                cartItemAttributes.put(key, attributeValue);
            }
        } catch (Exception e) {
            String error = e.getMessage();
            System.out.println(error);
            // throw e;
            // throw new Exception("Error in groovy-calculation-script '" +
            // (calculationStep.getScriptlet() != null ?
            // calculationStep.getScriptlet().getCode() :
            // calculationStep.getId() + " (" +
            // calculationStep.getSortOrder() + ")") + "'", e);
        }

        return cartItemAttributes;
    }

    @Override
    public Map<String, AttributeValue> getOrderAttributes(Model order) {
        if (!Reflect.isOfType(order.getClass(), ORDER_FQN))
            throw new IllegalArgumentException("The argument must be of type: " + ORDER_FQN);

        Map<String, AttributeValue> orderAttributes = new HashMap<>();

        List<CouponFilterAttribute> dynAttributes = couponFilterAttributes.byType(CouponFilterAttributeType.ORDER);
        if (dynAttributes == null || dynAttributes.size() == 0)
            return orderAttributes;

        try {
            Map<String, Object> dynAttributesValues = new HashMap<>();

            for (CouponFilterAttribute couponFilterAttribute : dynAttributes) {
                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("order", order);

                Object value = Groovy.eval(couponFilterAttribute.getExpression(), args, groovyImports, GCL);
                dynAttributesValues.put(couponFilterAttribute.getCode(), value);
            }

            for (String key : dynAttributesValues.keySet()) {
                AttributeValue attributeValue = app.getModel(AttributeValue.class);
                ContextObject<Object> val = new ContextObject<>();
                val.addGlobal(dynAttributesValues.get(key));
                attributeValue.setValue(val);
                orderAttributes.put(key, attributeValue);
            }
        } catch (Exception e) {
            String error = e.getMessage();
            System.out.println(error);
            // throw e;
            // throw new Exception("Error in groovy-calculation-script '" +
            // (calculationStep.getScriptlet() != null ?
            // calculationStep.getScriptlet().getCode() :
            // calculationStep.getId() + " (" +
            // calculationStep.getSortOrder() + ")") + "'", e);
        }

        return orderAttributes;
    }

    @Override
    public Map<String, AttributeValue> getOrderItemAttributes(Model orderItem) {
        if (!Reflect.isOfType(orderItem.getClass(), ORDER_ITEM_FQN))
            throw new IllegalArgumentException("The argument must be of type: " + ORDER_ITEM_FQN);

        Map<String, AttributeValue> cartItemAttributes = new HashMap<>();

        List<CouponFilterAttribute> dynAttributes = couponFilterAttributes.byType(CouponFilterAttributeType.ORDER_ITEM);
        if (dynAttributes == null || dynAttributes.size() == 0)
            return cartItemAttributes;

        try {
            Map<String, Object> dynAttributesValues = new HashMap<>();

            for (CouponFilterAttribute couponFilterAttribute : dynAttributes) {
                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("orderItem", orderItem);

                Object value = Groovy.eval(couponFilterAttribute.getExpression(), args, groovyImports, GCL);
                dynAttributesValues.put(couponFilterAttribute.getCode(), value);
            }

            for (String key : dynAttributesValues.keySet()) {
                AttributeValue attributeValue = app.getModel(AttributeValue.class);
                ContextObject<Object> val = new ContextObject<>();
                val.addGlobal(dynAttributesValues.get(key));
                attributeValue.setValue(val);
                cartItemAttributes.put(key, attributeValue);
            }
        } catch (Exception e) {
            String error = e.getMessage();
            System.out.println(error);
            // throw e;
            // throw new Exception("Error in groovy-calculation-script '" +
            // (calculationStep.getScriptlet() != null ?
            // calculationStep.getScriptlet().getCode() :
            // calculationStep.getId() + " (" +
            // calculationStep.getSortOrder() + ")") + "'", e);
        }

        return cartItemAttributes;
    }

    @Override
    public CouponCode maintainCouponCodesList(CouponCode cartCoupon, CartAttributeCollection cartAttributeCollection, boolean useAutoCoupon) {
        if (cartCoupon != null && isCouponApplicableToCart(cartCoupon, cartAttributeCollection, true)) {
            if (cartCoupon.getCoupon().getAuto() == null || !cartCoupon.getCoupon().getAuto() || cartCoupon.getCoupon().getAuto() && useAutoCoupon)
                return cartCoupon;
        }

        if (useAutoCoupon) {
            List<CouponCode> autoCoupons = getAutoCoupons();
            if (autoCoupons == null || autoCoupons.size() == 0)
                return null;

            for (CouponCode code : autoCoupons) {
                if (isCouponApplicableToCart(code, cartAttributeCollection, true)) {
                    return code;
                }
            }
        }

        /*
         * if (cartCoupon != null && isCouponApplicableToCart(cartCoupon,
         * cartAttributeCollection, false)) { return cartCoupon; }
         * 
         * if(useAutoCoupon) { List<CouponCode> autoCoupons = getAutoCoupons();
         * for (CouponCode code : autoCoupons) { if
         * (isCouponApplicableToCart(code, cartAttributeCollection, false)) {
         * return code; } } }
         */

        return null;
    }

    @Override
    public void applyDiscount(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection) {
        List<CouponProcessor> couponProcessors = getCouponProcessors();
        if (couponCode == null)
            return;

        Coupon coupon = getCoupon(couponCode.getCouponId());
        if (coupon == null)
            return;

        CouponAction couponAction = coupon.getCouponAction();
        if (couponAction == null)
            return;

        for (CouponProcessor couponProcessor : couponProcessors) {
            if (couponProcessor.canBeProcessed(calcCtx, couponCode, cartAttributeCollection)) {
                couponProcessor.process(calcCtx, couponCode, cartAttributeCollection);
            }
        }
    }

    @Override
    public List<CouponCode> getAutoCoupons() {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Coupon.Col.AUTO, true);

        MongoQueries.addCtxObjFilter(filter, Coupon.Col.ENABLED, true);

        Date now = new Date();
        DBObject gteClause = new BasicDBObject();
        gteClause.put("$gte", now);
        filter.put(Coupon.Col.TO_DATE, gteClause);

        DBObject ltClause = new BasicDBObject();
        ltClause.put("$lt", now);
        filter.put(Coupon.Col.FROM_DATE, ltClause);

        List<Coupon> findedCoupons = coupons.find(Coupon.class, filter);

        List<CouponCode> result = new ArrayList<>();
        for (Coupon coupon : findedCoupons) {
            if (coupon.getCodes() != null && coupon.getCodes().size() > 0)
                result.add(coupon.getCodes().get(0));
        }

        return result;
    }

    @Override
    public CouponCode getAutoCoupon(CouponCode cartCoupon, CartAttributeCollection cartAttributeCollection) {
        List<CouponCode> autoCoupons = getAutoCoupons();
        if (autoCoupons == null || autoCoupons.size() == 0)
            return null;

        for (CouponCode code : autoCoupons) {
            if (isCouponApplicableToCart(code, cartAttributeCollection, true)) {
                return code;
            }
        }

        for (CouponCode code : autoCoupons) {
            if (isCouponApplicableToCart(code, cartAttributeCollection, false)) {
                return code;
            }
        }

        return null;
    }

    @Override
    public List<CouponCode> getAutoCoupons(CartAttributeCollection cartAttributeCollection) {
        List<CouponCode> autoCoupons = getAutoCoupons();
        if (autoCoupons == null || autoCoupons.size() == 0)
            return new ArrayList<>();

        List<CouponCode> filteredAutoCoupons = new ArrayList<>();
        for (CouponCode code : autoCoupons) {
            if (isCouponApplicableToCart(code, cartAttributeCollection, false)) {
                filteredAutoCoupons.add(code);
            }
        }

        return filteredAutoCoupons;
    }

    @Override
    public void useCoupon(CouponCode couponCode, Id orderId, Id customerId) {
        CouponCode couponCodeActual = couponCode;
        int cnt = 0;
        while (true) {
            try {
                couponCodeActual.useCoupon(orderId, customerId);
                couponCodes.update(couponCodeActual);
                return;
            } catch (Exception ex) {
                couponCodeActual = couponCodes.findById(CouponCode.class, couponCode.getId());
                cnt++;
            }
        }
    }

    @Override
    public void useCoupon(CouponCode couponCode, Id orderId, String email) {
        CouponCode couponCodeActual = couponCode;
        int cnt = 0;
        while (true) {
            try {
                couponCodeActual.useCoupon(orderId, email);
                couponCodes.update(couponCodeActual);
                return;
            } catch (Exception ex) {
                couponCodeActual = couponCodes.findById(CouponCode.class, couponCode.getId());
                cnt++;
            }
        }
    }

    @Override
    public CouponCode generateCode(Coupon coupon, String email, Integer duration) {
        if (coupon.getCouponCodeGeneration().getAuto() != null && coupon.getCouponCodeGeneration().getAuto()) {
            CouponCodePattern pattern = couponCodePatterns.findById(CouponCodePattern.class, coupon.getCouponCodeGeneration().getPattern());
            List<String> codes = couponHelper.generateCodes(coupon.getCouponCodeGeneration().getPrefix(), coupon.getCouponCodeGeneration().getPostfix(), pattern,
                coupon.getCouponCodeGeneration().getLength(), 1);

            if (codes != null && codes.size() == 1) {
                CouponCode cc = app.getModel(CouponCode.class);
                cc.belongsTo(coupon);
                cc.setCode(codes.get(0));
                cc.setEmail(email);
                cc.setFromDate(new Date());
                if (duration != null) {
                    DateTime today = new DateTime().toDateTime();
                    DateTime toDate = today.plusDays(duration);
                    cc.setToDate(toDate.toDate());
                }
                couponCodes.add(cc);
                return cc;
            } else {
                throw new RuntimeException("Can't generate new code for coupon " + coupon.getId());
            }
        } else {
            throw new RuntimeException("Can't generate new code for coupon " + coupon.getId());
        }
    }

    public List<CouponProcessor> getCouponProcessors() {
        List<CouponProcessor> couponProcessors = new ArrayList<>();
        couponProcessors.add(app.inject(DefaultShippingCouponProcessor.class));

        couponProcessors.add(app.inject(DefaultBuyXGetYCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultBuyXGetYSameCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultSpendXGetYCouponProcessor.class));

        couponProcessors.add(app.inject(DefaultFixedProductCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultPercentProductCouponProcessor.class));

        couponProcessors.add(app.inject(DefaultListFixedProductCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultListPercentProductCouponProcessor.class));

        couponProcessors.add(app.inject(DefaultFixedCartCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultPercentCartCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultRangeFixedCartCouponProcessor.class));
        couponProcessors.add(app.inject(DefaultRangePercentCartCouponProcessor.class));

        return couponProcessors;
    }
}
