package com.geecommerce.search.model;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model("search_keywords")
public class DefaultAutocompleteMapping extends AbstractMultiContextModel implements AutocompleteMapping {
    private static final long serialVersionUID = 1L;

    private Id id = null;
    private String keyword = null;
    private ContextObject<String> label = null;

    private Id targetObjectId = null;
    private ObjectType targetObjectType = null;
    private Boolean useTargetObjectLabel = null;

    private List<String> dividedKeyword = null;

    // Loaded lazily
    private String displayURI = null;
    private TargetSupport targetObject = null;

    private ContextObject<String> externalURL = null;

    // Repositories
    private final UrlRewrites urlRewrites;
    private final ProductLists productLists;
    private final Products products;

    @Inject
    public DefaultAutocompleteMapping(UrlRewrites urlRewrites, ProductLists productLists, Products products) {
        this.urlRewrites = urlRewrites;
        this.productLists = productLists;
        this.products = products;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AutocompleteMapping setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public AutocompleteMapping setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    @Override
    public ContextObject<String> getLabels() {
        return label;
    }

    @Override
    public String getLabel() {
        return label.getStr();
    }

    @Override
    public AutocompleteMapping setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.keyword = str_(map.get(Column.KEYWORD));
        this.label = ctxObj_(map.get(Column.LABEL));

        if (map.get(Column.TARGET_OBJECT_ID) != null)
            this.targetObjectId = id_(map.get(Column.TARGET_OBJECT_ID));

        if (map.get(Column.TARGET_OBJECT_TYPE) != null)
            this.targetObjectType = ObjectType.fromId(int_(map.get(Column.TARGET_OBJECT_TYPE)));

        if (map.get(Column.TARGET_OBJECT_LABEL) != null)
            this.useTargetObjectLabel = bool_(map.get(Column.TARGET_OBJECT_LABEL));

        if (map.get(Column.EXTERNAL_URL) != null)
            this.externalURL = ctxObj_(map.get(Column.EXTERNAL_URL));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.KEYWORD, getKeyword());
        map.put(Column.LABEL, getLabels());

        if (getTargetObjectId() != null)
            map.put(Column.TARGET_OBJECT_ID, getTargetObjectId());

        if (getTargetObjectType() != null)
            map.put(Column.TARGET_OBJECT_TYPE, getTargetObjectType().toId());

        if (this.useTargetObjectLabel != null)
            map.put(Column.TARGET_OBJECT_LABEL, this.useTargetObjectLabel);

        if (getExternalURL() != null)
            map.put(Column.EXTERNAL_URL, getExternalURL());

        return map;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public AutocompleteMapping setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public ObjectType getTargetObjectType() {
        return targetObjectType;
    }

    @Override
    public AutocompleteMapping setTargetObjectType(ObjectType targetObjectType) {
        this.targetObjectType = targetObjectType;
        return this;
    }

    @Override
    public boolean isUseTargetObjectLabel() {
        return useTargetObjectLabel == null ? false : useTargetObjectLabel;
    }

    @Override
    public AutocompleteMapping setUseTargetObjectLabel(Boolean useTargetObjectLabel) {
        this.useTargetObjectLabel = useTargetObjectLabel;
        return this;
    }

    @Override
    public ContextObject<String> getExternalURL() {
        return externalURL;
    }

    @Override
    public AutocompleteMapping setExternalURL(ContextObject<String> externalURL) {
        this.externalURL = externalURL;
        return this;
    }

    protected TargetSupport getTargetObject() {
        if (targetObject == null) {
            Object obj = null;

            switch (targetObjectType) {
            case PRODUCT_LIST:
                obj = (ProductList) productLists.findById(ProductList.class, targetObjectId);
                break;
            case PRODUCT:
                obj = (Product) products.findById(Product.class, targetObjectId);
                break;
            default:
                throw new RuntimeException("TargetObjectType '" + targetObjectType.name() + "' not supported yet.");
            }

            targetObject = (TargetSupport) obj;
        }

        return targetObject;
    }

    @Override
    public String getDisplayLabel() {
        TargetSupport tarObject = getTargetObject();
        return tarObject != null ? (tarObject.getLabel() != null ? tarObject.getLabel().getVal() : null) : null;
    }

    @Override
    public String getDisplayURI() {
        if (displayURI == null) {
            if (hasExternalURL()) {
                displayURI = externalURL.getStr();
            } else {
                UrlRewrite urlRewrite = urlRewrites.forTargetObject(targetObjectId, targetObjectType);

                if (urlRewrite != null)
                    displayURI = urlRewrite.getRequestURI().getClosestValue();
            }

        }

        return displayURI;
    }

    @Override
    public boolean isForProductList() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT_LIST);
    }

    @Override
    public boolean isForProduct() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT);
    }

    @Override
    public boolean isForCMS() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.CMS);
    }

    @Override
    public boolean hasExternalURL() {
        return !isForProductList() && !isForProduct() && !isForCMS() && externalURL != null
            && externalURL.getStr() != null;
    }

    @Override
    public List<String> getDividedKeyword() {
        return dividedKeyword;
    }

    @Override
    public AutocompleteMapping setDividedKeyword(List<String> dividedKeyword) {
        this.dividedKeyword = dividedKeyword;
        return this;
    }
}
