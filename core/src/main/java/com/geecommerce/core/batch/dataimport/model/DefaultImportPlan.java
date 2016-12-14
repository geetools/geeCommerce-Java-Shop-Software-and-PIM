package com.geecommerce.core.batch.dataimport.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Model("import_plans")
public class DefaultImportPlan extends AbstractModel implements ImportPlan {
    private static final long serialVersionUID = -2155047709349897745L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.TOKEN)
    protected String token;

    @Column(Col.ACTIONS)
    protected Map<String, Integer> actions = new LinkedHashMap<>();

    @Column(Col.ACTION_STATUSES)
    protected Map<String, Integer> actionStatuses = new LinkedHashMap<>();

    @Inject
    protected App app;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ImportPlan setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public ImportPlan setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public Map<String, Integer> getActions() {
        return actions;
    }

    @Override
    public ImportPlan setActions(Map<String, Integer> actions) {
        this.actions = actions;
        return this;
    }

    @Override
    public ImportPlan addAction(String action) {
        Integer count = actions.get(action);

        if (count == null) {
            actions.put(action, 1);
            actionStatuses.put(action, 0);
        } else {
            actions.put(action, count + 1);
        }

        return this;
    }

    @Override
    public ImportPlan addAction(String action, int count) {
        actions.put(action, count);
        actionStatuses.put(action, 0);
        return this;
    }

    @Override
    public ImportPlan setActionComplete(String action) {
        actionStatuses.put(action, 1);
        return this;
    }

    @Override
    public ImportPlan setActionError(String action) {
        actionStatuses.put(action, -1);
        return this;
    }

    @Override
    public String toString() {
        return "DefaultImportPlan [id=" + id + ", token=" + token + ", actions=" + actions + ", actionStatuses=" + actionStatuses + "]";
    }
}
