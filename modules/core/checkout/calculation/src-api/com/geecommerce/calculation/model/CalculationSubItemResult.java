package com.geecommerce.calculation.model;

import java.util.Map;

import com.geecommerce.core.service.api.Injectable;

public interface CalculationSubItemResult extends Injectable, Map<String, Object> {
    public Integer getInteger(String key);

    public Double getDouble(String key);
}
