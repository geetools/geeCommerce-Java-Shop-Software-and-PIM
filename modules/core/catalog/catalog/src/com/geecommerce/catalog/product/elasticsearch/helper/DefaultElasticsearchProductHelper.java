package com.geecommerce.catalog.product.elasticsearch.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.pojo.PriceResult;
import com.google.inject.Inject;

@Helper
public class DefaultElasticsearchProductHelper implements ElasticsearchProductHelper {
    @Inject
    protected App app;

    protected static final String IS_SALE_PRODUCT_SCRIPT_KEY = "catalog/product/script/is_sale";
    protected static final String IS_SPECIAL_PRODUCT_SCRIPT_KEY = "catalog/product/script/is_special";

    protected static final String FIELD_KEY_ID = "_id";
    protected static final String FIELD_KEY_PRODUCT_ID = "product_id";
    protected static final String FIELD_KEY_PRODUCT_TYPE = "product_type";
    protected static final String FIELD_KEY_IS_VARIANT_MASTER = "is_variant_master";
    protected static final String FIELD_KEY_IS_VARIANT = "is_variant";
    protected static final String FIELD_KEY_IS_BUNDLE = "is_bundle";
    protected static final String FIELD_KEY_IS_PROGRAMME = "is_programme";
    protected static final String FIELD_KEY_QTY = "qty";
    protected static final String FIELD_KEY_IS_DELETED = "is_deleted";
    protected static final String FIELD_KEY_IS_VISIBLE = "is_visible";
    protected static final String FIELD_KEY_IS_VISIBLE_IN_PRODUCT_LIST = "is_visible_in_pl";
    protected static final String FIELD_KEY_IS_SALE = "is_sale";
    protected static final String FIELD_KEY_IS_SPECIAL = "is_special";
    protected static final String FIELD_KEY_PRICE = "price";

    private static final String KEY_PRODUCT = "product";

    private static final String PREFIX_HAS = "has_";

    private final Products products;
    private final ElasticsearchIndexHelper elasticsearchHelper;

    @Inject
    public DefaultElasticsearchProductHelper(Products products, ElasticsearchIndexHelper elasticsearchHelper) {
        this.products = products;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public Map<String, Object> buildJsonProduct(String id, Product product) {
        Map<String, Object> json = new LinkedHashMap<>();

        json.put(FIELD_KEY_ID, id);
        json.put(FIELD_KEY_PRODUCT_ID, product.getId().str());

        if (product.getType() != null)
            json.put(FIELD_KEY_PRODUCT_TYPE, product.getType().toId());

        json.put(FIELD_KEY_IS_VARIANT_MASTER, product.isVariantMaster());
        json.put(FIELD_KEY_IS_VARIANT, product.isVariant());
        json.put(FIELD_KEY_IS_BUNDLE, product.isBundle());
        json.put(FIELD_KEY_IS_PROGRAMME, product.isProgramme());

        json.put(FIELD_KEY_IS_DELETED, product.isDeleted());

        boolean isVisible = product.isVisible() && !product.isDeleted();
        json.put(FIELD_KEY_IS_VISIBLE, isVisible);

        Boolean visibleInProductList = ContextObjects.findCurrentStoreOrGlobal(product.getVisibleInProductList());
        json.put(FIELD_KEY_IS_VISIBLE_IN_PRODUCT_LIST,
            visibleInProductList == null ? true : visibleInProductList.booleanValue());

        Integer qty = product.getQty();
        json.put(FIELD_KEY_QTY, qty);

        if (isVisible) {
            boolean isSale = product.isSale();

            if (isSale) {
                json.put(FIELD_KEY_IS_SALE, isSale);
            } else {
                String isSaleMatcherScript = app.cpStr_(IS_SALE_PRODUCT_SCRIPT_KEY);

                if (isSaleMatcherScript != null) {
                    try {
                        isSale = Groovy.conditionMatches(isSaleMatcherScript, KEY_PRODUCT, product);

                        if (!isSale && (product.isVariantMaster() || product.isProgramme() || product.isBundle())) {
                            Set<Id> childProductIds = product.getAllChildProductIds();

                            if (childProductIds != null && !childProductIds.isEmpty()) {
                                List<Product> childProducts = products.findByIds(Product.class,
                                    childProductIds.toArray(new Id[childProductIds.size()]));

                                for (Product childProduct : childProducts) {
                                    if ((childProduct.isValidForSelling() || childProduct.hasVariantsValidForSelling())
                                        && childProduct.isVisible()) {
                                        isSale = Groovy.conditionMatches(isSaleMatcherScript, KEY_PRODUCT,
                                            childProduct);

                                        if (isSale)
                                            break;
                                    }
                                }
                            }
                        }

                        json.put(FIELD_KEY_IS_SALE, isSale);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

            boolean isSpecial = product.isSpecial();

            if (isSpecial) {
                json.put(FIELD_KEY_IS_SPECIAL, isSpecial);
            } else {
                String isSpecialMatcherScript = app.cpStr_(IS_SPECIAL_PRODUCT_SCRIPT_KEY);

                if (isSpecialMatcherScript != null) {
                    try {
                        isSpecial = Groovy.conditionMatches(isSpecialMatcherScript, KEY_PRODUCT, product);

                        if (!isSpecial && (product.getType() == ProductType.VARIANT_MASTER
                            || product.getType() == ProductType.PROGRAMME
                            || product.getType() == ProductType.BUNDLE)) {
                            Set<Id> childProductIds = product.getAllChildProductIds();

                            if (childProductIds != null && !childProductIds.isEmpty()) {
                                List<Product> childProducts = products.findByIds(Product.class,
                                    childProductIds.toArray(new Id[childProductIds.size()]));

                                for (Product childProduct : childProducts) {
                                    if ((childProduct.isValidForSelling() || childProduct.hasVariantsValidForSelling())
                                        && childProduct.isVisible()) {
                                        isSpecial = Groovy.conditionMatches(isSpecialMatcherScript, KEY_PRODUCT,
                                            childProduct);

                                        if (isSpecial)
                                            break;
                                    }
                                }
                            }
                        }

                        json.put(FIELD_KEY_IS_SPECIAL, isSpecial);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }

        PriceResult priceResult = product.getPrice();

        if (priceResult == null && !product.isProgramme()) {
            isVisible = false;
            json.put(FIELD_KEY_IS_VISIBLE, isVisible);
        } else {
            Double finalPrice = null;

            if ((!product.isProgramme() && !product.isVariantMaster())
                || (product.getPrice() != null && product.getPrice().hasValidPrice())) {
                finalPrice = priceResult.getFinalPrice();
            } else if (product.isVariantMaster() || product.isProgramme()) {
                finalPrice = priceResult.getLowestFinalPrice();
            }

            if (finalPrice == null && !product.isProgramme()) {
                isVisible = false;
                json.put(FIELD_KEY_IS_VISIBLE, isVisible);
            }

            if (finalPrice != null)
                json.put(FIELD_KEY_PRICE, finalPrice);

            if (priceResult != null && finalPrice != null) {
                Map<String, Double> prices = null;

                if ((!product.isProgramme() && !product.isVariantMaster())
                    || (product.getPrice() != null && product.getPrice().hasValidPrice())) {
                    prices = priceResult.getValidPrices();
                } else if (product.isVariantMaster() || product.isProgramme()) {
                    prices = priceResult.getLowestValidPrices();
                }

                Set<String> priceTypes = prices.keySet();

                for (String priceType : priceTypes) {
                    Double price = prices.get(priceType);
                    json.put(new StringBuilder(PREFIX_HAS).append(priceType).toString(), (price != null && price > 0));
                }
            }
        }

        Set<Id> indexedAttributes = new HashSet<>();
        for (AttributeValue attributeValue : product.getAttributes()) {
            Attribute attr = attributeValue.getAttribute();

            if (attr != null && (attr.isIncludeInSearchIndex() || attr.isSearchable()
                || attr.getIncludeInProductListFilter() || attr.getIncludeInProductListQuery())) {
                // Only index text values when product is visible.
                if (!isVisible && BackendType.STRING == attr.getBackendType())
                    continue;

                elasticsearchHelper.addAttribute(json, attr, attributeValue);
                indexedAttributes.add(attr.getId());
            }
        }

        // ------------------------------------------------------------------------------------------
        // When indexing a programme, bundle or variant-master we try to find
        // child attributes
        // that this main parent product does not have. The attributes found
        // must be set
        // accordingly to allow this by setting the
        // productListFilterIncludeChildren to true.
        // We only do this with visible products, as others do not play a role
        // in searching anyway.
        // ------------------------------------------------------------------------------------------
        if (isVisible && (product.isProgramme() || product.isBundle() || product.isVariantMaster())) {
            Set<Id> childProductIds = product.getAllChildProductIds();

            if (childProductIds != null && !childProductIds.isEmpty()) {
                List<Product> childProducts = products.findByIds(Product.class,
                    childProductIds.toArray(new Id[childProductIds.size()]));

                Map<Id, AttributeValue> childAttributeValues = new HashMap<Id, AttributeValue>();

                for (Product childProduct : childProducts) {
                    if (!childProduct.isVisible())
                        continue;

                    for (AttributeValue attributeValue : childProduct.getAttributes()) {
                        // We have already indexed this field with the main
                        // product, so just continue.
                        // Only attribute values that do not exist in the main
                        // parent product will be included.
                        // Also the attribute must have the appropriate settings
                        // to allow this
                        // -> attribute.productListFilterIncludeChildren.
                        if (indexedAttributes.contains(attributeValue.getAttributeId()))
                            continue;

                        Attribute attr = attributeValue.getAttribute();

                        if (attr != null && attr.isProductListFilterIncludeChildren()
                            && attr.getIncludeInProductListFilter()) {
                            AttributeValue av = childAttributeValues.get(attr.getId());

                            if (av == null) {
                                av = app.model(AttributeValue.class).setAttributeId(attr.getId());

                                childAttributeValues.put(attr.getId(), av);
                            }

                            av.addOptionIds(attributeValue.getOptionIds());
                        }
                    }
                }

                Set<Id> childAttributeIds = childAttributeValues.keySet();

                for (Id childAttributeId : childAttributeIds) {
                    AttributeValue childAttributeValue = childAttributeValues.get(childAttributeId);
                    elasticsearchHelper.addAttribute(json, childAttributeValue.getAttribute(), childAttributeValue);
                }
            }
        }

        return json;
    }

}
