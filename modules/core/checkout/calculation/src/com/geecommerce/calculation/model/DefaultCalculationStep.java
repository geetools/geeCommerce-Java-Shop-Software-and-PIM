package com.geecommerce.calculation.model;

import java.util.Map;

import com.geecommerce.calculation.repository.CalculationScriptlets;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model("calculation_steps")
public class DefaultCalculationStep extends AbstractModel implements CalculationStep {
    private static final long serialVersionUID = -7009848195522776338L;
    private Id id = null;
    private Id scriptletId = null;
    private Integer sortOrder = null;

    private CalculationScriptlet scriptlet = null;

    private final CalculationScriptlets calculationScriptlets;

    @Inject
    public DefaultCalculationStep(CalculationScriptlets calculationScriptlets) {
        this.calculationScriptlets = calculationScriptlets;
    }

    @Override
    public Id getId() {
        if (id == null) {
            id = app.nextId();
        }

        return id;
    }

    @Override
    public CalculationStep setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getScriptletId() {
        return scriptletId;
    }

    @Override
    public CalculationStep setScriptletId(Id scriptletId) {
        this.scriptletId = scriptletId;
        return this;
    }

    @Override
    public CalculationScriptlet getScriptlet() {
        if (scriptlet == null) {
            scriptlet = calculationScriptlets.findById(CalculationScriptlet.class, getScriptletId());
        }

        return scriptlet;
    }

    @Override
    public CalculationStep setScriptlet(CalculationScriptlet scriptlet) {
        this.scriptlet = scriptlet;
        return this;
    }

    @Override
    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public CalculationStep setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.scriptletId = id_(map.get(Column.SCRIPTLET_ID));
        this.sortOrder = int_(map.get(Column.SORT_ORDER));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.SCRIPTLET_ID, getScriptletId());
        map.put(Column.SORT_ORDER, getSortOrder());

        return map;
    }
}
