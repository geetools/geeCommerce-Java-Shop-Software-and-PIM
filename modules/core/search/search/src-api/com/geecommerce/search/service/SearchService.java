package com.geecommerce.search.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.search.model.AutocompleteMapping;
import com.geecommerce.search.model.SearchQuery;
import com.geecommerce.search.model.SearchResult;

public interface SearchService extends Service {
    public SearchResult autocomplete(SearchQuery searchQuery);

    public SearchResult query(SearchQuery searchQuery);

    public List<AutocompleteMapping> getAutocompleteMappingsByKeyword(String keyword);
}
