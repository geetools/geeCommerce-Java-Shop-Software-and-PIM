package com.geecommerce.guiwidgets.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model
public class DefaultPage extends AbstractModel implements Page {

    private Id id = null;
    private MediaAsset mediaAsset = null;
    @Column(Col.MEDIA_ASSET_ID)
    private Id mediaAssetId = null;
    private Integer position = null;
    private Boolean preview = null;

    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultPage(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public Page setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public MediaAsset getMediaAsset() {
        if (mediaAsset == null && mediaAssetId != null)
            mediaAsset = mediaAssetService.get(mediaAssetId);
        return mediaAsset;
    }

    @Override
    public Page setMediaAsset(MediaAsset mediaAsset) {
        this.mediaAsset = mediaAsset;
        this.mediaAssetId = mediaAsset.getId();
        return this;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public Page setPosition(Integer position) {
        this.position = position;
        return this;
    }

    @Override
    public Boolean getPreview() {
        return preview;
    }

    @Override
    public Page setPreview(Boolean preview) {
        this.preview = preview;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.mediaAssetId = id_(map.get(Col.MEDIA_ASSET_ID));
        this.position = int_(map.get(Col.POSITION), 0);
        this.preview = bool_(map.get(Col.PREVIEW), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());
        map.put(Col.ID, getId());
        map.put(Col.MEDIA_ASSET_ID, mediaAssetId);
        map.put(Col.POSITION, getPosition());
        map.put(Col.PREVIEW, getPreview());
        return map;
    }
}
