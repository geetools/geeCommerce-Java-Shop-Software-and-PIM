package com.geecommerce.mediaassets.model;

import java.io.InputStream;
import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface MediaAssetFile extends Model {

    public Id getId();

    public MediaAssetFile setId(Id id);

    public Id getPreviewImageId();

    public MediaAssetFile setPreviewImageId(Id id);

    public Id getPreviewDocId();

    public MediaAssetFile setPreviewDocId(Id id);

    public Id getMediaAssetId();

    public MediaAssetFile setMediaAssetId(Id id);

    public MediaAssetFile setMetadata(Map<Object, Object> metadata);

    public Map<Object, Object> getMetadata();

    // public MediaAssetFile setContent(InputStream inputStream);

    @JsonIgnore
    public InputStream getContent();

    public MediaAssetFile setName(String name);

    public String getName();

    public String getUrl();

    public String getPreviewImageUrl();

    public String getPreviewDocumentUrl();

    public Long getSize();

    public MediaAssetFile setSize(Long size);

    public MediaAssetFile setMimeType(String type);

    public String getMimeType();

    public MediaAssetFile setPreviewImageMimeType(String type);

    public String getPreviewImageMimeType();

    public MediaAssetFile setPreviewDocumentMimeType(String type);

    public String getPreviewDocumentMimeType();

    public MediaAssetFile setActive(boolean active);

    public boolean getActive();

    public boolean isActive();

    static final class Col {
        public static final String ID = "_id";
        public static final String PREVIEW_IMAGE_ID = "pr_img_id";
        public static final String PREVIEW_DOC_ID = "pr_doc_id";
        public static final String MEDIA_ASSET_ID = "ma_id";
        public static final String NAME = "name";
        public static final String GROUP = "group";
        public static final String MIME_TYPE = "mime_type";
        public static final String PREVIEW_IMAGE_MIME_TYPE = "prev_img_mime_type";
        public static final String PREVIEW_DOCUMENT_MIME_TYPE = "prev_doc_mime_type";
        public static final String ENABLED = "enabled";
        public static final String SIZE = "size";
        public static final String RAW_METADATA = "raw_meta";
        public static final String ACTIVE = "active";
    }
}
