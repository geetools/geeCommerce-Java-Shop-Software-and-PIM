package com.geecommerce.calculation.model;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;

import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.type.Id;

public interface CalculationResult extends Injectable, Map<String, Object> {
    public CalculationItemResult getItemResult(String productId);

    public CalculationItemResult getItemResult(Id productId);

    public Map<Id, CalculationItemResult> getItemResults();

    public CalculationResult setItemResults(Map<Id, CalculationItemResult> itemResults);

    public Map<String, Object> getResults();

    public CalculationResult setResults(Map<String, Object> results);

    public Integer getInteger(String key);

    public Double getDouble(String key);

    public CalculationResult fromJSON(String json) throws JSONException;
}
