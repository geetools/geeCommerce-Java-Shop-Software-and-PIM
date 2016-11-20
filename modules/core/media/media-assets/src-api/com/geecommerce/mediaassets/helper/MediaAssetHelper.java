package com.geecommerce.mediaassets.helper;

import java.io.InputStream;

import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.mongodb.gridfs.GridFSDBFile;

public interface MediaAssetHelper {

    Id createPreviewPdfFile(GridFSDBFile file);

    Id createPreviewImage(GridFSDBFile file);

    void createPreview(MediaAssetFile mediaAssetFile, GridFSDBFile file);

    long createGridFsFile(Id id, InputStream inputStream, String filename, String mimeType);
}
