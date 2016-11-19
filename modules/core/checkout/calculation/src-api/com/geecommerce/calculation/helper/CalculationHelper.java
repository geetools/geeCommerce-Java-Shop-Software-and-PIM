package com.geecommerce.calculation.helper;

import java.util.List;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationData;
import com.geecommerce.calculation.model.CalculationStep;
import com.geecommerce.core.service.api.Helper;

public interface CalculationHelper extends Helper {
    public List<CalculationStep> sortCalculationSteps(List<CalculationStep> calcSteps);

    public CalculationContext newCalculationContext(CalculationData calcData);
}
