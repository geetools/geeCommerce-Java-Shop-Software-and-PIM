package com.geecommerce.calculation.model;

import java.util.Map;

import com.geecommerce.calculation.CalculationType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable
@Model("calculation_scriptlets")
public class DefaultCalculationScriptlet extends AbstractMultiContextModel implements CalculationScriptlet {
    private static final long serialVersionUID = -449869001346042309L;
    private Id id = null;
    private CalculationType type = null;
    private String code = null;
    private ContextObject<String> label = null;
    private String body = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CalculationScriptlet setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public CalculationType getType() {
        return type;
    }

    @Override
    public CalculationScriptlet setType(CalculationType type) {
        this.type = type;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public CalculationScriptlet setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public CalculationScriptlet setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public CalculationScriptlet setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public boolean isValid() {
        return code != null && !"".equals(code.trim()) && label != null && body != null && !"".equals(body.trim());
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.type = CalculationType.fromId(int_(map.get(Column.TYPE)));
        this.code = str_(map.get(Column.CODE));
        this.label = ctxObj_(map.get(Column.LABEL));
        this.body = str_(map.get(Column.BODY));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.TYPE, getType().toId());
        map.put(Column.CODE, getCode());
        map.put(Column.LABEL, getLabel());
        map.put(Column.BODY, getBody());

        return map;
    }
}
