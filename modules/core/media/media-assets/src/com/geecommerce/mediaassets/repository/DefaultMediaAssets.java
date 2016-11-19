package com.geecommerce.mediaassets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.mediaassets.model.MediaAsset;

@Repository
public class DefaultMediaAssets extends AbstractRepository implements MediaAssets {
    @Override
    public MediaAsset havingName(String name) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAsset.Col.NAME, name);
        filter.put(MediaAsset.Col.ENABLED, true);
        return multiContextFindOne(MediaAsset.class, filter);
    }

    @Override
    public List<MediaAsset> havingGroup(String group) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAsset.Col.GROUP, group);
        filter.put(MediaAsset.Col.ENABLED, true);
        return find(MediaAsset.class, filter);
    }
}
