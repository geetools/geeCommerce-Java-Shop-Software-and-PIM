package com.geecommerce.search.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.List;

/**
 * Created by Andrey on 05.10.2015.
 */
@Model("search_rewrites")
public class DefaultSearchRewrite extends AbstractMultiContextModel implements SearchRewrite {

    @Column(Col.ID)
    private Id id;

    @Column(Col.KEYWORDS)
    private List<String> keywords;

    @Column(Col.TARGET_URI)
    private String targetUri;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public SearchRewrite setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public List<String> getKeywords() {
	return keywords;
    }

    @Override
    public SearchRewrite setKeywords(List<String> keywords) {
	this.keywords = keywords;
	return this;
    }

    @Override
    public String getTargetUri() {
	return targetUri;
    }

    @Override
    public SearchRewrite setTargetUri(String targetUri) {
	this.targetUri = targetUri;
	return this;
    }
}
