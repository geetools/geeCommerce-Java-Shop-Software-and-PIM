package com.geecommerce.core.batch.dataimport.model;

import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ImportPlan extends Model {
    Id getId();

    ImportPlan setId(Id id);

    String getToken();

    ImportPlan setToken(String token);

    Map<String, Integer> getActions();

    ImportPlan setActions(Map<String, Integer> actions);

    ImportPlan addAction(String action);

    ImportPlan addAction(String action, int count);

    ImportPlan setActionComplete(String action);

    ImportPlan setActionError(String action);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TOKEN = "token";
        public static final String ACTIONS = "actions";
        public static final String ACTION_STATUSES = "statuses";
    }
}
