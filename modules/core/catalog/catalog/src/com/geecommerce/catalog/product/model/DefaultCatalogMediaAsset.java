package com.geecommerce.catalog.product.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.geecommerce.catalog.product.MediaType;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.repository.CatalogMedia;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("catalog_media_assets")
public class DefaultCatalogMediaAsset extends AbstractMultiContextModel implements CatalogMediaAsset {
    private static final long serialVersionUID = -8640252151408188166L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.PRODUCT_ID)
    private Id productId = null;

    @Column(Col.PRODUCT_LIST_ID)
    private Id productListId = null;

    @Column(Col.MEDIA_TYPE_IDS)
    private Set<Id> mediaTypeIds = null;

    @Column(Col.PATH)
    private String path = null;

    @Column(Col.OLD_PATH)
    private String oldPath = null;

    @Column(Col.PREVIEW_IMAGE_PATH)
    private String previewImagePath = null;

    @Column(Col.MIME_TYPE)
    private String mimeType = null;

    @Column(Col.TITLE)
    private ContextObject<String> title = null;

    @Column(Col.WIDTH)
    private int width = 0;

    @Column(Col.HEIGHT)
    private int height = 0;

    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.VARIANT_MASTER_DEFAULT)
    private boolean variantMasterDefault = false;

    @Column(Col.BUNDLE_DEFAULT)
    private boolean bundleDefault = false;

    @Column(Col.PROGRAMME_DEFAULT)
    private boolean programmeDefault = false;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    // Loaded lazily from repository

    private Set<CatalogMediaType> mediaTypes = null;
    private Product product = null;
    private String webPath = null;
    private String webPreviewImagePath = null;
    private String systemPath = null;
    private String previewImageWebPath = null;
    private String previewImageSystemPath = null;

    // Repositories & Helper

    private CatalogMedia catalogMedia = null;
    private CatalogMediaHelper catalogMediaHelper = null;
    private Products products = null;

    protected static final String DEAULT_IMAGE_LIST_SIZE = "250x250";
    protected static final String DEAULT_IMAGE_DETAIL_SIZE = "330x330";
    protected static final String DEAULT_IMAGE_THUMBNAIL_SIZE = "60x60";
    protected static final String DEAULT_IMAGE_ZOOM_SIZE = "1024x1024";

    public DefaultCatalogMediaAsset() {
        this(i(CatalogMedia.class), i(CatalogMediaHelper.class), i(Products.class));
    }

    @Inject
    public DefaultCatalogMediaAsset(CatalogMedia catalogMedia, CatalogMediaHelper catalogMediaHelper,
        Products products) {
        this.catalogMedia = catalogMedia;
        this.catalogMediaHelper = catalogMediaHelper;
        this.products = products;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CatalogMediaAsset setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public CatalogMediaAsset setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @JsonIgnore
    @Override
    public Product getProduct() {
        if (productId != null && product == null) {
            product = products.findById(Product.class, productId);
        }

        return product;
    }

    @Override
    public CatalogMediaAsset belongsTo(Product product) {
        if (product != null && product.getId() != null) {
            this.productId = product.getId();
            this.product = product;
        }

        return this;
    }

    @Override
    public Id getProductListId() {
        return productListId;
    }

    @Override
    public CatalogMediaAsset setProductListId(Id productListId) {
        this.productListId = productListId;
        return this;
    }

    @Override
    public Set<Id> getMediaTypeIds() {
        return mediaTypeIds;
    }

    @Override
    public CatalogMediaAsset setMediaTypeIds(Set<Id> mediaTypeIds) {
        this.mediaTypeIds = mediaTypeIds;
        return this;
    }

    @Override
    public Set<CatalogMediaType> getMediaTypes() {
        if (mediaTypes == null && mediaTypeIds != null && mediaTypeIds.size() > 0) {
            mediaTypes = new LinkedHashSet<>();
            mediaTypes.addAll(
                catalogMedia.findByIds(CatalogMediaType.class, mediaTypeIds.toArray(new Id[mediaTypeIds.size()]),
                    QueryOptions.builder().sortBy(CatalogMediaType.Col.PRIORITY).build()));
        }

        return mediaTypes;
    }

    @Override
    public CatalogMediaAsset addMediaType(String mediaTypeKey) {
        if (mediaTypeKey == null)
            return this;

        CatalogMediaType mediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class, CatalogMediaType.Col.KEY,
            mediaTypeKey);

        if (mediaType != null) {
            getMediaTypes();

            if (mediaTypes == null) {
                mediaTypes = new LinkedHashSet<>();
                mediaTypeIds = new LinkedHashSet<>();
            }

            if (mediaType.getId() != null && !mediaTypeIds.contains(mediaType.getId())) {
                mediaTypeIds.add(mediaType.getId());
                mediaTypes.add(mediaType);
            }
        }

        return this;
    }

    @Override
    public CatalogMediaAsset removeMediaType(String mediaTypeKey) {
        if (mediaTypeKey == null)
            return this;

        CatalogMediaType mediaType = catalogMedia.findByUniqueKey(CatalogMediaType.class, CatalogMediaType.Col.KEY,
            mediaTypeKey);

        if (mediaType != null) {
            if (mediaTypes != null)
                mediaTypes.clear();

            if (mediaType != null && mediaType.getId() != null)
                mediaTypeIds.remove(mediaType.getId());
        }

        return this;
    }

    @Override
    public String getPath() {
        return path;
    }

    // origImage: _mainImage.path,
    // listImage: mediaUtil.buildImageURL(_mainImage.path, 250, 250),
    // largeImage: mediaUtil.buildImageURL(_mainImage.path, 330, 330),
    // thumbnail: mediaUtil.buildImageURL(_mainImage.path, 60, 60),
    // zoomImage: mediaUtil.buildImageURL(_mainImage.path, 1024, 1024),

    @Override
    public String getWebPath() {
        if (webPath == null) {
            String subDomain = catalogMediaHelper.getSubdomain(getMimeType(), getFirstStoreId());
            String servletPath = catalogMediaHelper.getServletPath(getMimeType(), getFirstStoreId());

            if (app.isExternalHost(subDomain)) {
                webPath = new StringBuilder(Str.SLASH_2X).append(subDomain).append(servletPath).append(Char.SLASH)
                    .append(getPath()).toString();
            } else {
                webPath = new StringBuilder(servletPath).append(Char.SLASH).append(getPath()).toString();
            }
        }

        return webPath;
    }

    @Override
    public String getWebListPath() {
        String size = catalogMediaHelper.getSize("list", getMimeType(), getFirstStoreId());
        return getWebPath(size == null ? DEAULT_IMAGE_LIST_SIZE : size);
    }

    @Override
    public String getWebDetailPath() {
        String size = catalogMediaHelper.getSize("detail", getMimeType(), getFirstStoreId());
        return getWebPath(size == null ? DEAULT_IMAGE_DETAIL_SIZE : size);
    }

    @Override
    public String getWebThumbnailPath() {
        String size = catalogMediaHelper.getSize("thumbnail", getMimeType(), getFirstStoreId());
        return getWebPath(size == null ? DEAULT_IMAGE_THUMBNAIL_SIZE : size);
    }

    @Override
    public String getWebZoomPath() {
        String size = catalogMediaHelper.getSize("zoom", getMimeType(), getFirstStoreId());
        return getWebPath(size == null ? DEAULT_IMAGE_ZOOM_SIZE : size);
    }

    @Override
    public String getWebPath(String size) {
        int pos = size.indexOf('x');

        Integer width = null;
        Integer height = null;

        if (pos == -1)
            width = Integer.valueOf(size);

        if (size.startsWith("x")) {
            height = Integer.valueOf(size.substring(1));
        } else if (size.endsWith("x")) {
            width = Integer.valueOf(size.substring(0, pos));
        } else {
            width = Integer.valueOf(size.substring(0, pos));
            height = Integer.valueOf(size.substring(pos + 1));
        }

        return getWebPath(width, height);
    }

    @Override
    public String getWebPath(Integer width, Integer height) {
        String webPath = getWebPath();

        if (webPath != null && mimeType != null && mimeType.startsWith("image/")) {
            int pos = webPath.lastIndexOf(Char.DOT);
            StringBuilder sb = new StringBuilder();
            sb.append(webPath.substring(0, pos));
            sb.append("___s:" + (width == null ? Str.EMPTY : width) + "x" + (height == null ? Str.EMPTY : height));
            sb.append(webPath.substring(pos));
            return sb.toString();
        }

        return webPath;
    }

    @Override
    public String getWebPreviewImagePath() {
        if (webPreviewImagePath == null) {
            String servletPath = catalogMediaHelper.getServletPath(getMimeType(), getFirstStoreId());
            webPreviewImagePath = new StringBuilder(servletPath).append("/").append(getPreviewImagePath()).toString();
        }

        return webPreviewImagePath;
    }

    @JsonIgnore
    @Override
    public String getSystemPath() {
        if (systemPath == null)
            systemPath = catalogMediaHelper.toAbsoluteSystemPath(getPath(), getFirstStoreId());

        return systemPath;
    }

    @Override
    public CatalogMediaAsset setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String getOldPath() {
        return oldPath;
    }

    @Override
    public CatalogMediaAsset setOldPath(String oldPath) {
        this.oldPath = oldPath;
        return this;
    }

    @Override
    public String getPreviewImagePath() {
        return previewImagePath;
    }

    @Override
    public String getPreviewImageWebPath() {
        if (previewImageWebPath == null && previewImagePath != null) {
            String servletPath = catalogMediaHelper.getServletPath(MimeType.fromFilename(getPreviewImagePath()),
                getFirstStoreId());
            previewImageWebPath = new StringBuilder(servletPath).append("/").append(getPreviewImagePath()).toString();
        }

        return previewImageWebPath;
    }

    @Override
    public String getPreviewImageWebThumbnailPath() {
        String webPath = getPreviewImageWebPath();

        if (webPath != null) {
            int pos = webPath.lastIndexOf('.');
            StringBuilder sb = new StringBuilder();
            sb.append(webPath.substring(0, pos));
            sb.append("___s:50x50");
            sb.append(webPath.substring(pos));

            return sb.toString();
        }

        return webPath;
    }

    @JsonIgnore
    @Override
    public String getPreviewImageSystemPath() {
        if (previewImageSystemPath == null)
            previewImageSystemPath = catalogMediaHelper.toAbsoluteSystemPath(getPreviewImagePath(), getFirstStoreId());

        return previewImageSystemPath;
    }

    @Override
    public CatalogMediaAsset setPreviewImagePath(String previewImagePath) {
        this.previewImagePath = previewImagePath;
        return this;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public CatalogMediaAsset setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    @Override
    public ContextObject<String> getTitle() {
        return title;
    }

    @Override
    public CatalogMediaAsset setTitle(ContextObject<String> title) {
        this.title = title;
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public CatalogMediaAsset setWidth(int width) {
        this.width = width;
        return this;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public CatalogMediaAsset setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public CatalogMediaAsset setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public boolean isVariantMasterDefault() {
        return variantMasterDefault;
    }

    @Override
    public CatalogMediaAsset setVariantMasterDefault(boolean variantMasterDefault) {
        this.variantMasterDefault = variantMasterDefault;
        return this;
    }

    @Override
    public boolean isBundleDefault() {
        return bundleDefault;
    }

    @Override
    public CatalogMediaAsset setBundleDefault(boolean bundleDefault) {
        this.bundleDefault = bundleDefault;
        return this;
    }

    @Override
    public boolean isProgrammeDefault() {
        return programmeDefault;
    }

    @Override
    public CatalogMediaAsset setProgrammeDefault(boolean programmeDefault) {
        this.programmeDefault = programmeDefault;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public CatalogMediaAsset setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isProductImage() {
        return productId != null
            && (isProductMainImage() || isProductListImage1() || isProductListImage2() || isProductGalleryImage());
    }

    @Override
    public boolean isProductVideo() {
        return productId != null && containsMediaType(MediaType.PRODUCT_GALLERY_VIDEO);
    }

    @Override
    public boolean isProductMainImage() {
        return containsMediaType(MediaType.PRODUCT_MAIN_IMAGE);
    }

    @Override
    public boolean isProductListImage1() {
        return containsMediaType(MediaType.PRODUCT_LIST_IMAGE1);
    }

    @Override
    public boolean isProductListImage2() {
        return containsMediaType(MediaType.PRODUCT_LIST_IMAGE2);
    }

    @Override
    public boolean isProductGalleryImage() {
        return containsMediaType(MediaType.PRODUCT_GALLERY_IMAGE);
    }

    @Override
    public boolean containsMediaType(String mediaTypeKey) {
        Set<CatalogMediaType> mediaTypes = getMediaTypes();

        if (mediaTypeKey == null || mediaTypes == null || mediaTypes.size() == 0)
            return false;

        for (CatalogMediaType catalogMediaType : mediaTypes) {
            if (mediaTypeKey.equals(catalogMediaType.getKey()))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "DefaultCatalogMediaAsset [id=" + id + ", productId=" + productId + ", productListId=" + productListId
            + ", mediaTypeIds=" + mediaTypeIds + ", path=" + path + ", oldPath=" + oldPath + ", previewImagePath="
            + previewImagePath + ", mimeType=" + mimeType + ", title=" + title + ", width=" + width + ", height="
            + height + ", position=" + position + ", variantMasterDefault=" + variantMasterDefault
            + ", bundleDefault=" + bundleDefault + ", programmeDefault=" + programmeDefault + ", enabled=" + enabled
            + ", merchantIds=" + merchantIds + ", storeIds=" + storeIds + ", requestContextIds=" + requestContextIds
            + "]";
    }
}
