package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportPlanInfo extends Model {
    Id getId();

    ImportPlanInfo setId(Id id);

    String getName();

    ImportPlanInfo setName(String name);

    Long getCount();

    ImportPlanInfo setCount(Long count);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String COUNT = "count";
    }
}
