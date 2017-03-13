package com.geecommerce.guiwidgets.model;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Model("product_promotions")
public class DefaultProductPromotion extends AbstractMultiContextModel implements ProductPromotion {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.LIMIT)
    private Integer limit = null;

    @Column(Col.TARGET_OBJECT_ID)
    private Id targetObjectId = null;

    @Column(Col.TARGET_OBJECT_TYPE)
    private ObjectType targetObjectType = null;

    @Column(Col.TARGET_OBJECT_LABEL)
    private boolean useTargetObjectLabel = false;

    @Column(Col.TEASER_IMAGE)
    private Id teaserImageId = null;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled;

    // Loaded lazily
    private String displayURI = null;
    private TargetSupport targetObject = null;
    private MediaAsset teaserImage = null;

    private final MediaAssetService mediaAssetService;
    private final UrlRewrites urlRewrites;
    private final ProductLists productLists;
    private final Products products;

    @Inject
    public DefaultProductPromotion(MediaAssetService mediaAssetService, UrlRewrites urlRewrites,
        ProductLists productLists, Products products) {
        this.mediaAssetService = mediaAssetService;
        this.urlRewrites = urlRewrites;
        this.productLists = productLists;
        this.products = products;
    }

    @Override
    public ProductPromotion setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ProductPromotion setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ProductPromotion setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public ProductPromotion setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public ProductPromotion setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public ObjectType getTargetObjectType() {
        return targetObjectType;
    }

    @Override
    public ProductPromotion setTargetObjectType(ObjectType targetObjectType) {
        this.targetObjectType = targetObjectType;
        return this;
    }

    @Override
    public boolean isUseTargetObjectLabel() {
        return useTargetObjectLabel;
    }

    @Override
    public ProductPromotion setUseTargetObjectLabel(boolean useTargetObjectLabel) {
        this.useTargetObjectLabel = useTargetObjectLabel;
        return this;
    }

    protected TargetSupport getTargetObject() {
        if (targetObject == null && targetObjectType != null) {
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
        if (useTargetObjectLabel) {
            TargetSupport tarObject = getTargetObject();
            return tarObject != null ? (tarObject.getLabel() != null ? tarObject.getLabel().getVal() : "???") : "???";
        } else {
            return getLabel() != null ? getLabel().getVal() : "???";
        }

    }

    @Override
    public ContextObject<String> getContextDisplayLabel() {
        if (useTargetObjectLabel) {
            TargetSupport tarObject = getTargetObject();
            return tarObject != null ? tarObject.getLabel() : null;
        } else {
            return getLabel();
        }
    }

    @Override
    public String getDisplayURI() {
        if (displayURI == null) {
            UrlRewrite urlRewrite = urlRewrites.forTargetObject(targetObjectId, targetObjectType);

            if (urlRewrite != null)
                displayURI = ContextObjects.findCurrentLanguageOrGlobal(urlRewrite.getRequestURI());
            else {
                TargetSupport targetSupport = getTargetObject();

                if (targetSupport != null)
                    displayURI = targetSupport.getURI().getStr();
            }
        }

        return displayURI == null ? "???" : displayURI;
    }

    @JsonIgnore
    @Override
    public boolean isForProductList() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT_LIST);
    }

    @JsonIgnore
    @Override
    public boolean isForProduct() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT);
    }

    @Override
    public MediaAsset getTeaserImage() {
        if (teaserImage == null && teaserImageId != null)
            teaserImage = mediaAssetService.get(teaserImageId);
        return teaserImage;
    }

    @Override
    public ProductPromotion setTeaserImage(MediaAsset teaserImage) {
        if (teaserImage == null) {
            this.teaserImage = null;
            this.teaserImageId = null;
        } else {
            this.teaserImage = teaserImage;
            this.teaserImageId = teaserImage.getId();
        }

        return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public ProductPromotion setEnabled(ContextObject<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Id getTeaserImageId() {
        return teaserImageId;
    }

    @Override
    public ProductPromotion setTeaserImageId(Id teaserImageId) {
        this.teaserImageId = teaserImageId;
        this.teaserImage = null;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }
}
