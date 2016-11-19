package com.geecommerce.mediaassets.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;

import java.io.InputStream;
import java.util.List;

public interface MediaAssetService extends Service {
    public MediaAsset create(InputStream inputStream, String filename);

    public MediaAsset update(MediaAsset mediaAsset);

    public void remove(MediaAsset mediaAsset);

    public MediaAsset update(Id id, InputStream inputStream, String filename);

    public MediaAsset get(Id id);

/*    public MediaAssetFile getContent(Id id);*/

    public List<MediaAsset> get(List<Id> ids);

    public MediaAsset findByName(String name);

    public List<MediaAsset> findByGroup(String group);

    public GridFSDBFile getGridFsFile(Id id);

    public void setMediaAssetUrl(MediaAsset mediaAsset);

}
