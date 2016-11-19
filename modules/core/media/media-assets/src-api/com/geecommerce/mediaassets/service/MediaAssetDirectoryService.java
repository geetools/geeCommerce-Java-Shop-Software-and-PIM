package com.geecommerce.mediaassets.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;

public interface MediaAssetDirectoryService extends Service {
    public MediaAssetDirectory createOrGetSystem(String path);
}
