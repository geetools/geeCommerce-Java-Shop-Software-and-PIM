package com.geecommerce.core.system.model;

import java.util.List;

import com.owlike.genson.annotation.JsonIgnore;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface UrlRewrite extends MultiContextModel {
    public Id getId();

    public UrlRewrite setId(Id id);

    public Id getId2();

    public UrlRewrite setId2(Id id2);

    public ContextObject<String> getRequestURI();

    public UrlRewrite setRequestURI(ContextObject<String> requestURI);

    public String getRequestMethod();

    public UrlRewrite setRequestMethod(String requestMethod);

    public String getTargetURL();

    public UrlRewrite setTargetURL(String targetURL);

    public Id getTargetObjectId();

    public UrlRewrite setTargetObjectId(Id targetObjectId);

    public ObjectType getTargetObjectType();

    public UrlRewrite setTargetObjectType(ObjectType targetObjectType);

    public List<String> getFlags();

    public UrlRewrite setFlags(List<String> flags);

    public boolean isManual();

    public UrlRewrite setManual(boolean manual);

    public boolean isEnabled();

    public UrlRewrite setEnabled(boolean enabled);

    @JsonIgnore
    public boolean isForProductList();

    @JsonIgnore
    public boolean isForProduct();

    @JsonIgnore
    public boolean isForCMS();

    @JsonIgnore
    public boolean hasTargetUrl();

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";

        public static final String REQUEST_URI = "req_uri";
        public static final String REQUEST_METHOD = "req_mtd";
        public static final String TARGET_URL = "tar_url";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TARGET_OBJECT_TYPE = "tar_obj_type";
        public static final String FLAGS = "flags";
        public static final String MANUAL_REWRITE = "manual";
        public static final String ENABLED = "enabled";
    }
}
