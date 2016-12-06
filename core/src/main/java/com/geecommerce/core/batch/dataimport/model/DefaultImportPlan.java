package com.geecommerce.core.batch.dataimport.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Column(Col.PLAN)
    protected List<ImportPlanInfo> importPlanInfos = new ArrayList<>();

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
    public void fromMap(Map<String, Object> map) {
        List<Map<String, Object>> _importPlan = list_(map.get(Col.PLAN));

        for (Map<String, Object> innerMap : _importPlan) {
            ImportPlanInfo importPlanInfo = app.model(ImportPlanInfo.class);
            importPlanInfo.fromMap(innerMap);

            importPlanInfos.add(importPlanInfo);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>(super.toMap());

        List<Map<String, Object>> _importPlan = new ArrayList<>();

        for (ImportPlanInfo planInfo : importPlanInfos) {
            _importPlan.add(planInfo.toMap());
        }

        data.put(Col.PLAN, _importPlan);

        return data;
    }

    @Override
    public String toString() {
        return "DefaultImportPlan [id=" + id + ", token=" + token + ", importPlanInfos=" + importPlanInfos + "]";
    }
}
