package com.geecommerce.mediaassets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.mediaassets.model.MediaAsset;

import java.util.List;

public interface MediaAssets extends Repository {
    public MediaAsset havingName(String name);

    public List<MediaAsset> havingGroup(String group);
}
