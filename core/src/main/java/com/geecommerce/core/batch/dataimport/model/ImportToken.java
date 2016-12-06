package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportToken extends Model {

    public Id getId();

    public ImportToken setId(Id id);

    public Id getTargetObjectId();

    public ImportToken setTargetObjectId(Id targetObjectId);

    public String getToken();

    public ImportToken setToken(String token);

    public String getFilePath();

    public ImportToken setFilePath(String filePath);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TOKEN = "token";
        public static final String FILE_PATH = "file_path";
    }
}
