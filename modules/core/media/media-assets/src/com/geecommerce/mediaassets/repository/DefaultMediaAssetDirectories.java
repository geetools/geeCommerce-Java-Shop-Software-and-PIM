package com.geecommerce.mediaassets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;

@Repository
public class DefaultMediaAssetDirectories extends AbstractRepository implements MediaAssetDirectories {
    @Override
    public List<MediaAssetDirectory> havingParent(Id id) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAssetDirectory.Col.PARENT_ID, id);
        return find(MediaAssetDirectory.class, filter);
    }

    @Override
    public MediaAssetDirectory havingKey(String key) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAssetDirectory.Col.KEY, key);
        return findOne(MediaAssetDirectory.class, filter);
    }
}
