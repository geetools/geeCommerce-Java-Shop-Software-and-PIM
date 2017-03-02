package com.geecommerce.coupon.service;

import java.util.*;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.enums.CouponFilterNodeType;
import com.geecommerce.coupon.enums.ProductSelectionType;
import com.geecommerce.coupon.helper.CouponHelper;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponFilterNode;
import com.google.inject.Inject;

@Service
public class DefaultFilterService implements FilterService {

    private final CouponHelper couponHelper;
    private final ProductLists productLists;
    private final ProductListService productListService;

    @Inject
    public DefaultFilterService(CouponHelper couponHelper, ProductLists productLists, ProductListService productListService) {
        this.couponHelper = couponHelper;
        this.productLists = productLists;
        this.productListService = productListService;
    }

    @Override
    public boolean fitCondition(CartAttributeCollection cartAttributeCollection, Coupon coupon) {
        if (coupon.getCouponCondition() == null)
            return true;

        if (!fitCondition(cartAttributeCollection, coupon.getCouponCondition()))
            return false;

        return true;
    }

    private boolean fitConditionAttributeNode(CartAttributeCollection cartAttributeCollection, CouponFilterNode node) {
        try {
            AttributeValue attribute = cartAttributeCollection.getCartAttributes().get(node.getAttributeCode());

            String script = "attr " + node.getOperator() + " val";

            if (attribute == null)
                return false;

            if (attribute.getAttribute() == null || !attribute.getAttribute().isOptionAttribute()) {

                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("attr", attribute.getVal());
                args.put("val", castObject(attribute.getVal(), node.getValue()));

                return (boolean) Groovy.eval(script, args);

            } else {
                boolean result = false;
                for (Id optionId : attribute.getOptionIds()) {

                    LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                    args.put("attr", optionId.toString());
                    args.put("val", node.getValue());

                    result = result || (boolean) Groovy.eval(script, args);
                }

                return result;
            }
        } catch (Exception ex) {
            //
            return false;
        }
    }

    private boolean fitConditionAttributeNode(Id id, CartAttributeCollection cartAttributeCollection,
        CouponFilterNode node) {
        Object attribute = null;
        if (node.getAttributeType().equals(CouponFilterAttributeType.CART_ITEM)) {
            attribute = cartAttributeCollection.getCartItemAttributes().get(id).get(node.getAttributeCode());
        } else if (node.getAttributeType().equals(CouponFilterAttributeType.PRODUCT)) {
            attribute = cartAttributeCollection.getProductAttributes().get(id).get(node.getAttributeCode());
        }

        String script = "attr " + node.getOperator() + " val";

        // Binding binding = new Binding();
        // binding.setVariable("attr", attribute);
        // binding.setVariable("val", node.getValue());
        //
        // GroovyShell shell = new GroovyShell(binding);
        // return (boolean)shell.evaluate(script);

        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("attr", attribute);
        args.put("val", node.getValue());

        try {
            return (boolean) Groovy.eval(script, args);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean fitCondition(CartAttributeCollection cartAttributeCollection, CouponFilterNode node) {
        boolean result = false;
        if (node.getType().equals(CouponFilterNodeType.BOOLEAN_OPERATION)) {
            if (node.getNodes() == null || node.getNodes().size() == 0)
                return true;

            if (node.getOperation().equals("AND")) {
                result = true;
                for (CouponFilterNode n : node.getNodes()) {
                    if (node.getOperationValue()) {
                        result = result && fitCondition(cartAttributeCollection, n);
                    } else {
                        result = result && !fitCondition(cartAttributeCollection, n);
                    }
                    if (!result)
                        return result;
                }

            } else if (node.getOperation().equals("OR")) {
                result = false;
                for (CouponFilterNode n : node.getNodes()) {
                    if (node.getOperationValue()) {
                        result = result || fitCondition(cartAttributeCollection, n);
                    } else {
                        result = result || !fitCondition(cartAttributeCollection, n);
                    }

                    if (result)
                        return result;
                }
            }
        } else if (node.getType().equals(CouponFilterNodeType.FIlTER_ATTRIBUTE_OPERATION)) {
            return fitConditionAttributeNode(cartAttributeCollection, node);
        } else if (node.getType().equals(CouponFilterNodeType.FOUND)) {
            if (node.getOperation().equals("AND")) {
                for (Id id : cartAttributeCollection.getProductAttributes().keySet()) {
                    result = true;
                    for (CouponFilterNode n : node.getNodes()) {
                        result = result && passFilter(id, cartAttributeCollection, n);
                    }
                    if (result)
                        return true;
                }
                return false;
            } else if (node.getOperation().equals("OR")) {
                for (Id id : cartAttributeCollection.getProductAttributes().keySet()) {
                    result = false;
                    for (CouponFilterNode n : node.getNodes()) {
                        result = result || passFilter(id, cartAttributeCollection, n);
                    }
                    if (result)
                        return true;
                }
                return false;
            }
        } else if (node.getType().equals(CouponFilterNodeType.NOT_FOUND)) {
            if (node.getOperation().equals("AND")) {
                for (Id id : cartAttributeCollection.getProductAttributes().keySet()) {
                    result = true;
                    for (CouponFilterNode n : node.getNodes()) {
                        result = result && passFilter(id, cartAttributeCollection, n);
                    }
                    if (result)
                        return false;
                }
                return true;
            } else if (node.getOperation().equals("OR")) {
                for (Id id : cartAttributeCollection.getProductAttributes().keySet()) {
                    result = false;
                    for (CouponFilterNode n : node.getNodes()) {
                        result = result || passFilter(id, cartAttributeCollection, n);
                    }
                    if (result)
                        return false;
                }
                return true;
            }
        } else {
            throw new UnsupportedOperationException("Unknown filter type operation");
        }
        return result;
    }

    @Override
    public List<Id> passFilter(CartAttributeCollection cartAttributeCollection, Coupon coupon) {

        List<Id> result = new ArrayList<>();
        Set<Id> allCartProducts = cartAttributeCollection.getProductAttributes().keySet();

        List<Id> priceTypeIds = new ArrayList<>();
        if (coupon.getPriceTypeIds() != null) {
            priceTypeIds.addAll(coupon.getPriceTypeIds());
        }

        if (coupon.getCouponAction().getPriceTypeId() != null) {
            priceTypeIds.add(coupon.getCouponAction().getPriceTypeId());
        }


        if(coupon.getCouponAction().getProductSelectionType().equals(ProductSelectionType.QUERY)){

            if (coupon.getCouponAction().getFilter() == null)
                return new ArrayList<>(allCartProducts);


            for (Id id : allCartProducts) {
                if (couponHelper.hasPriceTypes(id, priceTypeIds)
                        && passFilter(id, cartAttributeCollection, coupon.getCouponAction().getFilter()))
                    result.add(id);
            }
        }

        if(coupon.getCouponAction().getProductSelectionType().equals(ProductSelectionType.PRODUCT)){
            if(coupon.getCouponAction().getProductIds() != null){
                for (Id id: allCartProducts) {
                    if(couponHelper.hasPriceTypes(id, priceTypeIds) && coupon.getCouponAction().getProductIds().contains(id))
                        result.add(id);
                }
            }
        }


        if(coupon.getCouponAction().getProductSelectionType().equals(ProductSelectionType.LIST)){
            if(coupon.getCouponAction().getProductListIds() != null){
                List<Id> allProductsIds = new ArrayList<>();
                for(Id productListId: coupon.getCouponAction().getProductListIds()){
                    ProductList productList = productLists.findById(ProductList.class, productListId);
                    Id[] productIds = productListService.getProductIds(productList, false, null);
                    if(productIds != null)
                        allProductsIds.addAll(Arrays.asList(productIds));
                }
                for (Id id: allCartProducts) {
                    if(couponHelper.hasPriceTypes(id, priceTypeIds) && allProductsIds.contains(id))
                        result.add(id);
                }
            }
        }


        return result;
    }

    private boolean passFilterAttributeNode(Id id, CartAttributeCollection cartAttributeCollection,
        CouponFilterNode node) {
        try {
            AttributeValue attribute = null;
            if (node.getAttributeType().equals(CouponFilterAttributeType.CART_ITEM)) {
                attribute = cartAttributeCollection.getCartItemAttributes().get(id).get(node.getAttributeCode());
            } else if (node.getAttributeType().equals(CouponFilterAttributeType.PRODUCT)) {
                attribute = cartAttributeCollection.getProductAttributes().get(id).get(node.getAttributeCode());
            }

            if (attribute == null)
                return false;

            String script = "attr " + node.getOperator() + " val";
            if (attribute.getAttribute() == null || !attribute.getAttribute().isOptionAttribute()) {
                // Binding binding = new Binding();
                // binding.setVariable("attr", attribute.getVal());
                // binding.setVariable("val", castObject(attribute.getVal(),
                // node.getValue()));
                // GroovyShell shell = new GroovyShell(binding);
                //
                // return (boolean)shell.evaluate(script);

                LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                args.put("attr", attribute.getVal());
                args.put("val", castObject(attribute.getVal(), node.getValue()));

                return (boolean) Groovy.eval(script, args);
            } else {
                boolean result = false;
                for (Id optionId : attribute.getOptionIds()) {
                    // Binding binding = new Binding();
                    // binding.setVariable("attr", optionId.toString());
                    // binding.setVariable("val", node.getValue());
                    // GroovyShell shell = new GroovyShell(binding);
                    // result = result || (boolean)shell.evaluate(script);

                    LinkedHashMap<String, Object> args = new LinkedHashMap<>();
                    args.put("attr", optionId.toString());
                    args.put("val", node.getValue());

                    result = result || (boolean) Groovy.eval(script, args);
                }

                return result;
            }
        } catch (Exception ex) {
            //
            return false;
        }
    }

    private Object castObject(Object value, Object target) {
        try {
            if (value instanceof Double) {
                return Double.valueOf(target.toString());
            }
            if (value instanceof Id) {
                return Id.parseId(target.toString());
            }
            if (value instanceof Integer) {
                return Integer.valueOf(target.toString());
            }
            if (value instanceof Boolean) {
                return Boolean.valueOf(target.toString());
            }
        } catch (Exception ex) {
            return target;
        }
        return target;
    }

    private boolean passFilter(Id id, CartAttributeCollection cartAttributeCollection, CouponFilterNode node) {
        boolean result = false;
        if (node.getType().equals(CouponFilterNodeType.BOOLEAN_OPERATION)) {
            if (node.getNodes() == null || node.getNodes().size() == 0)
                return true;

            if (node.getOperation().equals("AND")) {
                result = true;
                for (CouponFilterNode n : node.getNodes()) {
                    if (node.getOperationValue()) {
                        result = result && passFilter(id, cartAttributeCollection, n);
                    } else {
                        result = result && !passFilter(id, cartAttributeCollection, n);
                    }
                    if (!result)
                        return result;
                }

            } else if (node.getOperation().equals("OR")) {
                result = false;
                for (CouponFilterNode n : node.getNodes()) {
                    if (node.getOperationValue()) {
                        result = result || passFilter(id, cartAttributeCollection, n);
                    } else {
                        result = result || !passFilter(id, cartAttributeCollection, n);
                    }
                    if (result)
                        return result;
                }
            }
        } else if (node.getType().equals(CouponFilterNodeType.FIlTER_ATTRIBUTE_OPERATION)) {
            return passFilterAttributeNode(id, cartAttributeCollection, node);
        } else {
            throw new UnsupportedOperationException("Unknown filter type operation");
        }

        return result;
    }

}
