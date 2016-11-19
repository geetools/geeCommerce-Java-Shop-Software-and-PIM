package com.geecommerce.calculation.model;

import java.util.Map;

public interface CalculationItem {
    public Map<String, Object> toCalculationItem();

    static final class FIELD {
	public static final String ITEM_ARTICLE_ID = "article_id";
	public static final String ITEM_QUANTITY = "qty";
	public static final String ITEM_BASE_CALCULATION_PRICE = "base_calculation_price";
	public static final String ITEM_BASE_CALCULATION_PRICE_TYPE = "base_calculation_price_type";
	public static final String ITEM_TAX_RATE = "tax_rate";
    }
}
