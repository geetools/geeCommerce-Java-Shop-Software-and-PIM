package com.geecommerce.mediaassets.model;

import java.io.InputStream;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mongodb.gridfs.GridFSDBFile;
import com.owlike.genson.annotation.JsonIgnore;

@Model("media_asset_files")
public class DefaultMediaAssetFile extends AbstractModel implements MediaAssetFile {
    private static final long serialVersionUID = -2853385687658408681L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.PREVIEW_IMAGE_ID)
    private Id previewImageId = null;

    @Column(Col.PREVIEW_DOC_ID)
    private Id previewDocId = null;

    @Column(Col.MEDIA_ASSET_ID)
    private Id mediaAssetId = null;

    @Column(Col.NAME)
    private String name = null;

    @Column(Col.PREVIEW_IMAGE_MIME_TYPE)
    private String previewImageMimeType;

    @Column(Col.PREVIEW_DOCUMENT_MIME_TYPE)
    private String previewDoumentMimeType;

    @Column(Col.MIME_TYPE)
    private String mimeType;

    @Column(Col.SIZE)
    private Long size;

    // @Column(Col.ACTIVE)
    private boolean active;

    private Map<Object, Object> metadata = null;

    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultMediaAssetFile(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    @JsonIgnore
    public InputStream getContent() {
        GridFSDBFile file = mediaAssetService.getGridFsFile(getId());
        return file.getInputStream();
    }

    @Override
    public MediaAssetFile setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return "dma/file/" + getId();
    }

    @Override
    public String getPreviewImageUrl() {
        if (getPreviewImageId() != null)
            return "dma/file/" + getPreviewImageId();
        return null;
    }

    @Override
    public String getPreviewDocumentUrl() {
        if (getPreviewDocId() != null)
            return "dma/file/" + getPreviewDocId();
        return null;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public MediaAssetFile setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public MediaAssetFile setMimeType(String type) {
        this.mimeType = type;
        return this;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public MediaAssetFile setPreviewImageMimeType(String type) {
        this.previewImageMimeType = type;
        return this;
    }

    @Override
    public String getPreviewImageMimeType() {
        return previewImageMimeType;
    }

    @Override
    public MediaAssetFile setPreviewDocumentMimeType(String type) {
        this.previewDoumentMimeType = type;
        return this;
    }

    @Override
    public String getPreviewDocumentMimeType() {
        return previewDoumentMimeType;
    }

    @Override
    public MediaAssetFile setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public MediaAssetFile setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getPreviewImageId() {
        return previewImageId;
    }

    @Override
    public MediaAssetFile setPreviewImageId(Id id) {
        this.previewImageId = id;
        return this;
    }

    @Override
    public Id getPreviewDocId() {
        return previewDocId;
    }

    @Override
    public MediaAssetFile setPreviewDocId(Id id) {
        this.previewDocId = id;
        return this;
    }

    @Override
    public Id getMediaAssetId() {
        return mediaAssetId;
    }

    @Override
    public MediaAssetFile setMediaAssetId(Id id) {
        this.mediaAssetId = id;
        return this;
    }

    @Override
    public MediaAssetFile setMetadata(Map<Object, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public Map<Object, Object> getMetadata() {
        return metadata;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);
        this.metadata = objMap_(map.get(Col.RAW_METADATA));
        this.active = bool_(map.get(Col.ACTIVE), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.RAW_METADATA, getMetadata());
        map.put(Col.ACTIVE, getActive());

        return map;
    }
}
