package com.geecommerce.search.service;

import java.util.Collections;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.search.model.SearchRewrite;
import com.geecommerce.search.repository.SearchRewrites;
import com.google.inject.Inject;

@Service
public class DefaultSearchRewriteService implements SearchRewriteService {

    private final SearchRewrites searchRewrites;

    @Inject
    public DefaultSearchRewriteService(SearchRewrites searchRewrites) {
        this.searchRewrites = searchRewrites;
    }

    @Override
    public String findUrl(String keyword) {
        SearchRewrite rewrite = searchRewrites.findOne(SearchRewrite.class,
            Collections.singletonMap(SearchRewrite.Col.KEYWORDS, keyword.toLowerCase()));
        return rewrite != null ? rewrite.getTargetUri() : null;
    }
}
