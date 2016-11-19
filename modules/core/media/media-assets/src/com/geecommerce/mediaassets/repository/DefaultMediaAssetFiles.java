package com.geecommerce.mediaassets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetFile;

@Repository
public class DefaultMediaAssetFiles extends AbstractRepository implements MediaAssetFiles {
    @Override
    public MediaAssetFile getCurrent(Id mediaAssetId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAssetFile.Col.ACTIVE, true);
        filter.put(MediaAssetFile.Col.MEDIA_ASSET_ID, mediaAssetId);
        return findOne(MediaAssetFile.class, filter);
    }

    @Override
    public List<MediaAssetFile> get(Id mediaAssetId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(MediaAssetFile.Col.MEDIA_ASSET_ID, mediaAssetId);
        return find(MediaAssetFile.class, filter);
    }
}
