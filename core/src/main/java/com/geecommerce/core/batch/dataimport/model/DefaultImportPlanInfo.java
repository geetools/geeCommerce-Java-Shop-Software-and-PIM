package com.geecommerce.core.batch.dataimport.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Model("import_profiles")
public class DefaultImportPlanInfo extends AbstractModel implements ImportPlanInfo {
    private static final long serialVersionUID = -2155047709349897745L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.NAME)
    protected String name = null;

    @Column(Col.COUNT)
    protected Long count;

    @Inject
    protected App app;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ImportPlanInfo setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImportPlanInfo setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Long getCount() {
        return count;
    }

    @Override
    public ImportPlanInfo setCount(Long count) {
        this.count = count;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.name = str_(map.get(Col.NAME));
        this.count = long_(map.get(Col.COUNT));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put(Col.NAME, getName());
        data.put(Col.COUNT, getCount());

        return data;
    }

    @Override
    public String toString() {
        return "DefaultImportPlanInfo [id=" + id + ", name=" + name + ", count=" + count + "]";
    }
}
