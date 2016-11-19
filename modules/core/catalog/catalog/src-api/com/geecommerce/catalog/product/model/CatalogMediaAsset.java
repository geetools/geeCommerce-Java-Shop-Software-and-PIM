package com.geecommerce.catalog.product.model;

import java.util.Set;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface CatalogMediaAsset extends MultiContextModel, ProductIdObject {
    public Id getId();

    public CatalogMediaAsset setId(Id id);

    public Id getProductId();

    public CatalogMediaAsset setProductId(Id productId);

    @JsonIgnore
    public Product getProduct();

    public CatalogMediaAsset belongsTo(Product product);

    public Id getProductListId();

    public CatalogMediaAsset setProductListId(Id productListId);

    public Set<Id> getMediaTypeIds();

    public CatalogMediaAsset setMediaTypeIds(Set<Id> mediaTypeIds);

    public Set<CatalogMediaType> getMediaTypes();

    public CatalogMediaAsset addMediaType(String mediaTypeKey);

    public CatalogMediaAsset removeMediaType(String mediaTypeKey);

    public String getPath();

    public String getWebPath();

    public String getWebListPath();

    public String getWebDetailPath();

    public String getWebThumbnailPath();

    public String getWebZoomPath();

    public String getWebPath(String size);

    public String getWebPath(Integer width, Integer height);

    public String getWebPreviewImagePath();

    @JsonIgnore
    public String getSystemPath();

    public CatalogMediaAsset setPath(String path);

    public String getOldPath();

    public CatalogMediaAsset setOldPath(String oldPath);

    public String getPreviewImagePath();

    public String getPreviewImageWebPath();

    public String getPreviewImageWebThumbnailPath();

    public String getPreviewImageSystemPath();

    public CatalogMediaAsset setPreviewImagePath(String previewImagePath);

    public String getMimeType();

    public CatalogMediaAsset setMimeType(String mimeType);

    public ContextObject<String> getTitle();

    public CatalogMediaAsset setTitle(ContextObject<String> title);

    public int getWidth();

    public CatalogMediaAsset setWidth(int width);

    public int getHeight();

    public CatalogMediaAsset setHeight(int height);

    public int getPosition();

    public CatalogMediaAsset setPosition(int position);

    public boolean isVariantMasterDefault();

    public CatalogMediaAsset setVariantMasterDefault(boolean variantMasterDefault);

    public boolean isBundleDefault();

    public CatalogMediaAsset setBundleDefault(boolean bundleDefault);

    public boolean isProgrammeDefault();

    public CatalogMediaAsset setProgrammeDefault(boolean programmeDefault);

    public boolean isEnabled();

    public CatalogMediaAsset setEnabled(boolean enabled);

    public boolean isProductImage();

    public boolean isProductVideo();

    public boolean isProductMainImage();

    public boolean isProductListImage1();

    public boolean isProductListImage2();

    public boolean isProductGalleryImage();

    public boolean containsMediaType(String mediaTypeKey);

    static final class Col {
        public static final String ID = "_id";
        public static final String PRODUCT_ID = "prd_id";
        public static final String PRODUCT_LIST_ID = "prd_lst_id";
        public static final String MEDIA_TYPE_IDS = "media_type_ids";
        public static final String PATH = "path";
        public static final String OLD_PATH = "old_path";
        public static final String PREVIEW_IMAGE_PATH = "prw_img_path";
        public static final String MIME_TYPE = "mime_type";
        public static final String TITLE = "title";
        public static final String WIDTH = "w";
        public static final String HEIGHT = "h";
        public static final String POSITION = "pos";
        public static final String VARIANT_MASTER_DEFAULT = "v_def";
        public static final String BUNDLE_DEFAULT = "b_def";
        public static final String PROGRAMME_DEFAULT = "p_def";
        public static final String ENABLED = "enabled";
    }
}
