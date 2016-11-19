package com.geecommerce.calculation.model;

import java.util.Map;

public interface CalculationData {
    public Map<String, Object> toCalculationData();

    static final class FIELD {
	public static final String ITEMS = "items";
    }
}
