package com.geecommerce.search.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.search.model.AutocompleteMapping;
import com.geecommerce.search.model.AutocompleteMapping.Column;

@Repository
public class DefaultAutocompleteMappings extends AbstractRepository implements AutocompleteMappings {
    @Override
    public List<AutocompleteMapping> thatBelongsTo(String keyword) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Column.KEYWORD, keyword);
        List<AutocompleteMapping> result = find(AutocompleteMapping.class, filter);
        if (result == null || result.isEmpty()) {
            filter = new HashMap<>();
            filter.put(Column.DIVIDED_KEYWORD, keyword);
            result = find(AutocompleteMapping.class, filter);
        }
        return result;
    }
}
