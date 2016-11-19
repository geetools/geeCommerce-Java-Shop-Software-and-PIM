package com.geecommerce.core.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultUrlRewriteService implements UrlRewriteService {
    private final UrlRewrites urlRewrites;
    @SuppressWarnings("unused")
    private final UrlRewriteHelper urlRewriteHelper;

    @Inject
    public DefaultUrlRewriteService(UrlRewrites urlRewrites, UrlRewriteHelper urlRewriteHelper) {
	this.urlRewrites = urlRewrites;
	this.urlRewriteHelper = urlRewriteHelper;
    }

    @Override
    public UrlRewrite createUrlRewrite(UrlRewrite urlRewrite) {
	return urlRewrites.add(urlRewrite);
    }

    @Override
    public UrlRewrite findUrlRewrite(String uri) {
	return urlRewrites.havingURI(uri);
    }

    @Override
    public List<UrlRewrite> findUrlRewrites(Id[] targetObjectIds, ObjectType objType) {
	Map<String, Object> filter = new HashMap<>();

	DBObject inClause = new BasicDBObject();
	inClause.put("$in", targetObjectIds);
	filter.put(UrlRewrite.Col.TARGET_OBJECT_ID, inClause);
	filter.put(UrlRewrite.Col.TARGET_OBJECT_TYPE, objType.toId());
	filter.put(UrlRewrite.Col.ENABLED, true);

	return urlRewrites.multiContextFind(UrlRewrite.class, filter, UrlRewrite.Col.TARGET_OBJECT_ID);
    }

    @Override
    public List<UrlRewrite> findUrlRewritesForProducts(Id... ids) {
	return findUrlRewrites(ids, ObjectType.PRODUCT);
    }

    @Override
    public List<UrlRewrite> findUrlRewritesForProductLists(Id... ids) {
	return findUrlRewrites(ids, ObjectType.PRODUCT_LIST);
    }
}
