package com.geecommerce.mediaassets.model;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.elasticsearch.annotation.Indexable;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.repository.MediaAssetFiles;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mongodb.gridfs.GridFSFile;
import com.owlike.genson.annotation.JsonIgnore;

@Indexable(collection = "media_asset")
@Model("media_assets")
public class DefaultMediaAsset extends AbstractAttributeSupport
    implements MediaAsset, MediaAssetUrl, SearchIndexSupport {
    private static final long serialVersionUID = -6499739421456471445L;

    private final String UNLABELED = "unlabeled";

    private Id id = null;
    @Column(Col.DIRECTORY)
    private Id directoryId = null;
    private ContextObject<String> name;
    private String url = null;
    private String mimeType = null;
    /* private String name = null; */
    private String group = null;
    private Long size = null;
    private Boolean enabled = null;
    private Map<Object, Object> rawMetadata = null;

    private final MediaAssetService mediaAssetService;
    private final MediaAssetFiles mediaAssetFiles;
    private final ElasticsearchIndexHelper elasticsearchHelper;

    @Inject
    public DefaultMediaAsset(MediaAssetService mediaAssetService, MediaAssetFiles mediaAssetFiles,
        ElasticsearchIndexHelper elasticsearchHelper) {
        this.mediaAssetService = mediaAssetService;
        this.mediaAssetFiles = mediaAssetFiles;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public MediaAsset setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getDirectoryId() {
        return directoryId;
    }

    @Override
    public MediaAsset setDirectoryId(Id directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    /*
     * @Override public String getLabel() { if (label != null) return
     * label.getString(); return UNLABELED; }
     * 
     * @Override public ContextObject<String> getLabels() { return label; }
     * 
     * @Override public MediaAsset setLabel(ContextObject<String> label) {
     * this.label = label; return this; }
     */

    @Override
    public MediaAssetFile getFile() {
        return mediaAssetFiles.getCurrent(getId());
    }

    @Override
    public List<MediaAssetFile> getFiles() {
        return mediaAssetFiles.get(getId());
    }

    @Override
    public String getUrl() {
        if (url == null) {
            mediaAssetService.setMediaAssetUrl(this);
        }
        return url;
    }

    @Override
    public String getUrl(int width, int height) {
        String webPath = getUrl();

        // TODO: FIXME
        /*
         * if (webPath != null && mimeType != null &&
         * mimeType.startsWith("image/")) { int pos = webPath.lastIndexOf('.');
         * StringBuilder sb = new StringBuilder();
         * sb.append(webPath.substring(0, pos)); sb.append("___s:" + width + "x"
         * + height); sb.append(webPath.substring(pos)); return sb.toString(); }
         */

        return webPath;
    }

    @Override
    public MediaAsset setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getMimeType() {
        if ((mimeType == null || mimeType.isEmpty()) && getId() != null) {
            MediaAssetService mediaAssetService = app.service(MediaAssetService.class);
            GridFSFile fsFile = mediaAssetService.getGridFsFile(getId());
            if (fsFile != null) {
                mimeType = fsFile.getContentType();
            }
        }
        return mimeType;
    }

    @Override
    public MediaAsset setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    @Override
    public MediaAsset setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public MediaAsset setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public MediaAsset setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public MediaAsset setRawMetadata(Map<Object, Object> rawMetadata) {
        this.rawMetadata = rawMetadata;
        return this;
    }

    @Override
    public Map<Object, Object> getRawMetadata() {
        return rawMetadata;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public MediaAsset setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String getWebThumbnailPath() {
        String webPath = getUrl();
        /*
         * if (webPath != null && mimeType != null &&
         * mimeType.startsWith("image/")) { int pos = webPath.lastIndexOf('.');
         * StringBuilder sb = new StringBuilder();
         * sb.append(webPath.substring(0, pos)); sb.append("___s:150x150");
         * sb.append(webPath.substring(pos)); return sb.toString(); }
         */

        return webPath;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        /* this.label = ctxObj_(map.get(Col.LABEL)); */
        try {
            this.name = ctxObj_(map.get(Col.NAME));
        } catch (Exception ex) {
            String name = str_(map.get(Col.NAME));
            this.name = new ContextObject<>();
            this.name.addGlobal(name);
        }

        this.group = str_(map.get(Col.GROUP));
        this.mimeType = str_(map.get(Col.MIME_TYPE));
        this.size = long_(map.get(Col.SIZE));
        this.rawMetadata = objMap_(map.get(Col.RAW_METADATA));
        this.enabled = bool_(map.get(Col.ENABLED));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());
        /* map.put(Col.LABEL, getLabels()); */
        map.put(Col.NAME, getName());
        map.put(Col.GROUP, getGroup());
        map.put(Col.MIME_TYPE, getMimeType());
        map.put(Col.SIZE, getSize());
        map.put(Col.RAW_METADATA, getRawMetadata());
        map.put(Col.ENABLED, getEnabled());
        return map;
    }

    private static final String FIELD_KEY_ID = "_id";
    private static final String FIELD_KEY_NAME = "name";
    private static final String FIELD_KEY_MIME = "mime_type";

    @Override
    @JsonIgnore
    public Map<String, Object> getIndexMap() {
        Map<String, Object> json = new LinkedHashMap<>();

        json.put(FIELD_KEY_ID, id);
        json.put(FIELD_KEY_MIME, getMimeType());
        json.put(FIELD_KEY_NAME, getName().str());

        boolean isVisible = true;

        Set<Id> indexedAttributes = new HashSet<>();
        for (AttributeValue attributeValue : getAttributes()) {
            Attribute attr = attributeValue.getAttribute();

            if (attr != null && (attr.isIncludeInSearchIndex() || attr.isSearchable()
                || attr.getIncludeInProductListFilter() || attr.getShowInQuery())) {
                // Only index text values when product is visible.
                if (!isVisible && BackendType.STRING == attr.getBackendType())
                    continue;

                elasticsearchHelper.addAttribute(json, attr, attributeValue);
                indexedAttributes.add(attr.getId());
            }
        }

        return json;
    }
}
