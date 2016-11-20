package com.geecommerce.coupon.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCodePattern;
import com.geecommerce.coupon.model.CouponFilterNode;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.price.model.PriceType;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.price.pojo.PricingContext;
import com.geecommerce.price.repository.PriceTypes;
import com.google.inject.Inject;

@Helper
public class DefaultCouponHelper implements CouponHelper {
    @Inject
    protected App app;

    protected Random random = new Random();
    protected final CouponCodes couponCodes;
    protected final ProductService productService;
    protected final PriceTypes priceTypes;

    @Inject
    public DefaultCouponHelper(CouponCodes couponCodes, ProductService productService, PriceTypes priceTypes) {
        this.couponCodes = couponCodes;
        this.productService = productService;
        this.priceTypes = priceTypes;
    }

    public List<String> generateCodes(String prefix, String postfix, CouponCodePattern pattern, Integer length,
        Integer quantity) {
        // List<String> codes = new ArrayList<>();
        HashSet<String> codes = new HashSet<>();
        int count = 0;
        while (count < quantity) {
            StringBuilder stringBuilder = new StringBuilder();
            if (prefix != null && !prefix.isEmpty()) {
                stringBuilder.append(prefix);
            }

            if (pattern.getIsPattern() == null || !pattern.getIsPattern()) {
                appendCode(pattern, length, stringBuilder);
            } else {
                appendPatternCode(pattern, stringBuilder);
            }

            if (postfix != null && !postfix.isEmpty()) {
                stringBuilder.append(postfix);
            }

            String code = stringBuilder.toString();
            if (isUnique(code, codes)) {
                codes.add(code);
                count++;
            }
        }
        return new ArrayList<String>(codes);
    }

    @Override
    public void fixCouponFilters(Coupon coupon) {
        while (true) {
            CouponFilterNode node = coupon.getCouponCondition();
            Boolean changed = false;
            ReturnValue n = fixFilterNode(node);
            if (n.node == null)
                coupon.setCouponCondition(null);
            if (!n.changed)
                break;
        }
        while (true) {
            CouponFilterNode node = coupon.getCouponAction().getFilter();
            Boolean changed = false;
            ReturnValue n = fixFilterNode(node);
            if (n.node == null)
                coupon.getCouponAction().setFilter(null);
            if (!n.changed)
                break;
        }
    }

    @Override
    public boolean hasPriceTypes(Id productId, List<Id> priceTypeIds) {
        if (priceTypeIds == null || priceTypeIds.size() == 0)
            return true;

        Product product = productService.getProduct(productId);
        PriceResult priceResult = product.getPrice();
        PricingContext defaultPricingCtx = app.pojo(PricingContext.class);
        Map<String, Double> validPrices = priceResult.getValidPrices(defaultPricingCtx);

        for (Id priceTypeId : priceTypeIds) {
            PriceType priceType = priceTypes.findById(PriceType.class, priceTypeId);
            if (!validPrices.containsKey(priceType.getCode()))
                return false;
        }

        return true;
    }

    private ReturnValue fixFilterNode(CouponFilterNode node) {
        ReturnValue value = new ReturnValue();
        if (node == null) {
            value.changed = false;
            value.node = null;
            return value;
        }

        if (!node.isValid()) {
            value.changed = true;
            value.node = null;
            return value;
        }

        boolean changedInternal = false;
        if (node.getNodes() != null) {
            List<CouponFilterNode> nodesForDelete = new ArrayList<>();
            for (CouponFilterNode n : node.getNodes()) {
                ReturnValue t = fixFilterNode(n);
                if (t.node == null)
                    nodesForDelete.add(n);
                changedInternal = changedInternal || t.changed;
            }
            for (CouponFilterNode n : nodesForDelete) {
                node.getNodes().remove(n);
            }
            value.changed = changedInternal;
            value.node = node;
            return value;
        }
        value.changed = false;
        value.node = node;
        return value;
    }

    private void appendPatternCode(CouponCodePattern pattern, StringBuilder stringBuilder) {
        for (int i = 0; i < pattern.getPattern().length(); i++) {
            Character c = pattern.getPattern().charAt(i);
            if (pattern.getProductionRules().containsKey(c.toString())) {
                stringBuilder.append(uniqueChar(pattern.getProductionRules().get(c.toString())));
            } else {
                stringBuilder.append(c);
            }
        }
    }

    private void appendCode(CouponCodePattern pattern, Integer length, StringBuilder stringBuilder) {
        for (int i = 0; i < length; i++) {
            stringBuilder.append(uniqueChar(pattern.getTerminalString()));
        }
    }

    private char uniqueChar(String terminalString) {
        int i = random.nextInt(terminalString.length());
        return terminalString.charAt(i);
    }

    private boolean isUnique(String code, HashSet<String> codes) {
        if (codes.contains(code))
            return false;
        if (couponCodes.byCode(code) != null)
            return false;
        return true;
    }

    class ReturnValue {
        Boolean changed;
        CouponFilterNode node;
    }

}
