package com.geecommerce.search.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.search.model.AutocompleteMapping;

public interface AutocompleteMappings extends Repository {
    public List<AutocompleteMapping> thatBelongsTo(String keyword);
}
