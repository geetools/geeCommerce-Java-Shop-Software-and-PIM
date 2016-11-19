package com.geecommerce.mediaassets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;

import java.util.List;

public interface MediaAssetFiles extends Repository {
    public MediaAssetFile getCurrent(Id mediaAssetId);

    public List<MediaAssetFile> get(Id mediaAssetId);
}
