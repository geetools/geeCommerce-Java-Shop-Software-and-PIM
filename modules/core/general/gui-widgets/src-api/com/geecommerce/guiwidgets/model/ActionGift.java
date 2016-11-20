package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;

public interface ActionGift extends Model {

    public ActionGift setId(Id id);

    public ContextObject<String> getName();

    public ActionGift setName(ContextObject<String> name);

    public ContextObject<String> getDescription();

    public ActionGift setDescription(ContextObject<String> description);

    public Integer getPosition();

    public ActionGift setPosition(Integer position);

    public MediaAsset getMediaAsset();

    public ActionGift setMediaAsset(MediaAsset mediaAsset);

    public String getUrl();

    public ActionGift setUrl(String url);

    static final class Col {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String POSITION = "pos";
        public static final String MEDIA_ASSET_ID = "media_asset_id";
        public static final String URL = "url";
    }

}
