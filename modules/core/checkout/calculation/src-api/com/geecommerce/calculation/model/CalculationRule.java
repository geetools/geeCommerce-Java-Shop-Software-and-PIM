package com.geecommerce.calculation.model;

import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CalculationRule extends MultiContextModel {
    public Id getId();

    public CalculationRule setId(Id id);

    public String getCode();

    public CalculationRule setCode(String code);

    public ContextObject<String> getLabel();

    public CalculationRule setLabel(ContextObject<String> label);

    public List<CalculationStep> getCalculationSteps();

    public CalculationRule setCalculationSteps(List<CalculationStep> calculationSteps);

    public CalculationRule addCalculationStep(CalculationStep calculationStep);

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String CALCULATION_STEPS = "calc_steps";
    }
}
