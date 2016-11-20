package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;

public interface Page extends Model {

    public Id getId();

    public Page setId(Id id);

    public MediaAsset getMediaAsset();

    public Page setMediaAsset(MediaAsset mediaAsset);

    public Integer getPosition();

    public Page setPosition(Integer position);

    public Boolean getPreview();

    public Page setPreview(Boolean preview);

    static final class Col {
        public static final String ID = "_id";
        public static final String MEDIA_ASSET_ID = "media_asset_id";
        public static final String POSITION = "pos";
        public static final String PREVIEW = "preview";
    }
}
