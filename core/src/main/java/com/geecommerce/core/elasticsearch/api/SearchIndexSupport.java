package com.geecommerce.core.elasticsearch.api;

import java.util.Map;

import com.geecommerce.core.type.Id;

public interface SearchIndexSupport {
    public Map<String, Object> getIndexMap();

    public Id getId();
}
