package com.geecommerce.core.system.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface UrlRewriteHelper extends Helper {
    public Id[] filterCompletedTargetObjectIds(List<UrlRewrite> urlRewrites, Id... targetObjectIds);

    public boolean isExcludedFromURLRewriting(String path);

    public boolean isUriUnique(ObjectType targetObjectType, Id targetObjectId, String uri);

    public Map<String, Boolean> isUriUnique(ObjectType targetObjectType, Id targetObjectId, ContextObject<String> uris);

    public Map<String, Boolean> isUriUnique(ContextObject<String> uris);
}
