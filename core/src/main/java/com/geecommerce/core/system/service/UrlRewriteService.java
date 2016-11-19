package com.geecommerce.core.system.service;

import java.util.List;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;

public interface UrlRewriteService extends Service {
    public UrlRewrite createUrlRewrite(UrlRewrite urlRewrite);

    public UrlRewrite findUrlRewrite(String uri);

    public List<UrlRewrite> findUrlRewritesForProducts(Id... ids);

    public List<UrlRewrite> findUrlRewritesForProductLists(Id... ids);

    public List<UrlRewrite> findUrlRewrites(Id[] targetObjectIds, ObjectType type);
}
