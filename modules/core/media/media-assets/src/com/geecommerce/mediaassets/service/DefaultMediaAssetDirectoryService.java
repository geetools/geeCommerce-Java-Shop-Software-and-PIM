package com.geecommerce.mediaassets.service;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;
import com.geecommerce.mediaassets.repository.MediaAssetDirectories;
import com.google.inject.Inject;

@Service
public class DefaultMediaAssetDirectoryService implements MediaAssetDirectoryService {
    @Inject
    protected App app;

    protected final MediaAssetDirectories mediaAssetDirectories;

    @Inject
    public DefaultMediaAssetDirectoryService(MediaAssetDirectories mediaAssetDirectories) {
        this.mediaAssetDirectories = mediaAssetDirectories;
    }

    @Override
    public MediaAssetDirectory createOrGetSystem(String path) {
        MediaAssetDirectory directory = mediaAssetDirectories.havingKey(path);
        if (directory == null) {
            String parentPath = getParentPath(path);
            if (StringUtils.isBlank(parentPath)) {
                MediaAssetDirectory mediaAssetDirectory = app.model(MediaAssetDirectory.class);
                mediaAssetDirectory.setName(new ContextObject<>(path));
                mediaAssetDirectory.setKey(path);
                return mediaAssetDirectories.add(mediaAssetDirectory);
            } else {
                MediaAssetDirectory parent = createOrGetSystem(parentPath);
                MediaAssetDirectory mediaAssetDirectory = app.model(MediaAssetDirectory.class);
                mediaAssetDirectory.setName(new ContextObject<>(getKey(path)));
                mediaAssetDirectory.setKey(path);
                mediaAssetDirectory.setParentId(parent.getId());
                return mediaAssetDirectories.add(mediaAssetDirectory);
            }
        }
        return directory;
    }

    private String getParentPath(String path) {
        boolean endsWithSlash = path.endsWith("/");
        if (path.contains("/")) {
            return path.substring(0, path.lastIndexOf("/", endsWithSlash ? path.length() - 2 : path.length() - 1));
        } else {
            return null;
        }
    }

    private String getKey(String path) {
        String[] keys = path.split("/");
        return keys[keys.length - 1];
    }
}
