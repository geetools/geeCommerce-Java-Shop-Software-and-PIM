package com.geecommerce.calculation.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.annotation.Injectable;

@Injectable
public class DefaultCalculationItemResult extends LinkedHashMap<String, Object> implements CalculationItemResult, Map<String, Object> {
    private static final long serialVersionUID = -4554179472658772285L;

    private Map<String, CalculationSubItemResult> subItemResults = new LinkedHashMap<>();

    @Override
    public Map<String, CalculationSubItemResult> getSubItemResults() {
        return subItemResults;
    }

    @Override
    public CalculationItemResult addSubItemResult(String subItemKey, String key, Object value) {
        CalculationSubItemResult itemResult = subItemResults.get(subItemKey);

        if (itemResult == null) {
            // itemResult = app.getInjectable(CalculationSubItemResult.class);
            itemResult = new DefaultCalculationSubItemResult();
            subItemResults.put(subItemKey, itemResult);
        }
        itemResult.put(key, value);

        return this;
    }

    @Override
    public Integer getInteger(String key) {
        Object value = super.get(key);

        if (value == null)
            return null;

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).intValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        }

        return Integer.parseInt(value.toString());
    }

    @Override
    public Double getDouble(String key) {
        Object value = super.get(key);

        if (value == null)
            return null;

        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).doubleValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }

        return Double.parseDouble(value.toString());
    }
}
