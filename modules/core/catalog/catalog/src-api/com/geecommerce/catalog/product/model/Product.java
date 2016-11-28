package com.geecommerce.catalog.product.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.ProductStatus;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.DataSupport;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.pojo.PriceResult;
import com.owlike.genson.annotation.JsonIgnore;

public interface Product extends AttributeSupport, TargetSupport, PageSupport, DataSupport, SearchIndexSupport {
    public Id getId();

    public Product setId(Id id);

    public String getId2();

    public Product setId2(String id2);

    public Long getEan();

    public Product setEan(Long ean);

    public ProductType getType();

    public Product setType(ProductType type);

    public ProductStatus getStatus();

    public Product setStatus(ProductStatus status);

    @JsonIgnore
    public boolean isEnabled();

    @JsonIgnore
    public boolean isValidForSelling();

    @JsonIgnore
    public boolean hasVariantsValidForSelling();

    @JsonIgnore
    public boolean hasValidPrice();

    @JsonIgnore
    public boolean isInStock();

    @JsonIgnore
    public boolean isInStock(int qty);

    @JsonIgnore
    public boolean hasSalableVariants();

    @JsonIgnore
    public boolean isSalable();

    public ContextObject<Boolean> getSaleable();

    public Product setSaleable(ContextObject<Boolean> saleable);

    @JsonIgnore
    public boolean isVisible();

    public ContextObject<Boolean> getVisible();

    public Product setVisible(ContextObject<Boolean> visible);

    public ContextObject<Date> getVisibleFrom();

    public Product setVisibleFrom(ContextObject<Date> visibleFrom);

    public ContextObject<Date> getVisibleTo();

    public Product setVisibleTo(ContextObject<Date> visibleTo);

    public ContextObject<Boolean> getVisibleInProductList();

    public Product setVisibleInProductList(ContextObject<Boolean> visibleInProductList);

    public ContextObject<Boolean> getIncludeInFeeds();

    public Product setIncludeInFeeds(ContextObject<Boolean> includeInFeeds);

    @JsonIgnore
    public boolean isSpecial();

    public ContextObject<Boolean> getSpecial();

    public Product setSpecial(ContextObject<Boolean> special);

    @JsonIgnore
    public boolean isSale();

    public ContextObject<Boolean> getSale();

    public Product setSale(ContextObject<Boolean> sale);

    public Date getLastSold();

    public Product setLastSold(Date lastSold);

    public boolean isDeleted();

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    public String getDeletedNote();

    public Product setDeletedNote(String deletedNote);

    @JsonIgnore
    public String getArticleNumber();

    @JsonIgnore
    public String getName();

    @JsonIgnore
    public String getName2();

    @JsonIgnore
    public String getDescription();

    @JsonIgnore
    public String getShortDescription();

    public Id getParentId();

    @JsonIgnore
    public Product getParent();

    @JsonIgnore
    public Product setParent(Product parent);

    public Product unsetParent();

    public boolean isVariant();

    public boolean isVariantMaster();

    public boolean isValidVariant();

    public boolean hasValidVariants();

    public List<Id> getVariantProductIds();

    public List<AttributeValue> getVariantAttributes();

    public Product addVariantAttribute(Attribute attribute, Id optionId);

    public Product addVariantAttribute(String attributeCode, Id optionId);

    @JsonIgnore
    public List<Product> getVariants();

    public Product addVariant(Product product);

    public Product removeVariant(Product variantProduct);

    public Product findMatchingVariant(Id... optionIds);

    public ContextObject<String> getURI();

    public Product setURI(ContextObject<String> uri);

    @JsonIgnore
    public PriceResult getPrice();

    @JsonIgnore
    public PriceResult getPriceFor(String currencyCode);

    @JsonIgnore
    public Integer getQty();

    @JsonIgnore
    public boolean isAllowBackorder();

    @JsonIgnore
    public Map<String, Object> getStockData();

    @JsonIgnore
    public List<CatalogMediaAsset> getImages();

    @JsonIgnore
    List<CatalogMediaAsset> getVideos();

    @JsonIgnore
    List<CatalogMediaAsset> getDocuments();

    public String getMainImageURI();

    public CatalogMediaAsset getMainImage();

    @JsonIgnore
    public List<Map<String, Object>> getAssemblyInstructions();

    @JsonIgnore
    public List<Map<String, Object>> getProductInstructions();

    @JsonIgnore
    public List<Map<String, Object>> getModelList();

    @JsonIgnore
    public List<Map<String, Object>> getImagesMaps();

    @JsonIgnore
    public List<Map<String, Object>> getSurfaceImagesMaps();

    @JsonIgnore
    public List<Map<String, Object>> getVideoImagesMaps();

    @JsonIgnore
    public String getCat1ImageURI();

    @JsonIgnore
    public String getCat2ImageURI();

    public List<Id> getUpsellProductIds();

    public Product addUpsellProduct(Product product);

    public Product addUpsellProductIds(Id... upSellingProductIds);

    public Product setUpsellProductIds(List<Id> upSellingProductIds);

    public Product removeUpsellProduct(Product product);

    @JsonIgnore
    public boolean hasUpsells();

    @JsonIgnore
    public boolean hasValidUpsells();

    @JsonIgnore
    public List<Product> getUpsells();

    @JsonIgnore
    public List<Product> getValidUpsells();

    public List<Id> getCrossSellProductIds();

    public Product addCrossSellProduct(Product product);

    public Product addCrossSellProductIds(Id... crossSellingProductIds);

    public Product setCrossSellProductIds(List<Id> crossSellingProductIds);

    public Product removeCrossSellProduct(Product product);

    @JsonIgnore
    public boolean hasCrossSells();

    @JsonIgnore
    public boolean hasValidCrossSells();

    @JsonIgnore
    public List<Product> getCrossSells();

    @JsonIgnore
    public boolean isBundle();

    @JsonIgnore
    public boolean hasBundleProducts();

    @JsonIgnore
    public boolean hasValidBundleProducts();

    public List<BundleProductItem> getBundleProductItems();

    public Product setBundleProductItems(List<BundleProductItem> bundleProductItems);

    public Product addBundleProduct(Product product);

    public Product addBundleProduct(Product product, int quantity);

    public Product removeBundleProduct(Product product);

    @JsonIgnore
    public List<Product> getBundleProducts();

    @JsonIgnore
    public boolean isProgramme();

    @JsonIgnore
    boolean isProgrammeChild();

    @JsonIgnore
    public Product getProgrammeParent();

    @JsonIgnore
    public boolean hasProgrammeProducts();

    @JsonIgnore
    public boolean hasValidProgrammeProducts();

    @JsonIgnore
    public boolean hasProgrammeProductsValidForSelling();

    public List<Id> getProgrammeProductIds();

    @JsonIgnore
    public List<Product> getProgrammeProducts();

    @JsonIgnore
    public List<Product> getValidProgrammeProducts();

    public Product addProgrammeProduct(Product product);

    public Product addProgrammeProductIds(Id... programmeProductIds);

    public Product setProgrammeProductIds(List<Id> programmeProductIds);

    public Product removeProgrammeProduct(Product product);

    public Map<String, List<Id>> getProductLinks();

    public Product setProductLinks(Map<String, List<Id>> productLinks);

    public Product addProductLinks(Map<String, List<Id>> productLinks);

    public List<Id> getAssets();

    public Product setAssets(List<Id> assets);

    public Product addAsset(Id asset);

    public Set<Id> getAllChildProductIds();

    public Set<Id> getAllChildProductIds(boolean useIndex);

    public Set<Id> getSellableChildProductIds();

    public Set<Id> getSellableChildProductIds(boolean useIndex);

    public Set<Id> getAllConnectedProductIds();

    public Set<Id> getAllConnectedProductIds(boolean useIndex);

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String EAN = "ean";
        public static final String TYPE = "type";
        public static final String GROUP = "group";
        public static final String STATUS = "status";
        public static final String SALEABLE = "saleable";
        public static final String VISIBLE = "visible";
        public static final String VISIBLE_FROM = "visible_from";
        public static final String VISIBLE_TO = "visible_to";
        public static final String VISIBLE_IN_PRODUCT_LIST = "pl_visible";
        public static final String INCLUDE_IN_FEEDS = "feeds_inc";
        public static final String SPECIAL = "special";
        public static final String SALE = "sale";
        public static final String SHOW_CART_BUTTON = "cart_btn";
        public static final String DELETED = "del";
        public static final String DELETED_NOTE = "del_note";
        public static final String LAST_SOLD = "last_sold";
        public static final String PARENT_ID = "parent_id";
        public static final String VARIANTS = "variants";
        public static final String UPSELL_PRODUCTS = "upsells";
        public static final String CROSS_SELL_PRODUCTS = "cross_sells";
        public static final String BUNDLE_PRODUCTS = "bundle_products";
        public static final String PROGRAMME_PRODUCTS = "prog_products";
        public static final String PRODUCT_LINKS = "prd_links";
        public static final String ASSETS = "assets";
    }
}
