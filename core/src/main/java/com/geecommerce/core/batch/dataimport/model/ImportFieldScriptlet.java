package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportFieldScriptlet extends Model {

    Id getId();

    ImportFieldScriptlet setId(Id id);

    Id getTargetObjectId();

    ImportFieldScriptlet setTargetObjectId(Id targetObjectId);

    String getFieldName();

    ImportFieldScriptlet setFieldName(String fieldName);

    String getScript();

    ImportFieldScriptlet setScript(String script);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String FIELD_NAME = "field_name";
        public static final String SCRIPT = "script";
    }
}
