package com.geecommerce.calculation.service;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.CalculationRule;
import com.geecommerce.calculation.model.CalculationScriptlet;
import com.geecommerce.core.service.api.Service;

public interface CalculationService extends Service {
    public CalculationResult calculateTotals(CalculationContext ctx) throws Exception;

    public CalculationScriptlet insertCalculationScriptlet(CalculationScriptlet calculationScriptlet);

    public CalculationRule insertCalculationRule(CalculationRule calculationRule);

    public CalculationRule getCalculationRule();
}
