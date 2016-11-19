package com.geecommerce.calculation.model;

import java.util.Map;

import com.geecommerce.core.service.api.Injectable;

public interface CalculationItemResult extends Injectable, Map<String, Object> {
    public Integer getInteger(String key);

    public Double getDouble(String key);

    public Map<String, CalculationSubItemResult> getSubItemResults();

    public CalculationItemResult addSubItemResult(String subItemKey, String key, Object value);
}
