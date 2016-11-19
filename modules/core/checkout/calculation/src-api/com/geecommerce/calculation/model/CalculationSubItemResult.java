package com.geecommerce.calculation.model;

import com.geecommerce.core.service.api.Injectable;

import java.util.Map;

public interface CalculationSubItemResult extends Injectable, Map<String, Object> {
    public Integer getInteger(String key);

    public Double getDouble(String key);
}
