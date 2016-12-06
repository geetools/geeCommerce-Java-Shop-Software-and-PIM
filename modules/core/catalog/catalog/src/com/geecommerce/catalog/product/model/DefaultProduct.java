package com.geecommerce.catalog.product.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.util.Strings;

import com.geecommerce.catalog.product.MediaType;
import com.geecommerce.catalog.product.ProductConstant;
import com.geecommerce.catalog.product.elasticsearch.helper.ElasticsearchProductHelper;
import com.geecommerce.catalog.product.repository.CatalogMedia;
import com.geecommerce.catalog.product.repository.ProductConnectionIndexes;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.elasticsearch.annotation.Indexable;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.ChildSupport;
import com.geecommerce.core.service.CopySupport;
import com.geecommerce.core.service.DataSupport;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.service.ParentSupport;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.repository.Stocks;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.price.service.PriceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;
import com.sun.xml.txw2.annotation.XmlAttribute;
//import org.apache.sis.internal.converter.StringConverter;

@Cacheable
@Indexable(collection = "product")
@Model(collection = "products", autoPopulate = false, optimisticLocking = true, history = true)
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultProduct extends AbstractAttributeSupport
    implements Product, ParentSupport<Product>, ChildSupport<Product>, AttributeSupport, TargetSupport, PageSupport,
    DataSupport, SearchIndexSupport, CopySupport<Product> {
    private static final long serialVersionUID = -756836049391579472L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.ID2)
    protected String id2 = null;

    @Column(Col.EAN)
    protected Long ean = null;

    @Column(Col.TYPE)
    protected ProductType type = null;

    @Column(Col.SALEABLE)
    protected ContextObject<Boolean> saleable = null;

    @Column(Col.VISIBLE)
    protected ContextObject<Boolean> visible = null;

    @Column(Col.VISIBLE_FROM)
    protected ContextObject<Date> visibleFrom = null;

    @Column(Col.VISIBLE_TO)
    protected ContextObject<Date> visibleTo = null;

    @Column(Col.VISIBLE_IN_PRODUCT_LIST)
    protected ContextObject<Boolean> visibleInProductList = null;

    @Column(Col.PARENT_ID)
    protected Id parentId = null;

    @Column(Col.VARIANTS)
    protected List<Id> variantProductIds = null;

    @Column(Col.UPSELL_PRODUCTS)
    protected List<Id> upsellProductIds = null;

    @Column(Col.CROSS_SELL_PRODUCTS)
    protected List<Id> crossSellProductIds = null;

    @Column(Col.BUNDLE_PRODUCTS)
    protected List<BundleProductItem> bundleProductItems = null;

    @Column(Col.PROGRAMME_PRODUCTS)
    protected List<Id> programmeProductIds = null;

    @Column(Col.INCLUDE_IN_FEEDS)
    protected ContextObject<Boolean> includeInFeeds = null;

    @Column(Col.DELETED)
    protected Boolean deleted = null;

    @Column(Col.BUNDLE_AS_SINGLE_PRODUCT)
    protected Boolean bundleAsSingleProduct = null;

    @Column(Col.DELETED_NOTE)
    protected String deletedNote = null;

    /* Data support map */

    protected Map<String, Object> data = null;

    /* Services and repositories */

    protected transient final Products products;

    protected transient final PriceService priceService;

    protected transient final Stocks stocks;

    protected transient final CatalogMedia catalogMedia;

    protected transient final UrlRewrites urlRewrites;

    protected transient final ProductConnectionIndexes productConnectionIndexes;

    protected transient final ElasticsearchProductHelper elasticsearchProductHelper;

    /* Load from DB on demand */

    @JsonIgnore
    protected ContextObject<String> uri = null;

    @JsonIgnore
    protected Product parent = null;

    @JsonIgnore
    protected List<Product> variants = null;

    protected List<Id> assets = null;

    protected Map<String, List<Id>> productLinks = null;

    @JsonIgnore
    protected List<Product> upsellProducts = null;

    @JsonIgnore
    protected List<Product> crossSellProducts = null;

    @JsonIgnore
    protected List<Product> bundleProducts = null;

    @JsonIgnore
    protected List<Product> programmeProducts = null;

    @JsonIgnore
    protected List<Product> programmeParents = null;

    protected static final String PROP_KEY_VARIANT = "variant";

    protected static final String SCRIPT_KEY_PRODUCT = "product";

    protected static final String IS_VISIBLE_SCRIPT_KEY = "catalog/product/script/is_visible";

    protected String defaultCurrency = null;
    protected Map<String, PriceResult> priceResultMap = new HashMap<>();

    public DefaultProduct() {
        this(i(Products.class), i(PriceService.class), i(Stocks.class), i(CatalogMedia.class), i(UrlRewrites.class),
            i(ProductConnectionIndexes.class), i(ElasticsearchProductHelper.class));
    }

    @Inject
    public DefaultProduct(Products products, PriceService priceService, Stocks stocks, CatalogMedia catalogMedia,
        UrlRewrites urlRewrites, ProductConnectionIndexes productConnectionIndexes,
        ElasticsearchProductHelper elasticsearchProductHelper) {
        this.products = products;
        this.priceService = priceService;
        this.stocks = stocks;
        this.catalogMedia = catalogMedia;
        this.urlRewrites = urlRewrites;
        this.productConnectionIndexes = productConnectionIndexes;
        this.elasticsearchProductHelper = elasticsearchProductHelper;
    }

    @Override
    @XmlAttribute
    public Id getId() {
        return id;
    }

    @Override
    public Product setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    @XmlAttribute
    public String getId2() {
        return id2;
    }

    @Override
    public Product setId2(String id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public Long getEan() {
        return ean;
    }

    @Override
    public Product setEan(Long ean) {
        this.ean = ean;
        return this;
    }

    @Override
    @XmlAttribute
    public ProductType getType() {
        return type;
    }

    @Override
    public Product setType(ProductType type) {
        this.type = type;
        return this;
    }

    @JsonIgnore
    @Override
    public boolean isValidForSelling() {
        Boolean isValid = threadGet(this, "isValidForSelling");

        if (isValid == null) {
            if (!isVariantMaster() && !isProgramme()) {
                isValid = isSalable() && isInStock() && visibilityFlagsOK(true) && hasValidPrice();
            } else {
                isValid = false;
            }

            threadPut(this, "isValidForSelling", isValid);
        }

        return isValid;
    }

    @Override
    public boolean hasVariantsValidForSelling() {
        boolean hasVariantsValidForSelling = false;

        if (isVariantMaster()) {
            for (Product variant : getVariants()) {
                if (variant.isValidForSelling()) {
                    hasVariantsValidForSelling = true;
                    break;
                }
            }
        }

        return hasVariantsValidForSelling;
    }

    @JsonIgnore
    @Override
    public boolean hasValidPrice() {
        PriceResult pr = getPrice();
        return pr != null && pr.hasAnyValidPrice();
    }

    @JsonIgnore
    @Override
    public boolean isInStock() {
        int minQty = app.cpInt_("inventory/stock/min_qty", 0);

        return getQty() >= minQty || isAllowBackorder();
    }

    @JsonIgnore
    @Override
    public boolean isInStock(int qty) {
        int minQty = app.cpInt_("inventory/stock/min_qty", 0);

        return (getQty() >= minQty && getQty() >= qty) || isAllowBackorder();
    }

    @Override
    public boolean hasSalableVariants() {
        boolean isSalable = false;

        if (isVariantMaster()) {
            for (Product variant : getVariants()) {
                if (variant.isSalable()) {
                    isSalable = true;
                    break;
                }
            }
        }

        return isSalable;
    }

    @JsonIgnore
    @Override
    public boolean isSalable() {
        return saleable == null ? false : saleable.getBoolean().booleanValue();
    }

    @Override
    @XmlAttribute
    public ContextObject<Boolean> getSaleable() {
        return saleable;
    }

    @Override
    public Product setSaleable(ContextObject<Boolean> saleable) {
        this.saleable = saleable;
        return this;
    }

    @Override
    @XmlAttribute
    public ContextObject<Boolean> getVisible() {
        return visible;
    }

    @Override
    public Product setVisible(ContextObject<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    @Override
    @XmlAttribute
    public ContextObject<Date> getVisibleFrom() {
        return visibleFrom;
    }

    @Override
    public Product setVisibleFrom(ContextObject<Date> visibleFrom) {
        this.visibleFrom = visibleFrom;
        return this;
    }

    @Override
    @XmlAttribute
    public ContextObject<Date> getVisibleTo() {
        return visibleTo;
    }

    @Override
    public Product setVisibleTo(ContextObject<Date> visibleTo) {
        this.visibleTo = visibleTo;
        return this;
    }

    @Override
    @XmlAttribute
    public ContextObject<Boolean> getVisibleInProductList() {
        return visibleInProductList;
    }

    @Override
    public Product setVisibleInProductList(ContextObject<Boolean> visibleInProductList) {
        this.visibleInProductList = visibleInProductList;
        return this;
    }

    @JsonIgnore
    @Override
    public boolean isVisible() {
        Boolean isVisible = threadGet(this, "isVisible");

        if (isVisible != null) {
            return isVisible;
        } else {
            isVisible = true;

            // long start = System.currentTimeMillis();

            // If this product is a variant-master, make sure that at least one
            // variant is visible.
            if (isVariantMaster()) {
                isVisible = hasValidVariants() && hasVariantsValidForSelling();
            }
            // If this product is a programme, make sure that at least one
            // linked product is visible.
            else if (isProgramme()) {
                // If the programme has no child-products, there is no
                // child-status to be dependant on, so we just
                // return
                // true.
                boolean visibleProductExists = true;

                if (programmeProductIds != null && programmeProductIds.size() > 0) {
                    List<Product> programmeProducts = getProgrammeProducts();

                    visibleProductExists = false;

                    for (Product pp : programmeProducts) {
                        if (pp == null || getId().equals(pp.getId()))
                            continue;

                        if (pp.isVisible()) {
                            visibleProductExists = true;
                            break;
                        }
                    }
                }

                if (!visibleProductExists)
                    isVisible = false;
            }
            // If this product is a bundle, make sure that at least one linked
            // product is visible.
            else if (isBundle()) {
                List<Product> bundleProducts = getBundleProducts();

                boolean visibleProductExists = false;

                for (Product bp : bundleProducts) {
                    if (bp == null || getId().equals(bp.getId()))
                        continue;

                    if (bp.isVisible()) {
                        visibleProductExists = true;
                        break;
                    }
                }

                if (!visibleProductExists)
                    isVisible = false;
            }
            // Otherwise just check 'this' product which must be a single
            // product or variant.
            else {
                isVisible = visibilityFlagsOK(true);
            }

            boolean retIsVisible = !isDeleted() && visibilityFlagsOK(false) && isVisible;

            threadPut(this, "isVisible", retIsVisible);

            return retIsVisible;
        }
    }

    private boolean visibilityFlagsOK(boolean isEvaluateScript) {
        boolean isVisible = visible == null ? false : visible.getBoolean().booleanValue();

        if (!isVisible)
            return isVisible;

        String isVisibleScript = app.cpStr_(IS_VISIBLE_SCRIPT_KEY);

        if (isVisible && isEvaluateScript && isVisibleScript != null)
            isVisible = isVisible && Groovy.conditionMatches(isVisibleScript, SCRIPT_KEY_PRODUCT, this);

        if (!isVisible)
            return isVisible;

        // Check article status.
        AttributeValue statusArticle = getAttribute("status_article");
        AttributeOption attrOption = null;

        if (statusArticle != null) {
            ContextObject<List<Id>> xOptionIds = statusArticle.getXOptionIds();

            if (xOptionIds != null && !xOptionIds.isEmpty()) {
                List<Id> optionId = xOptionIds.getClosestValue();

                if (optionId != null && !optionId.isEmpty())
                    attrOption = statusArticle.getAttribute().getOption(optionId.get(0));
            }
        }

        String articleStatus = statusArticle == null || attrOption == null ? "approval_required"
            : attrOption.getLabel().getGlobalValue();
        isVisible = isVisible && "approved".equals(articleStatus);

        return isVisible;
    }

    @Override
    public ContextObject<Boolean> getIncludeInFeeds() {
        return includeInFeeds;
    }

    @Override
    public Product setIncludeInFeeds(ContextObject<Boolean> includeInFeeds) {
        this.includeInFeeds = includeInFeeds;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted == null ? false : deleted;
    }

    @Override
    @XmlAttribute
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isBundleAsSingleProduct() {
        return bundleAsSingleProduct == null ? false : bundleAsSingleProduct;
    }

    @Override
    @XmlAttribute
    public Boolean getBundleAsSingleProduct() {
        return bundleAsSingleProduct;
    }

    @Override
    public void setBundleAsSingleProduct(Boolean bundleAsSingleProduct) {
        this.bundleAsSingleProduct = bundleAsSingleProduct;
    }


    @Override
    @XmlAttribute
    public String getDeletedNote() {
        return deletedNote;
    }

    @Override
    public Product setDeletedNote(String deletedNote) {
        this.deletedNote = deletedNote;
        return this;
    }

    @JsonIgnore
    @Override
    public String getArticleNumber() {
        AttributeValue attr = getAttribute("article_number", false);
        return attr == null ? null : attr.getString();
    }

    @JsonIgnore
    @Override
    public String getName() {
        AttributeValue attr = getAttribute("name", true);
        return attr == null ? null : attr.getString();
    }

    @JsonIgnore
    @Override
    public String getName2() {
        AttributeValue attr = getAttribute("name2", true);
        return attr == null ? null : attr.getString();
    }

    @JsonIgnore
    @Override
    public String getDescription() {
        AttributeValue attr = getAttribute("description", true);
        return attr == null ? null : attr.getString();
    }

    @JsonIgnore
    @Override
    public String getShortDescription() {
        AttributeValue attr = getAttribute("short_description", true);
        return attr == null ? null : attr.getString();
    }

    @Override
    public boolean isVariant() {
        List<AttributeValue> variantAttributes = getVariantAttributes();

        return parentId != null && variantAttributes != null && variantAttributes.size() > 0;
    }

    @Override
    public boolean isVariantMaster() {
        return variantProductIds != null && variantProductIds.size() > 0;
    }

    @Override
    @XmlAttribute
    public Id getParentId() {
        return parentId;
    }

    @JsonIgnore
    @Override
    public Product getParent() {
        if (parent == null && parentId != null) {
            parent = products.findById(Product.class, parentId);
        }

        return parent;
    }

    @JsonIgnore
    @Override
    public Product setParent(Product parent) {
        if (parent == null || parent.getId() == null)
            throw new NullPointerException("Parent or parent.id cannot be null");

        this.parent = parent;
        this.parentId = parent.getId();

        return this;
    }

    @Override
    public Product unsetParent() {
        this.parentId = null;
        return this;
    }

    @Override
    public boolean hasValidVariants() {
        boolean hasValidVariants = false;

        if (isVariantMaster()) {
            for (Product variant : getVariants()) {
                if (variant != null && variant.isVariant() && variant.isValidVariant() && variant.isVisible()) {
                    hasValidVariants = true;
                    break;
                }
            }

            return hasValidVariants;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValidVariant() {
        List<AttributeValue> variantAttributes = getVariantAttributes();
        return variantAttributes != null && variantAttributes.size() > 0;
    }

    @Override
    public List<Id> getVariantProductIds() {
        return variantProductIds;
    }

    @Override
    public List<AttributeValue> getVariantAttributes() {
        return super.getAttributesHavingProperty(PROP_KEY_VARIANT);
    }

    @Override
    public Product addVariantAttribute(Attribute attribute, Id optionId) {
        if (attribute == null || optionId == null)
            throw new NullPointerException("Attribute or optionId cannot be null");

        if (isVariantMaster())
            throw new IllegalStateException(
                "A variant holder cannot have variant-attributes. This product is the holder of "
                    + variantProductIds.size() + " variant products. Adding attribute=" + attribute.getCode()
                    + ", optionId=" + optionId + " failed.");

        super.addAttribute(app.model(AttributeValue.class).forAttribute(attribute).addOptionId(optionId)
            .addProperty(PROP_KEY_VARIANT, true));

        return this;
    }

    @Override
    public Product addVariantAttribute(String attributeCode, Id optionId) {
        if (attributeCode == null || optionId == null)
            throw new NullPointerException("AttributeCode or optionId cannot be null");

        if (isVariantMaster())
            throw new IllegalStateException(
                "A variant holder cannot have variant-attributes. This product is the holder of "
                    + variantProductIds.size() + " variant products. Adding attributeCode=" + attributeCode
                    + ", optionId=" + optionId + " failed.");

        super.addAttribute(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode)
            .addOptionId(optionId).addProperty(PROP_KEY_VARIANT, true));

        return this;
    }

    @JsonIgnore
    @Override
    public List<Product> getVariants() {
        if (variantProductIds == null || variantProductIds.size() == 0)
            return null;

        if (variants == null) {
            variants = products.findByIds(Product.class, variantProductIds.toArray(new Id[variantProductIds.size()]));
        }

        return variants;
    }

    @JsonIgnore
    @Override
    public List<Product> getChildren() {
        return getVariants();
    }

    @JsonIgnore
    @Override
    public List<Product> getAnyChildren() {
        if (isProgramme() && hasValidProgrammeProducts()) {
            return getProgrammeProducts();
        } else if (isVariantMaster() && hasVariantsValidForSelling()) {
            return getVariants();
        }
        if (isBundle() && hasValidBundleProducts()) {
            return getBundleProducts();
        }

        return null;
    }

    @JsonIgnore
    @Override
    public boolean isValidChild() {
        if (isProgramme()) {
            return hasValidProgrammeProducts();
        } else if (isVariantMaster()) {
            return hasVariantsValidForSelling();
        }
        if (isBundle()) {
            return hasValidBundleProducts();
        } else {
            return isValidForSelling();
        }
    }

    @Override
    public Product addVariant(Product variantProduct) {
        if (variantProduct == null || variantProduct.getId() == null)
            throw new NullPointerException("Product variant or its id cannot be null");

        if (isVariant())
            throw new IllegalStateException("A variant cannot be added to another variant product");

        if (variantProductIds == null)
            variantProductIds = new ArrayList<>();

        if (!variantProductIds.contains(variantProduct.getId())) {
            variantProductIds.add(variantProduct.getId());
        }

        variantProduct.setParent(this);

        // reset lazy-loaded list.
        variants = null;

        return this;
    }

    @Override
    public Product removeVariant(Product variantProduct) {
        if (variantProduct == null || variantProduct.getId() == null)
            throw new NullPointerException("Product variant or its id cannot be null");

        if (variantProductIds != null && variantProductIds.contains(variantProduct.getId())) {
            variantProductIds.remove(variantProduct.getId());
        }

        variantProduct.unsetParent();

        // reset lazy-loaded list.
        variants = null;

        return this;
    }

    @Override
    public Product findMatchingVariant(Id... optionIds) {
        if (variantProductIds == null || variantProductIds.size() == 0 || optionIds == null || optionIds.length == 0)
            return null;

        List<Product> productVariants = getVariants();

        List<Id> paramOptionIds = Lists.newArrayList(optionIds);

        Product foundProductVariant = null;

        for (Product product : productVariants) {
            boolean foundMatch = false;

            List<AttributeValue> variantAttributes = product.getVariantAttributes();

            for (AttributeValue attributeValue : variantAttributes) {
                Id optionId = attributeValue.getOptionId();

                if (paramOptionIds.contains(optionId)) {
                    foundMatch = true;
                } else {
                    foundMatch = false;
                    break;
                }

            }

            if (foundMatch) {
                foundProductVariant = product;
                break;
            }
        }

        return foundProductVariant;
    }

    @Override
    @JsonProperty("uri")
    public ContextObject<String> getURI() {
        if (uri == null) {
            UrlRewrite urlRewrite = urlRewrites.forProduct(getId());

            if (urlRewrite != null)
                uri = urlRewrite.getRequestURI();

            if (uri == null)
                uri = new ContextObject<String>();

            if (!uri.hasGlobalEntry())
                uri.addOrUpdateGlobal("/catalog/product/view/" + getId());
        }

        return uri;
    }

    @Override
    public Product setURI(ContextObject<String> uri) {
        this.uri = uri;
        return this;
    }

    @JsonIgnore
    @Override
    public PriceResult getPrice() {
        // if (defaultCurrency == null)
        String baseCurrency = app.getBaseCurrency();

        String key = new StringBuilder(PriceResult.class.getSimpleName()).append(Char.UNDERSCORE).append(baseCurrency)
            .toString();

        PriceResult pr = threadGet(this, key);

        if (pr == null) {
            pr = getPriceFor(baseCurrency);
            threadPut(this, key, pr);
        }

        return pr;
    }

    @JsonIgnore
    @Override
    public PriceResult getPriceFor(String currencyCode) {
        if (priceResultMap.get(currencyCode) == null) {
            Map<Id, PriceResult> priceResults = app.registryGet(ProductConstant.PRELOADED_PRODUCT_PRICES);

            if (priceResults != null) {
                PriceResult pr = priceResults.get(getId());

                if (pr != null) {
                    priceResultMap.put(currencyCode, pr);
                    return pr;
                }
            }

            Set<Id> childProductIds = getSellableChildProductIds();

            PriceResult pr = null;

            if (childProductIds != null && childProductIds.size() > 0) {
                pr = priceService.getPriceFor(getId(), currencyCode,
                    childProductIds.toArray(new Id[childProductIds.size()]));
            } else {
                pr = priceService.getPriceFor(getId(), currencyCode);
            }
            priceResultMap.put(currencyCode, pr);
            return pr;
        }
        return priceResultMap.get(currencyCode);
    }

    @JsonIgnore
    @Override
    public Integer getQty() {
        // long start = System.currentTimeMillis();

        Map<String, Object> stockData = getStockData();

        // System.out.println("++++++ getQty TIME: " +
        // (System.currentTimeMillis() - start));

        return stockData == null || stockData.get("qty") == null ? 0 : (Integer) stockData.get("qty");
    }

    @JsonIgnore
    @Override
    public boolean isAllowBackorder() {
        boolean isAllowBackorder = app.cpBool_("inventory/stock/allow_backorder", false);

        // long start = System.currentTimeMillis();

        Map<String, Object> stockData = getStockData();

        // System.out.println("++++++ isAllowBackorder TIME: " +
        // (System.currentTimeMillis() - start));

        return stockData == null || (isAllowBackorder && stockData.get("allow_backorder") == null ? false
            : (Boolean) stockData.get("allow_backorder"));
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getStockData() {
        return stocks.getStockData(getId(), app.context().getStore());
    }

    @JsonIgnore
    @Override
    public List<CatalogMediaAsset> getImages() {
        List<CatalogMediaAsset> images = threadGet(this, "images");

        if (images == null) {
            images = catalogMedia.enabledImagesFor(this);
            threadPut(this, "images", images);
        }

        return images;
    }

    @JsonIgnore
    @Override
    public List<CatalogMediaAsset> getVideos() {
        List<CatalogMediaAsset> videos = threadGet(this, "videos");

        if (videos == null) {
            videos = catalogMedia.enabledVideosFor(this);
            threadPut(this, "videos", videos);
        }

        return videos;
    }

    @JsonIgnore
    @Override
    public List<CatalogMediaAsset> getDocuments() {
        List<CatalogMediaAsset> documents = threadGet(this, "documents");

        if (documents == null) {
            documents = catalogMedia.enabledDocumentsFor(this);
            threadPut(this, "documents", documents);
        }

        return documents;
    }

    @Override
    public String getMainImageURI() {
        CatalogMediaAsset image = getMainImage();
        return image == null ? null : image.getWebDetailPath();
    }

    @Override
    public CatalogMediaAsset getMainImage() {
        CatalogMediaAsset image = null;

        List<CatalogMediaAsset> images = getImages();

        if (images != null && images.size() > 0) {
            for (CatalogMediaAsset pImage : images) {
                if (pImage != null && pImage.isEnabled() && pImage.isProductMainImage()) {
                    image = pImage;
                    break;
                }
            }
        }

        return image == null ? null : image;
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getAssemblyInstructions() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_ASSEMBLY_INSTRUCTIONS);

        return getMediaAssetsMaps(catalogMediaType, getDocuments());
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getProductInstructions() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_INSTRUCTIONS);

        return getMediaAssetsMaps(catalogMediaType, getDocuments());
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getModelList() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_MODEL_LIST);

        return getMediaAssetsMaps(catalogMediaType, getDocuments());
    }

    @JsonIgnore
    private List<Map<String, Object>> getMediaAssetsMaps(CatalogMediaType catalogMediaType,
        List<CatalogMediaAsset> mediaAssets) {
        List<Map<String, Object>> imageMaps = new ArrayList<>();

        for (CatalogMediaAsset productImage : mediaAssets) {
            Map<String, Object> imageMap = new HashMap<>();

            if (productImage.isEnabled() && catalogMediaType != null && productImage.getMediaTypeIds() != null
                && productImage.getMediaTypeIds().contains(catalogMediaType.getId())) {
                if (productImage.getWebPath() == null || "".equals(productImage.getWebPath().trim()))
                    continue;

                imageMap.put("path", productImage.getPath());

                imageMap.put("preview", productImage.getPreviewImagePath());

                if (productImage.getTitle() != null) {
                    imageMap.put("title", productImage.getTitle().getStr());
                }

                imageMap.put("pos", productImage.getPosition());

                imageMaps.add(imageMap);
            }
        }
        return imageMaps;
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getImagesMaps() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_GALLERY_IMAGE);

        return getMediaAssetsMaps(catalogMediaType, getImages());
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getSurfaceImagesMaps() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_GALLERY_SURFACE);

        return getMediaAssetsMaps(catalogMediaType, getImages());
    }

    @JsonIgnore
    @Override
    public List<Map<String, Object>> getVideoImagesMaps() {
        CatalogMediaType catalogMediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class,
            CatalogMediaType.Col.KEY, MediaType.PRODUCT_GALLERY_VIDEO);

        return getMediaAssetsMaps(catalogMediaType, getVideos());
    }

    @JsonIgnore
    @Override
    public String getCat1ImageURI() {
        CatalogMediaAsset image = null;

        List<CatalogMediaAsset> images = getImages();

        if (images != null && !images.isEmpty()) {
            for (CatalogMediaAsset pImage : images) {
                if (pImage.isEnabled() && pImage.isProductListImage1()) {
                    image = pImage;
                }
            }
        }

        if (image == null)
            image = getMainImage();

        return image == null ? null : image.getWebListPath();
    }

    @JsonIgnore
    @Override
    public String getCat2ImageURI() {
        CatalogMediaAsset image = null;

        List<CatalogMediaAsset> images = getImages();

        if (images != null && !images.isEmpty()) {
            for (CatalogMediaAsset pImage : images) {
                if (pImage.isEnabled() && pImage.isProductListImage2())
                    image = pImage;
            }
        }

        return image == null ? null : image.getWebListPath();
    }

    @Override
    @XmlAttribute
    public List<Id> getUpsellProductIds() {
        return upsellProductIds;
    }

    @Override
    public Product addUpsellProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or product.id cannot be null");

        if (upsellProductIds == null)
            upsellProductIds = new ArrayList<>();

        if (!upsellProductIds.contains(product.getId())) {
            upsellProductIds.add(product.getId());
        }

        // reset lazy-loaded list.
        upsellProducts = null;

        return this;
    }

    @Override
    public Product addUpsellProductIds(Id... upsellProductIds) {
        if (this.upsellProductIds == null) {
            this.upsellProductIds = new ArrayList<>();
        }

        this.upsellProductIds.addAll(Arrays.asList(upsellProductIds));

        return this;
    }

    @Override
    public Product setUpsellProductIds(List<Id> upsellProductIds) {
        this.upsellProductIds = upsellProductIds;
        return this;
    }

    @Override
    public Product removeUpsellProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or its id cannot be null");

        if (upsellProductIds != null && upsellProductIds.contains(product.getId())) {
            upsellProductIds.remove(product.getId());
        }

        // reset lazy-loaded list.
        upsellProducts = null;

        return this;
    }

    @JsonIgnore
    @Override
    public boolean hasUpsells() {
        return upsellProductIds != null && upsellProductIds.size() > 0;
    }

    @JsonIgnore
    @Override
    public boolean hasValidUpsells() {
        if (!hasUpsells())
            return false;

        boolean hasValidUpsells = false;

        List<Product> upsells = getUpsells();

        for (Product p : upsells) {
            if ((p.isValidForSelling() || p.hasVariantsValidForSelling()
                || (p.isProgramme() && !p.hasProgrammeProducts()) || p.hasProgrammeProductsValidForSelling())
                && p.isVisible()) {
                hasValidUpsells = true;
                break;
            }
        }

        return hasValidUpsells;
    }

    @JsonIgnore
    @Override
    public List<Product> getUpsells() {
        if (hasUpsells()) {
            upsellProducts = products.findByIds(Product.class,
                upsellProductIds.toArray(new Id[upsellProductIds.size()]));
        }

        return upsellProducts;
    }

    @JsonIgnore
    @Override
    public List<Product> getValidUpsells() {
        List<Product> upsells = getUpsells();
        List<Product> validUpsellProducts = null;

        if (upsells != null) {
            validUpsellProducts = new ArrayList<>();

            for (Product p : upsells) {
                if ((p.isValidForSelling() || p.hasVariantsValidForSelling()
                    || (p.isProgramme() && !p.hasProgrammeProducts()) || p.hasProgrammeProductsValidForSelling())
                    && p.isVisible()) {
                    validUpsellProducts.add(p);
                }
            }
        }

        return validUpsellProducts;
    }

    @Override
    @XmlAttribute
    public List<Id> getCrossSellProductIds() {
        return crossSellProductIds;
    }

    @Override
    public Product addCrossSellProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or product.id cannot be null");

        if (crossSellProductIds == null)
            crossSellProductIds = new ArrayList<>();

        if (!crossSellProductIds.contains(product.getId())) {
            crossSellProductIds.add(product.getId());
        }

        // reset lazy-loaded list.
        crossSellProducts = null;

        return this;
    }

    @Override
    public Product addCrossSellProductIds(Id... crossSellingProductIds) {
        if (this.crossSellProductIds == null) {
            this.crossSellProductIds = new ArrayList<>();
        }

        this.crossSellProductIds.addAll(Arrays.asList(crossSellingProductIds));

        return this;
    }

    @Override
    public Product setCrossSellProductIds(List<Id> crossSellProductIds) {
        this.crossSellProductIds = crossSellProductIds;
        return this;
    }

    @Override
    public Product removeCrossSellProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or its id cannot be null");

        if (crossSellProductIds != null && crossSellProductIds.contains(product.getId())) {
            crossSellProductIds.remove(product.getId());
        }

        // reset lazy-loaded list.
        crossSellProducts = null;

        return this;
    }

    @JsonIgnore
    @Override
    public boolean hasCrossSells() {
        return crossSellProductIds != null && crossSellProductIds.size() > 0;
    }

    @JsonIgnore
    @Override
    public boolean hasValidCrossSells() {
        if (!hasCrossSells())
            return false;

        boolean hasValidCrossSells = false;

        List<Product> crossSells = getCrossSells();

        for (Product p : crossSells) {
            if ((p.isValidForSelling() || p.hasVariantsValidForSelling()) && p.isVisible()) {
                hasValidCrossSells = true;
                break;
            }
        }

        return hasValidCrossSells;
    }

    @JsonIgnore
    @Override
    public List<Product> getCrossSells() {
        if (hasCrossSells()) {
            crossSellProducts = products.findByIds(Product.class,
                crossSellProductIds.toArray(new Id[crossSellProductIds.size()]));
        }

        return crossSellProducts;
    }

    @JsonIgnore
    @Override
    public boolean isBundle() {
        return hasBundleProducts();
    }

    @JsonIgnore
    @Override
    public boolean hasBundleProducts() {
        return bundleProductItems != null && bundleProductItems.size() > 0;
    }

    @JsonIgnore
    @Override
    public boolean hasValidBundleProducts() {
        if (isBundle() || !hasBundleProducts())
            return false;

        boolean hasValidBundleProducts = false;

        List<Product> bundleProducts = getBundleProducts();

        for (Product p : bundleProducts) {
            if ((p.isValidForSelling() || p.hasVariantsValidForSelling()) && p.isVisible()) {
                hasValidBundleProducts = true;
                break;
            }
        }

        return hasValidBundleProducts;
    }

    @Override
    @XmlAttribute
    public List<BundleProductItem> getBundleProductItems() {
        return bundleProductItems;
    }

    @Override
    public Product setBundleProductItems(List<BundleProductItem> bundleProductItems) {
        this.bundleProductItems = bundleProductItems;
        this.bundleProducts = null;
        return this;
    }

    @JsonIgnore
    @Override
    public List<Product> getBundleProducts() {
        if (bundleProductItems != null && bundleProductItems.size() > 0) {
            List<Id> bundleProductIds = new ArrayList<>();

            bundleProducts = products.findByIds(Product.class,  bundleProductIds.toArray(new Id[bundleProductIds.size()]));
        }

        return bundleProducts;
    }
    @Override
    public Product addBundleProduct(Product product) {
        return addBundleProduct(product, 1);
    }

    @Override
    public Product addBundleProduct(Product product, int quantity) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or product.id cannot be null");

        if (getId().equals(product.getId()))
            return this;

        if (bundleProductItems == null)
            bundleProductItems = new ArrayList<>();

        BundleProductItem bundleProductItem = bundleProductItems.stream().filter(bundleProductItem1 -> bundleProductItem1.getProductId()
                .equals(product.getId())).findFirst().orElse(null);

        if(bundleProductItem == null){
            bundleProductItem = app.model(BundleProductItem.class);
            bundleProductItem.setProductId(product.getId());
            bundleProductItem.setQuantity(quantity);
            bundleProductItems.add(bundleProductItem);
        } else {
            bundleProductItem.setQuantity(quantity);
        }

        // reset lazy-loaded list.
        bundleProducts = null;

        return this;
    }

    @Override
    public Product removeBundleProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or its id cannot be null");

        if (bundleProductItems != null){

            BundleProductItem bundleProductItem = bundleProductItems.stream().filter(bundleProductItem1 -> bundleProductItem1.getProductId()
                    .equals(product.getId())).findFirst().orElse(null);
            if(bundleProductItem != null){
                bundleProductItems.remove(bundleProductItem);
            }
        }

        // reset lazy-loaded list.
        bundleProducts = null;

        return this;
    }

    @JsonIgnore
    @Override
    public boolean isProgramme() {
        return type != null && type == ProductType.PROGRAMME;
    }

    @JsonIgnore
    @Override
    public boolean isProgrammeChild() {
        if (!isProgramme()) {
            return getProgrammeParent() != null;
        } else {
            return false;
        }
    }

    @JsonIgnore
    @Override
    public Product getProgrammeParent() {
        if (!isProgramme() && programmeParents == null) {
            programmeParents = products.havingProgrammeChildProduct(this);
        }

        return programmeParents == null || programmeParents.isEmpty() ? null : programmeParents.get(0);
    }

    @JsonIgnore
    @Override
    public boolean hasProgrammeProducts() {
        return programmeProductIds != null && programmeProductIds.size() > 0;
    }

    @JsonIgnore
    @Override
    public boolean hasValidProgrammeProducts() {
        if (!hasProgrammeProducts())
            return false;

        boolean hasValidProgrammeProducts = false;

        List<Product> programmeProducts = getProgrammeProducts();

        for (Product p : programmeProducts) {
            if (p == null || getId().equals(p.getId()))
                continue;

            if (p.isVisible()) {
                hasValidProgrammeProducts = true;
                break;
            }
        }

        return hasValidProgrammeProducts;
    }

    @JsonIgnore
    @Override
    public boolean hasProgrammeProductsValidForSelling() {
        if (!isProgramme() || !hasProgrammeProducts())
            return false;

        boolean hasProgrammeProductsValidForSelling = false;

        List<Product> programmeProducts = getProgrammeProducts();

        for (Product p : programmeProducts) {
            if (p == null || getId().equals(p.getId()))
                continue;

            if (!p.isVisible())
                continue;

            if ((p.isVariantMaster() && p.hasVariantsValidForSelling()) || p.isValidForSelling()) {
                hasProgrammeProductsValidForSelling = true;
                break;
            }
        }

        return hasProgrammeProductsValidForSelling;
    }

    @Override
    @XmlAttribute
    public List<Id> getProgrammeProductIds() {
        return programmeProductIds;
    }

    @JsonIgnore
    @Override
    public List<Product> getProgrammeProducts() {
        if (programmeProductIds != null && programmeProductIds.size() > 0) {
            programmeProducts = products.findByIds(Product.class,
                programmeProductIds.toArray(new Id[programmeProductIds.size()]));
        }

        return programmeProducts;
    }

    @JsonIgnore
    @Override
    public List<Product> getValidProgrammeProducts() {
        List<Product> programmeProducts = getProgrammeProducts();
        List<Product> validProgrammeProducts = null;

        if (programmeProducts != null) {
            validProgrammeProducts = new ArrayList<>();

            for (Product p : programmeProducts) {
                if (p == null || getId().equals(p.getId()))
                    continue;

                if (p.isVisible()) {
                    validProgrammeProducts.add(p);
                }
            }
        }

        return validProgrammeProducts;
    }

    @Override
    public Product addProgrammeProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or product.id cannot be null");

        if (getId().equals(product.getId()))
            return this;

        if (programmeProductIds == null)
            programmeProductIds = new ArrayList<>();

        if (!programmeProductIds.contains(product.getId())) {
            programmeProductIds.add(product.getId());
        }

        // reset lazy-loaded list.
        programmeProducts = null;

        return this;
    }

    @Override
    public Product addProgrammeProductIds(Id... programmeProductIds) {
        if (this.programmeProductIds == null) {
            this.programmeProductIds = new ArrayList<>();
        }

        List<Id> productIds = Arrays.asList(programmeProductIds);

        if (productIds.contains(getId())) {
            productIds.remove(getId());
        }

        this.programmeProductIds.addAll(Arrays.asList(programmeProductIds));

        return this;
    }

    @Override
    public Product setProgrammeProductIds(List<Id> programmeProductIds) {
        if (programmeProductIds != null && programmeProductIds.contains(getId())) {
            programmeProductIds.remove(getId());
        }

        this.programmeProductIds = programmeProductIds;
        return this;
    }

    @Override
    public Product removeProgrammeProduct(Product product) {
        if (product == null || product.getId() == null)
            throw new NullPointerException("Product or its id cannot be null");

        if (programmeProductIds != null && programmeProductIds.contains(product.getId())) {
            programmeProductIds.remove(product.getId());
        }

        // reset lazy-loaded list.
        programmeProducts = null;

        return this;
    }

    @Override
    @XmlAttribute
    public Map<String, List<Id>> getProductLinks() {
        return productLinks;
    }

    @Override
    public Product setProductLinks(Map<String, List<Id>> productLinks) {
        this.productLinks = productLinks;
        return this;
    }

    @Override
    public Product addProductLinks(Map<String, List<Id>> productLinks) {
        if (this.productLinks == null) {
            this.productLinks = new HashMap<>();
            this.productLinks.putAll(productLinks);
        } else {
            fillProductLinks(productLinks);
        }
        return this;
    }

    private void fillProductLinks(Map<String, List<Id>> productLinks) {
        Set<String> existedKeys = this.productLinks.keySet();
        Set<String> newKeys = productLinks.keySet();
        for (String existedKey : existedKeys) {
            for (String newKey : newKeys) {
                if (existedKey.equals(newKey)) {
                    for (Id newId : productLinks.get(existedKey)) {
                        if (!this.productLinks.get(existedKey).contains(newId)) {
                            this.productLinks.get(existedKey).add(newId);
                        }
                    }
                    newKeys.remove(newKey);
                }
            }
        }
        for (String newKey : newKeys) {
            this.productLinks.put(newKey, productLinks.get(newKey));
        }
    }

    @Override
    @XmlAttribute
    public List<Id> getAssets() {
        return assets;
    }

    @Override
    public Product setAssets(List<Id> assets) {
        this.assets = assets;
        return this;
    }

    @Override
    public Product addAsset(Id asset) {
        if (assets == null)
            assets = new ArrayList<>();
        assets.add(asset);
        return this;
    }

    @JsonIgnore
    @Override
    public ContextObject<String> getLabel() {
        if (hasAttribute("name") || hasAttribute("name2")) {
            ApplicationContext appCtx = app.context();

            AttributeValue nameAV = attr("name");
            AttributeValue name2AV = attr("name2");

            String name = nameAV == null ? "" : nameAV.getString();
            String name2 = name2AV == null ? "" : name2AV.getString();

            ContextObject<String> newCtxObj = new ContextObject<>();
            newCtxObj.add(appCtx.getLanguage(), (name2 + " " + name).trim());

            return newCtxObj;
        }

        return null;
    }

    @Override
    public Set<Id> getAllChildProductIds() {
        return getAllChildProductIds(true);
    }

    @Override
    public Set<Id> getAllChildProductIds(boolean useIndex) {
        ProductConnectionIndex pci = productConnectionIndexes.forProduct(this);

        if (useIndex && pci != null && pci.getChildConnections() != null && !pci.getChildConnections().isEmpty()) {
            return pci.getChildConnections();
        } else {
            HashSet<Id> productIds = new HashSet<>();

            if (variantProductIds != null && variantProductIds.size() > 0) {
                productIds.addAll(variantProductIds);
            } else if (isProgramme() && programmeProductIds != null && programmeProductIds.size() > 0) {
                List<Map<String, Object>> programmeProducts = products.findDataByIds(Product.class,
                    programmeProductIds.toArray(new Id[programmeProductIds.size()]),
                    QueryOptions.builder().fetchFields(Col.ID, Col.TYPE, Col.VARIANTS).build());

                for (Map<String, Object> data : programmeProducts) {
                    ProductType type = enum_(ProductType.class, data.get(Col.TYPE));
                    Id productId = id_(data.get(Col.ID));

                    if (getId().equals(productId))
                        continue;

                    if (type == ProductType.VARIANT_MASTER) {
                        List<Id> variantProductIds = idList_(data.get(Col.VARIANTS));

                        if (variantProductIds != null && variantProductIds.size() > 0)
                            productIds.addAll(variantProductIds);
                    } else {
                        productIds.add(productId);
                    }
                }
            }

            return productIds;
        }
    }

    @Override
    public Set<Id> getSellableChildProductIds() {
        return getSellableChildProductIds(true);
    }

    @Override
    public Set<Id> getSellableChildProductIds(boolean useIndex) {
        ProductConnectionIndex pci = productConnectionIndexes.forProduct(this);

        if (useIndex && pci != null && pci.getChildConnections() != null && !pci.getChildConnections().isEmpty()) {
            return pci.getSellableChildConnections();
        } else {
            HashSet<Id> productIds = new HashSet<>();

            if (variantProductIds != null && variantProductIds.size() > 0) {
                productIds.addAll(variantProductIds);
            } else if (isProgramme() && programmeProductIds != null && programmeProductIds.size() > 0) {
                List<Map<String, Object>> programmeProducts = products.findDataByIds(Product.class,
                    programmeProductIds.toArray(new Id[programmeProductIds.size()]),
                    QueryOptions.builder().fetchFields(Col.ID, Col.TYPE, Col.VARIANTS).build());

                for (Map<String, Object> data : programmeProducts) {
                    ProductType type = enum_(ProductType.class, data.get(Col.TYPE));
                    Id productId = id_(data.get(Col.ID));

                    if (getId().equals(productId))
                        continue;

                    if (type == ProductType.VARIANT_MASTER) {
                        List<Id> variantProductIds = idList_(data.get(Col.VARIANTS));

                        if (variantProductIds != null && variantProductIds.size() > 0)
                            productIds.addAll(variantProductIds);
                    } else {
                        productIds.add(productId);
                    }
                }
            }

            if (!productIds.isEmpty()) {
                HashSet<Id> sellableProductIds = new HashSet<>();

                List<Product> childProducts = products.findByIds(Product.class,
                    productIds.toArray(new Id[productIds.size()]));

                for (Product p : childProducts) {
                    if (p.isValidForSelling())
                        sellableProductIds.add(p.getId());
                }

                productIds = sellableProductIds;
            }

            return productIds;
        }
    }

    @Override
    public Set<Id> getAllConnectedProductIds() {
        return getAllConnectedProductIds(true);
    }

    @Override
    public Set<Id> getAllConnectedProductIds(boolean useIndex) {
        ProductConnectionIndex pci = productConnectionIndexes.forProduct(this);

        if (useIndex && pci != null && pci.getConnections() != null && !pci.getConnections().isEmpty()) {
            return pci.getConnections();
        } else {
            HashSet<Id> productIds = new HashSet<>();

            if (variantProductIds != null && variantProductIds.size() > 0) {
                productIds.addAll(variantProductIds);
            } else if (isProgramme() && programmeProductIds != null && programmeProductIds.size() > 0) {
                List<Map<String, Object>> programmeProducts = products.findDataByIds(Product.class,
                    programmeProductIds.toArray(new Id[programmeProductIds.size()]),
                    QueryOptions.builder().fetchFields(Col.ID, Col.TYPE, Col.VARIANTS).build());

                for (Map<String, Object> data : programmeProducts) {
                    ProductType type = enum_(ProductType.class, data.get(Col.TYPE));
                    Id productId = id_(data.get(Col.ID));

                    if (getId().equals(productId))
                        continue;

                    if (type == ProductType.VARIANT_MASTER) {
                        List<Id> variantProductIds = idList_(data.get(Col.VARIANTS));

                        if (variantProductIds != null && variantProductIds.size() > 0)
                            productIds.addAll(variantProductIds);
                    } else {
                        productIds.add(id_(data.get(Col.ID)));
                    }
                }
            } else if (isBundle() && bundleProductItems != null && bundleProductItems.size() > 0) {
                List<Map<String, Object>> bundleProducts = products.findDataByIds(Product.class,
                        bundleProductItems.stream().map(bundleProductItem -> bundleProductItem.getProductId()).collect(Collectors.toList())
                                .toArray(new Id[bundleProductItems.size()]), QueryOptions.builder().fetchFields(Col.ID, Col.TYPE, Col.VARIANTS).build());

                for (Map<String, Object> data : bundleProducts) {
                    ProductType type = enum_(ProductType.class, data.get(Col.TYPE));
                    Id productId = id_(data.get(Col.ID));

                    if (getId().equals(productId))
                        continue;

                    if (type == ProductType.VARIANT_MASTER) {
                        List<Id> variantProductIds = idList_(data.get(Col.VARIANTS));

                        if (variantProductIds != null && variantProductIds.size() > 0)
                            productIds.addAll(variantProductIds);
                    } else {
                        productIds.add(id_(data.get(Col.ID)));
                    }
                }
            }

            if (upsellProductIds != null && upsellProductIds.size() > 0) {
                List<Product> upsells = getUpsells();

                for (Product product : upsells) {
                    productIds.addAll(product.getAllChildProductIds());
                }
            }

            if (crossSellProductIds != null && crossSellProductIds.size() > 0) {
                List<Product> crossSells = getCrossSells();

                for (Product product : crossSells) {
                    productIds.addAll(product.getAllChildProductIds());
                }
            }

            return productIds;
        }
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.id2 = str_(map.get(Col.ID2));
        this.ean = long_(map.get(Col.EAN));

        if (map.get(Col.TYPE) != null)
            this.type = enum_(ProductType.class, map.get(Col.TYPE));

        if (map.get(Col.SALEABLE) != null)
            this.saleable = ctxObj_(map.get(Col.SALEABLE));
        else {
            this.saleable = new ContextObject<>();
            this.saleable.addGlobal(true);
        }

        if (map.get(Col.VISIBLE) != null)
            this.visible = ctxObj_(map.get(Col.VISIBLE));

        if (map.get(Col.VISIBLE_FROM) != null)
            this.visibleFrom = ctxObj_(map.get(Col.VISIBLE_FROM));

        if (map.get(Col.VISIBLE_TO) != null)
            this.visibleTo = ctxObj_(map.get(Col.VISIBLE_TO));

        if (map.get(Col.VISIBLE_IN_PRODUCT_LIST) != null)
            this.visibleInProductList = ctxObj_(map.get(Col.VISIBLE_IN_PRODUCT_LIST));

        if (map.get(Col.DELETED) != null)
            this.deleted = bool_(map.get(Col.DELETED));

        if (map.get(Col.BUNDLE_AS_SINGLE_PRODUCT) != null)
            this.bundleAsSingleProduct = bool_(map.get(Col.BUNDLE_AS_SINGLE_PRODUCT));

        if (map.get(Col.DELETED_NOTE) != null)
            this.deletedNote = str_(map.get(Col.DELETED_NOTE));

        if (map.get(Col.PARENT_ID) != null)
            this.parentId = id_(map.get(Col.PARENT_ID));

        if (map.get(Col.INCLUDE_IN_FEEDS) != null)
            this.includeInFeeds = ctxObj_(map.get(Col.INCLUDE_IN_FEEDS));

        this.variantProductIds = idList_(map.get(Col.VARIANTS));
        this.upsellProductIds = idList_(map.get(Col.UPSELL_PRODUCTS));
        this.crossSellProductIds = idList_(map.get(Col.CROSS_SELL_PRODUCTS));

        List<Map<String, Object>> items = list_(map.get(Col.BUNDLE_PRODUCTS));
        if (items != null && items.size() > 0) {
            this.bundleProductItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                BundleProductItem bundleProductItem = app.model(BundleProductItem.class);
                bundleProductItem.fromMap(item);
                this.bundleProductItems.add(bundleProductItem);
            }
        }

        this.programmeProductIds = idList_(map.get(Col.PROGRAMME_PRODUCTS));
        this.productLinks = mapIdList_(map.get(Col.PRODUCT_LINKS));
        this.assets = idList_(map.get(Col.ASSETS));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());

        if (getId2() != null)
            map.put(Col.ID2, getId2());

        if (getEan() != null)
            map.put(Col.EAN, getEan());

        map.put(Col.TYPE, getType().toId());

        if (getSaleable() != null)
            map.put(Col.SALEABLE, getSaleable());

        if (getVisible() != null)
            map.put(Col.VISIBLE, getVisible());

        if (getVisibleFrom() != null)
            map.put(Col.VISIBLE_FROM, getVisibleFrom());

        if (getVisibleTo() != null)
            map.put(Col.VISIBLE_TO, getVisibleTo());

        if (getVisibleInProductList() != null)
            map.put(Col.VISIBLE_IN_PRODUCT_LIST, getVisibleInProductList());

        if (getIncludeInFeeds() != null)
            map.put(Col.INCLUDE_IN_FEEDS, getIncludeInFeeds());

        if (getDeleted() != null)
            map.put(Col.DELETED, getDeleted());

        if (getBundleAsSingleProduct() != null)
            map.put(Col.BUNDLE_AS_SINGLE_PRODUCT, getBundleAsSingleProduct());

        if (getDeletedNote() != null)
            map.put(Col.DELETED_NOTE, getDeletedNote());

        map.put(Col.PARENT_ID, getParentId());

        if (getVariantProductIds() != null && getVariantProductIds().size() > 0)
            map.put(Col.VARIANTS, getVariantProductIds());

        if (getCrossSellProductIds() != null && getCrossSellProductIds().size() > 0)
            map.put(Col.CROSS_SELL_PRODUCTS, getCrossSellProductIds());

        if (getUpsellProductIds() != null && getUpsellProductIds().size() > 0)
            map.put(Col.UPSELL_PRODUCTS, getUpsellProductIds());

        if (getBundleProductItems() != null && getBundleProductItems().size() > 0){
            List<Map<String, Object>> items = new ArrayList<>();
            for (BundleProductItem bundleProductItem : bundleProductItems) {
                items.add(bundleProductItem.toMap());
            }
            map.put(Col.BUNDLE_PRODUCTS, items);
        } else {
            map.put(Col.BUNDLE_PRODUCTS, null);
        }

        if (getProgrammeProductIds() != null && getProgrammeProductIds().size() > 0)
            map.put(Col.PROGRAMME_PRODUCTS, getProgrammeProductIds());

        if (getAssets() != null && getAssets().size() > 0)
            map.put(Col.ASSETS, getAssets());

        // ----------------------------------------------------------------------------------------
        // Product links
        // ----------------------------------------------------------------------------------------

        if (getProductLinks() != null && getProductLinks().size() > 0)
            map.put(Col.PRODUCT_LINKS, getProductLinks());

        return map;
    }

    // --------------------------------------------------------------------------------
    // Page support methods
    // --------------------------------------------------------------------------------

    @Override
    public ContextObject<String> getTitle() {
        AttributeValue metaTitle = getAttribute("meta_title");
        ContextObject<String> title = getLabel();

        if (metaTitle != null && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(metaTitle.getValue()))) {
            return metaTitle.getValue();
        } else if (title != null && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(title))) {
            return title;
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getMetaDescription() {
        AttributeValue metaDescription = getAttribute("meta_description");
        AttributeValue description = getAttribute("description");

        if (metaDescription != null
            && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(metaDescription.getValue()))) {
            return metaDescription.getValue();
        } else if (description != null
            && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(description.getValue()))) {
            return description.getValue();
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getMetaRobots() {
        AttributeValue metaRobots = getAttribute("meta_robots");

        if (metaRobots != null && metaRobots.getFirstAttributeOption() != null) {
            return metaRobots.getFirstAttributeOption().getLabel();
        } else if (getCanonicalURI() != null) {
            return ContextObjects.global("noindex,follow");
        } else {
            return null;
        }
    }

    @JsonIgnore
    @Override
    public ContextObject<String> getCanonicalURI() {
        if (isVariant()) {
            ContextObject<String> canonicalURI = null;

            UrlRewrite urlRewrite = urlRewrites.forProduct(getParentId());

            if (urlRewrite != null)
                canonicalURI = urlRewrite.getRequestURI();

            if (canonicalURI != null)
                return canonicalURI;
        } else {
            UrlRewrite urlRewrite = urlRewrites.forProduct(getId());

            if (urlRewrite != null) {
                String rewriteURI = ContextObjects.findCurrentLanguageOrGlobal(urlRewrite.getRequestURI());
                String requestURI = app.getOriginalURI();

                if (rewriteURI != null && !requestURI.equals(rewriteURI)) {
                    return urlRewrite.getRequestURI();
                }
            }
        }

        return null;
    }

    // --------------------------------------------------------------------------------
    // Data support methods
    // --------------------------------------------------------------------------------

    @Override
    public Object getData(String key) {
        return data == null ? null : data.get(key);
    }

    @Override
    public void putData(String key, Object value) {
        if (data == null)
            data = new HashMap<>();

        data.put(key, value);
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getIndexMap() {
        return elasticsearchProductHelper.buildJsonProduct(getId().toString(), this);
    }

    @Override
    public Product makeCopy() {
        DefaultProduct p = new DefaultProduct();

        p.id2 = id2;
        p.ean = ean;
        p.type = type;
        p.visibleInProductList = visibleInProductList;
        p.parentId = parentId;
        p.includeInFeeds = includeInFeeds;
        p.bundleAsSingleProduct = bundleAsSingleProduct;
        p.attributes = copyOfAttributes();

        // Needs to be set manually by product admin.
        p.removeAttribute("status_article");

        if (merchantIds != null)
            p.merchantIds = merchantIds;

        if (requestContextIds != null)
            p.requestContextIds = requestContextIds;

        if (storeIds != null)
            p.storeIds = storeIds;

        if (variantProductIds != null)
            p.variantProductIds = new ArrayList<>(variantProductIds);

        if (upsellProductIds != null)
            p.upsellProductIds = new ArrayList<>(upsellProductIds);

        if (crossSellProductIds != null)
            p.crossSellProductIds = new ArrayList<>(crossSellProductIds);

        if (bundleProductItems != null)
            p.bundleProductItems = new ArrayList<>(bundleProductItems);

        if (programmeProductIds != null)
            p.programmeProductIds = new ArrayList<>(programmeProductIds);

        if (assets != null)
            p.assets = new ArrayList<>(assets);

        return p;
    }
}
