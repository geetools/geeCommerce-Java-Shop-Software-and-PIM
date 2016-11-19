package com.geecommerce.calculation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Cacheable
@Model("calculation_rules")
public class DefaultCalculationRule extends AbstractMultiContextModel implements CalculationRule {
    private static final long serialVersionUID = -6600541897762431345L;
    private Id id = null;
    private String code = null;
    private ContextObject<String> label = null;
    @Column(name = Col.CALCULATION_STEPS, autoPopulate = false)
    private List<CalculationStep> calculationSteps = new ArrayList<>();

    private final CalculationHelper calculationHelper;

    @Inject
    public DefaultCalculationRule(CalculationHelper calculationHelper) {
        this.calculationHelper = calculationHelper;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CalculationRule setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public CalculationRule setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public CalculationRule setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    public List<CalculationStep> getCalculationSteps() {
        return calculationSteps;
    }

    public CalculationRule setCalculationSteps(List<CalculationStep> calculationSteps) {
        this.calculationSteps = calculationSteps;
        return this;
    }

    @Override
    public CalculationRule addCalculationStep(CalculationStep calculationStep) {
        this.calculationSteps.add(calculationStep);
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.code = str_(map.get(Col.CODE));
        this.label = ctxObj_(map.get(Col.LABEL));

        // ----------------------------------------------------------------------------------------
        // Calculation Steps
        // ----------------------------------------------------------------------------------------
        List<Map> calcSteps = (List<Map>) map.get(Col.CALCULATION_STEPS);

        if (calcSteps != null && !calcSteps.isEmpty()) {
            for (Map m : calcSteps) {
                CalculationStep calcStep = app.getModel(CalculationStep.class);
                calcStep.fromMap(m);

                this.calculationSteps.add(calcStep);
            }
        }

        calculationSteps = calculationHelper.sortCalculationSteps(calculationSteps);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());
        map.put(Col.CODE, getCode());
        map.put(Col.LABEL, getLabel());

        // ----------------------------------------------------------------------------------------
        // Calculation Steps
        // ----------------------------------------------------------------------------------------

        ArrayList<Map<String, Object>> calcSteps = new ArrayList<>();

        for (CalculationStep step : getCalculationSteps()) {
            calcSteps.add(step.toMap());
        }

        map.put(Col.CALCULATION_STEPS, calcSteps);

        return map;
    }
}
