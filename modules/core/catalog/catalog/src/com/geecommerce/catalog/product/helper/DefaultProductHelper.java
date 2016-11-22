package com.geecommerce.catalog.product.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductIdObject;
import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeInputCondition;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Helper
public class DefaultProductHelper implements ProductHelper {
    @Inject
    protected App app;

    protected final AttributeService attributeService;
    protected final Attributes attributes;
    protected final CatalogMediaHelper catalogMediaHelper;

    protected final String DEFAULT_CURRENT_LIST_NAME = "cat";
    protected final String TOP_SELLER_PRODUCT_LIST_NAME = "ts";

    protected static final String NEW = "not_started";
    protected static final String IN_PROGESS = "in_progress";
    protected static final String COMPLETE = "complete";

    protected static final String ATTRIBUTE_CODE_PRODUCT_GROUP = "product_group";
    protected static final String ATTRIBUTE_CODE_PROGRAMME = "programme";

    protected static final Logger log = LogManager.getLogger(DefaultProductHelper.class);

    @Inject
    public DefaultProductHelper(AttributeService attributeService, Attributes attributes,
        CatalogMediaHelper catalogMediaHelper) {
        this.attributeService = attributeService;
        this.attributes = attributes;
        this.catalogMediaHelper = catalogMediaHelper;
    }

    @Override
    public AttributeOption getDescriptionStatus(Product product, Store store) {
        AttributeValue productGroup = product.getAttribute("product_group");
        AttributeValue programme = product.getAttribute("programme");

        List<Attribute> mandatoryAttributes = attributes.thatAreMandatoryAndEditable(product.targetObject(), true);
        List<AttributeInputCondition> inputConditions = attributes.findAll(AttributeInputCondition.class);

        int countNumExpectedNonEmptyAttributes = 0;
        int countNumNonEmptyAttributes = 0;

        for (Attribute attr : mandatoryAttributes) {
            // We handle these two manually for now.
            if (ATTRIBUTE_CODE_PRODUCT_GROUP.equals(attr.getCode())
                || ATTRIBUTE_CODE_PROGRAMME.equals(attr.getCode())) {
                continue;
            }

            boolean isAttributeAvailableForProduct = isAttributeAvailableForProduct(attr, inputConditions, product);

            if (isAttributeAvailableForProduct) {
                countNumExpectedNonEmptyAttributes++;
            }

            if (isAttributeAvailableForProduct
                && (!product.isAttributeEmpty(attr.getId(), store) || product.isAttributeOptedOut(attr.getId()))) {
                countNumNonEmptyAttributes++;
            } else if (isAttributeAvailableForProduct) {
                System.out.println(attr.getCode() + " - " + attr.getInputType().name());
            }
        }

        String descStatus = NEW;

        if ((productGroup != null || programme != null)
            && countNumExpectedNonEmptyAttributes == countNumNonEmptyAttributes) {
            descStatus = COMPLETE;
        } else if (productGroup != null || programme != null
            || (countNumExpectedNonEmptyAttributes > 0 && countNumNonEmptyAttributes > 0)
            || (countNumExpectedNonEmptyAttributes == 0 && countNumNonEmptyAttributes == 0)) {
            descStatus = IN_PROGESS;
        }

        Attribute descStatusAttr = attributeService.getAttribute(product.targetObject(), "status_description");

        List<AttributeOption> options = descStatusAttr.getOptions();

        AttributeOption descStatusOption = null;

        for (AttributeOption ao : options) {
            String label = (String) ao.getLabel().getGlobalValue();

            if (label.equals(descStatus)) {
                descStatusOption = ao;
                break;
            }
        }

        return descStatusOption;
    }

    @Override
    public boolean isAttributeAvailableForProduct(Attribute attr, List<AttributeInputCondition> inputConditions,
        Product product) {
        Set<ProductType> productTypes = attr.getProductTypes();

        // If attribute is only for a particular product type, check to see if
        // the current product matches that type.
        if (productTypes != null && productTypes.size() > 0 && !productTypes.contains(product.getType()))
            return false;

        // If no input conditions exist for this attribute, then it is available
        // for all.
        if (inputConditions == null || inputConditions.size() == 0)
            return true;

        List<AttributeInputCondition> foundInputConditions = new ArrayList<AttributeInputCondition>();

        for (AttributeInputCondition inputCondition : inputConditions) {
            if (attr.getId().equals(inputCondition.getShowAttributeId())) {
                foundInputConditions.add(inputCondition);
            }
        }

        // No input conditions exist for this attribute, so it is available for
        // all.
        if (foundInputConditions.size() == 0)
            return true;

        int numMatchingInputConditions = 0;

        if (foundInputConditions.size() > 0) {
            for (AttributeInputCondition foundInputCondition : foundInputConditions) {
                // Nothing to check.
                if (foundInputCondition.getWhenAttributeId() == null)
                    continue;

                Id whenAttributeId = foundInputCondition.getWhenAttributeId();

                if (product.hasAttribute(whenAttributeId)) {
                    AttributeValue productAttr = product.attr(whenAttributeId);

                    List<Id> hasOptionIds = foundInputCondition.getHasOptionIds();

                    if (hasOptionIds != null && hasOptionIds.size() > 0) {
                        boolean foundOption = false;

                        for (Id optionId : hasOptionIds) {
                            if (productAttr.hasOptionId(optionId)) {
                                numMatchingInputConditions++;
                                foundOption = true;
                                break;
                            }
                        }

                        if (foundOption)
                            break;
                    } else {
                        numMatchingInputConditions++;
                        break;
                    }
                }
            }
        }

        // if the number of matching input conditions does not match the number
        // of found input conditions,
        // then the attribute we are checking is not available for this product.
        return numMatchingInputConditions == foundInputConditions.size();
    }

    @Override
    public AttributeOption getImageStatus(Product product) {
        List<CatalogMediaAsset> images = product.getImages();

        String imageStatus = NEW;

        if (images != null && images.size() > 0) {
            imageStatus = COMPLETE;
        }

        Attribute imgStatusAttr = attributeService.getAttribute(product.targetObject(), "status_image");

        List<AttributeOption> options = imgStatusAttr.getOptions();

        AttributeOption imageStatusOption = null;

        for (AttributeOption ao : options) {
            String label = (String) ao.getLabel().getGlobalValue();

            if (label.equals(imageStatus)) {
                imageStatusOption = ao;
                break;
            }
        }

        return imageStatusOption;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> toVariantsMap(Product product) {
        Map<String, Object> variantData = new LinkedHashMap<>();

        List<Product> variants = product.getVariants();

        if (variants == null || variants.size() == 0)
            return null;

        Set<Id> allOptionIds = Sets.newHashSet();
        Set<Id> allAttributeIds = Sets.newHashSet();

        Map<Id, Map<String, Object>> variantProductsData = new LinkedHashMap<>();

        int mainImgWidth = app.cpInt_("catalog/product/variant/main_image/width", 378);
        int mainImgHeight = app.cpInt_("catalog/product/variant/main_image/height", 450);
        int mainImgZoomWidth = app.cpInt_("catalog/product/variant/main_image/zoom_width", 2000);
        int mainImgZoomHeight = app.cpInt_("catalog/product/variant/main_image/zoom_height", 2000);

        // Get all option data
        for (Product productVariant : variants) {
            if (!productVariant.isValidForSelling())
                continue;

            Map<String, Object> data = new LinkedHashMap<>();

            List<AttributeValue> attrValues = productVariant.getVariantAttributes();

            if (attrValues == null || attrValues.size() == 0) {
                log.warn("Variant '" + productVariant.getId() + "' (" + productVariant.getArticleNumber()
                    + ") has no variant attributes.");
                continue;
            }

            List<Id> variantOptionIds = new ArrayList<>();

            for (AttributeValue attrVal : attrValues) {
                variantOptionIds.add(attrVal.getOptionId());
                allOptionIds.add(attrVal.getOptionId());
            }

            String imageURL = catalogMediaHelper.toMediaAssetURL(productVariant.getMainImageURI(), mainImgWidth,
                mainImgHeight);
            data.put("variantImage", imageURL);

            String zoomImageURL = catalogMediaHelper.toMediaAssetURL(productVariant.getMainImageURI(), mainImgZoomWidth,
                mainImgZoomHeight);
            data.put("variantZoomImage", zoomImageURL);

            data.put("gallery", productVariant.getImagesMaps());

            data.put("id", productVariant.getId().str());
            data.put("artNo", productVariant.getArticleNumber());
            data.put("name", productVariant.getName());
            data.put("name2", productVariant.getName2());

            data.put("options", variantOptionIds);

            variantProductsData.put(productVariant.getId(), data);
        }

        if (allOptionIds.size() == 0) {
            log.warn("None of the variants found for product '" + product.getId() + "' (" + product.getArticleNumber()
                + ") had any options.");
            return variantData;
        }

        List<AttributeOption> attributeOptions = attributeService
            .getAttributeOptions(allOptionIds.toArray(new Id[allOptionIds.size()]));

        if (attributeOptions == null || attributeOptions.size() == 0)
            return variantData;

        Map<Id, AttributeOption> attributeOptionsMap = toMap(attributeOptions);

        for (AttributeOption attributeOption : attributeOptions) {
            allAttributeIds.add(attributeOption.getAttributeId());
        }

        List<Attribute> attributes = attributeService
            .getAttributes(allAttributeIds.toArray(new Id[allAttributeIds.size()]));

        if (attributes == null || attributes.size() == 0)
            return variantData;

        Map<Id, Attribute> attributesMap = toAttributesMap(attributes);

        List<Map<String, Object>> variantOptionsData = new ArrayList<>();

        if (attributeOptionsMap != null && attributeOptionsMap.size() > 0) {
            for (Product productVariant : variants) {
                for (AttributeValue attrVal : productVariant.getVariantAttributes()) {
                    Id optionId = attrVal.getOptionId();

                    AttributeOption attributeOption = attributeOptionsMap.get(optionId);

                    if (attributeOption != null) {
                        Attribute attribute = attributesMap.get(attributeOption.getAttributeId());

                        if (attribute != null) {
                            Map<String, Object> variantMap = getVariantMap(variantOptionsData, attribute.getCode());
                            variantMap.put("attribute_code", attribute.getCode());
                            variantMap.put("attribute_label", attribute.getFrontendLabel().getVal());

                            List<Map<String, Object>> attributeOptionMaps = (List<Map<String, Object>>) variantMap
                                .get("options");

                            if (attributeOptionMaps == null) {
                                attributeOptionMaps = new ArrayList<>();
                                variantMap.put("options", attributeOptionMaps);
                            }

                            if (!containsOption(attributeOptionMaps, optionId)) {
                                Map<String, Object> attributeOptionMap = new LinkedHashMap<>();

                                ContextObject<String> label = attributeOption.getLabel();

                                if (label != null) {
                                    String labelStr = label.getClosestValue();

                                    if (!Str.isEmpty(labelStr)) {
                                        attributeOptionMap.put("id", String.valueOf(optionId));
                                        attributeOptionMap.put("attr_id", String.valueOf(attribute.getId()));
                                        attributeOptionMap.put("value", attributeOption.getLabel().getClosestValue());
                                        attributeOptionMap.put("position", attributeOption.getPosition());

                                        List<String> inGroupWithOptions = findOptionsInGroupWith(
                                            attributeOption.getAttributeId(), optionId, variants);
                                        attributeOptionMap.put("inGroupWithOptions", inGroupWithOptions);

                                        attributeOptionMaps.add(attributeOptionMap);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            variantData.put("variant_products", variantProductsData);
            variantData.put("variant_options", variantOptionsData);
        }

        return variantData;
    }

    private Map<String, Object> getVariantMap(List<Map<String, Object>> variantMaps, String attributeCode) {
        Map<String, Object> returnMap = null;

        for (Map<String, Object> map : variantMaps) {
            if (attributeCode.equals(map.get("attribute_code"))) {
                returnMap = map;
                break;
            }
        }

        if (returnMap == null) {
            returnMap = new HashMap<>();
            variantMaps.add(returnMap);
        }

        return returnMap;
    }

    private List<String> findOptionsInGroupWith(Id attributeId, Id optionId, List<Product> variants) {
        List<String> inGroupWithOptions = new ArrayList<>();

        for (Product productVariant : variants) {
            if (!hasOption(optionId, productVariant) || !productVariant.isVisible())
                continue;

            List<AttributeValue> attributeValues = productVariant.getVariantAttributes();

            for (AttributeValue attributeValue : attributeValues) {
                if (!attributeValue.getOptionId().equals(optionId)
                    && !attributeValue.getAttributeId().equals(attributeId)
                    && !inGroupWithOptions.contains(attributeValue.getOptionId())) {
                    inGroupWithOptions.add(String.valueOf(attributeValue.getOptionId()));
                }
            }
        }

        return inGroupWithOptions;
    }

    private boolean hasOption(Id optionId, Product productVariant) {
        List<AttributeValue> attributeValues = productVariant.getVariantAttributes();

        for (AttributeValue attributeValue : attributeValues) {
            if (attributeValue.getOptionId().equals(optionId))
                return true;
        }

        return false;
    }

    private boolean containsOption(List<Map<String, Object>> attributeOptionMaps, Id id) {
        boolean containsOption = false;

        if (id != null && attributeOptionMaps != null && attributeOptionMaps.size() > 0) {
            for (Map<String, Object> optionMap : attributeOptionMaps) {
                if (String.valueOf(id).equals(String.valueOf(optionMap.get("id")))) {
                    containsOption = true;
                    break;
                }
            }
        }

        return containsOption;
    }

    protected Map<Id, AttributeOption> toMap(List<AttributeOption> attributeOptions) {
        if (attributeOptions == null)
            return null;

        Map<Id, AttributeOption> attributeOptionsMap = new HashMap<>();

        for (AttributeOption attributeOption : attributeOptions) {
            attributeOptionsMap.put(attributeOption.getId(), attributeOption);
        }

        return attributeOptionsMap;
    }

    protected Map<Id, Attribute> toAttributesMap(List<Attribute> attributes) {
        if (attributes == null)
            return null;

        Map<Id, Attribute> attributesMap = new HashMap<>();

        for (Attribute attribute : attributes) {
            attributesMap.put(attribute.getId(), attribute);
        }

        return attributesMap;
    }

    @Override
    public void completeProducts(List<Product> products, List<UrlRewrite> urlRewrites) {
        if (products == null || products.size() == 0)
            return;

        Map<Id, ContextObject<String>> productURIs = toURIMap(urlRewrites);

        for (Product p : products) {
            // Add UrlRewrite.
            ContextObject<String> uri = productURIs.get(p.getId());

            if (uri == null)
                uri = new ContextObject<String>();

            // Add the default URI in case there is none for the current
            // language.
            if (!uri.hasGlobalEntry())
                uri.addOrUpdateGlobal("/catalog/product/view/" + p.getId());

            p.setURI(uri);
        }
    }

    @Override
    public <T extends ProductIdObject> Id[] filterCompletedProductIds(List<T> productIdObjects, Id[] allProductIds) {
        List<Id> newProductIds = new ArrayList<>();

        List<Id> completedProductIds = toProductIds(productIdObjects);

        for (Id id : allProductIds) {
            if (!completedProductIds.contains(id)) {
                newProductIds.add(id);
            }
        }

        return newProductIds.toArray(new Id[newProductIds.size()]);
    }

    /**
     * Creates a new map grouping objects by their productId.
     *
     * @param idObjects
     * @return groupedObjects
     */
    @Override
    public <T extends ProductIdObject> Map<Id, List<T>> toProductIdListMap(List<T> objectsWithProductId) {
        Map<Id, List<T>> returnMap = new HashMap<>();

        for (T t : objectsWithProductId) {
            List<T> subList = returnMap.get(t.getProductId());

            if (subList == null) {
                subList = new ArrayList<>();
                returnMap.put(t.getProductId(), subList);
            }

            subList.add(t);
        }

        return returnMap;
    }

    protected <T extends ProductIdObject> List<Id> toProductIds(List<T> productIdObjects) {
        List<Id> ids = new ArrayList<>();

        if (productIdObjects == null || productIdObjects.size() == 0)
            return ids;

        for (ProductIdObject productIdObj : productIdObjects) {
            ids.add(productIdObj.getProductId());
        }

        return ids;
    }

    protected Map<Id, ContextObject<String>> toURIMap(List<UrlRewrite> urlRewrites) {
        Map<Id, ContextObject<String>> productsURIs = new HashMap<Id, ContextObject<String>>();

        if (urlRewrites == null || urlRewrites.size() == 0)
            return productsURIs;

        for (UrlRewrite urlRewrite : urlRewrites) {
            if (urlRewrite.isForProduct() && urlRewrite.getRequestURI() != null)
                productsURIs.put(urlRewrite.getTargetObjectId(), urlRewrite.getRequestURI());
        }

        return productsURIs;
    }

    @Override
    public String getAttributeOrConfigProperty(Product product, String attrName, String configPropertyName) {
        if (product.hasAttribute(attrName)) {
            return product.getAttribute(attrName).getStr();
        } else {
            return app.cpStr_(configPropertyName);
        }
    }

    @Override
    public void rememberCurrentProductList(String currentListName, List<Id> productIds) {
        app.sessionSet(currentListName, productIds);
    }

    @Override
    public List<Id> getCurrentProductList(String currentListName) {
        return app.sessionGet(currentListName);
    }

    @Override
    public Id getPreviousProductId(String currentListName, Id currentProductId) {
        if (currentListName == null || currentListName.isEmpty())
            currentListName = DEFAULT_CURRENT_LIST_NAME;

        List<Id> currentProductList = app.sessionGet(currentListName);
        if (currentProductList != null && !currentProductList.isEmpty()) {
            reverseCurrentProductListIfNeed(currentListName, currentProductList);

            for (Id currentProductListElement : currentProductList) {
                if (currentProductListElement.equals(currentProductId)
                    && !currentProductListElement.equals(currentProductList.get(0))) {
                    Id previousId = currentProductList.get(currentProductList.indexOf(currentProductListElement) - 1);
                    reverseCurrentProductListIfNeed(currentListName, currentProductList);
                    return previousId;
                }
            }

            reverseCurrentProductListIfNeed(currentListName, currentProductList);
        }

        return null;
    }

    @Override
    public Id getNextProductId(String currentListName, Id currentProductId) {
        if (currentListName == null || currentListName.isEmpty())
            currentListName = DEFAULT_CURRENT_LIST_NAME;

        List<Id> currentProductList = app.sessionGet(currentListName);

        if (currentProductList != null && !currentProductList.isEmpty()) {
            reverseCurrentProductListIfNeed(currentListName, currentProductList);

            for (Id currentProductListElement : currentProductList) {
                if (currentProductListElement.equals(currentProductId)
                    && !currentProductListElement.equals(currentProductList.get(currentProductList.size() - 1))) {
                    Id nextId = currentProductList.get(currentProductList.indexOf(currentProductListElement) + 1);
                    reverseCurrentProductListIfNeed(currentListName, currentProductList);
                    return nextId;
                }
            }

            reverseCurrentProductListIfNeed(currentListName, currentProductList);
        }

        return null;
    }

    private void reverseCurrentProductListIfNeed(String currentListName, List<Id> currentProductList) {
        if (currentListName.equals(TOP_SELLER_PRODUCT_LIST_NAME))
            Collections.reverse(currentProductList);
    }

    public void clearFromCache(Product product) {
        if (product == null || product.getId() == null)
            return;

    }
}
