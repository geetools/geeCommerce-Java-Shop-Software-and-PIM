package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportPlan extends Model {
    Id getId();

    ImportPlan setId(Id id);

    String getToken();

    ImportPlan setToken(String token);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TOKEN = "token";
        public static final String PLAN = "plan";
    }
}
