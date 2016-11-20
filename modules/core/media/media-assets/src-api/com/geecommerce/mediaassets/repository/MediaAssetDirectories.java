package com.geecommerce.mediaassets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;

public interface MediaAssetDirectories extends Repository {
    public List<MediaAssetDirectory> havingParent(Id id);

    public MediaAssetDirectory havingKey(String key);
}
