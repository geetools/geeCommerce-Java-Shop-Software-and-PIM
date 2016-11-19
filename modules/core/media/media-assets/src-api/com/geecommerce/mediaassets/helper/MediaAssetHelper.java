package com.geecommerce.mediaassets.helper;

import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.mongodb.gridfs.GridFSDBFile;

import java.io.InputStream;

public interface MediaAssetHelper {

    Id createPreviewPdfFile(GridFSDBFile file);

    Id createPreviewImage(GridFSDBFile file);

    void createPreview(MediaAssetFile mediaAssetFile, GridFSDBFile file);

    long createGridFsFile(Id id, InputStream inputStream, String filename, String mimeType);
}
