package com.geecommerce.core.elasticsearch.api;

import com.geecommerce.core.type.Id;

import java.util.Map;

public interface SearchIndexSupport {
    public Map<String, Object> getIndexMap();

    public Id getId();
}
