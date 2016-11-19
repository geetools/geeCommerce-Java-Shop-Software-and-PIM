package com.geecommerce.calculation.model;

import com.geecommerce.core.service.annotation.Injectable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

@Injectable
public class DefaultCalculationSubItemResult extends LinkedHashMap<String, Object> implements CalculationSubItemResult, Map<String, Object> {
    private static final long serialVersionUID = -4554179472658772285L;

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
