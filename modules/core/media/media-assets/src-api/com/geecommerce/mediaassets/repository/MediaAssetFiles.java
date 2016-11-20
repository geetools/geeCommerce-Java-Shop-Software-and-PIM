package com.geecommerce.mediaassets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetFile;

public interface MediaAssetFiles extends Repository {
    public MediaAssetFile getCurrent(Id mediaAssetId);

    public List<MediaAssetFile> get(Id mediaAssetId);
}
