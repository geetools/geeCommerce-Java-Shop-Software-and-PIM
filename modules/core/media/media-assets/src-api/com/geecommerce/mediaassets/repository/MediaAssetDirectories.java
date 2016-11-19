package com.geecommerce.mediaassets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;

import java.util.List;

public interface MediaAssetDirectories extends Repository {
    public List<MediaAssetDirectory> havingParent(Id id);
    public MediaAssetDirectory havingKey(String key);
}
