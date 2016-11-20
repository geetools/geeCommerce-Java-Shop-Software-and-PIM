package com.geecommerce.mediaassets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.mediaassets.model.MediaAsset;

public interface MediaAssets extends Repository {
    public MediaAsset havingName(String name);

    public List<MediaAsset> havingGroup(String group);
}
