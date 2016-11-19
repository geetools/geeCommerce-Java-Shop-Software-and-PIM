package com.geecommerce.calculation.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationRule;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultCalculationRules extends AbstractRepository implements CalculationRules {
    @Override
    public CalculationRule havingCode(String code) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CalculationRule.Col.CODE, code);

        return multiContextFindOne(CalculationRule.class, filter);
    }
}
