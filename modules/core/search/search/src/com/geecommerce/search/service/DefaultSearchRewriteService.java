package com.geecommerce.search.service;

import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.search.model.*;
import com.geecommerce.search.repository.SearchRewrites;

import java.util.Collections;

@Service
public class DefaultSearchRewriteService implements SearchRewriteService {

    private final SearchRewrites searchRewrites;

    @Inject
    public DefaultSearchRewriteService(SearchRewrites searchRewrites) {
	this.searchRewrites = searchRewrites;
    }

    @Override
    public String findUrl(String keyword) {
	SearchRewrite rewrite = searchRewrites.findOne(SearchRewrite.class, Collections.singletonMap(SearchRewrite.Col.KEYWORDS, keyword.toLowerCase()));
	return rewrite != null ? rewrite.getTargetUri() : null;
    }
}
