package com.geecommerce.guiwidgets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.guiwidgets.model.Content;

@Repository
public class DefaultContents extends AbstractRepository implements Contents {
    @Override
    public List<Content> withKey(String key) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Content.Col.KEY, key);
        return find(Content.class, filter);
    }
}
