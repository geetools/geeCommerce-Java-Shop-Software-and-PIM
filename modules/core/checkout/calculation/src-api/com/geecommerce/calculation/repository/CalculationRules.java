package com.geecommerce.calculation.repository;

import com.geecommerce.calculation.model.CalculationRule;
import com.geecommerce.core.service.api.Repository;

public interface CalculationRules extends Repository {
    public CalculationRule havingCode(String code);

}
