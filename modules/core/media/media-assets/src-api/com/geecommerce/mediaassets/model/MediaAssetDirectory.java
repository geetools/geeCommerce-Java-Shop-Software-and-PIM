package com.geecommerce.mediaassets.model;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface MediaAssetDirectory extends MultiContextModel, AttributeSupport {

    public Id getId();

    public MediaAssetDirectory setId(Id id);

    public ContextObject<String> getName();

    public MediaAssetDirectory setName(ContextObject<String> name);

    public String getKey();

    public MediaAssetDirectory setKey(String key);

/*
    public String getUri();

    public MediaAssetDirectory setUri(String uri);
*/

    public Id getParentId();

    public MediaAssetDirectory setParentId(Id id);

    public List<MediaAssetDirectory> getChildren();

    static final class Col {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String KEY = "key";
/*        public static final String URI = "uri";*/
        public static final String PARENT_ID = "parent_id";
    }
}
