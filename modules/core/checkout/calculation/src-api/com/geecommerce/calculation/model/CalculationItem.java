package com.geecommerce.calculation.model;

import com.geecommerce.core.type.Id;
import com.geecommerce.price.pojo.PricingContext;

import java.util.List;
import java.util.Map;

public interface CalculationItem {
    public Map<String, Object> toCalculationItem();

    public Map<String, Object> toCalculationItem(PricingContext pricingContext);

    static final class FIELD {
        public static final String ITEM_ARTICLE_ID = "article_id";
        public static final String ITEM_QUANTITY = "qty";
        public static final String ITEM_BASE_CALCULATION_PRICE = "base_calculation_price";
        public static final String ITEM_BASE_CALCULATION_PRICE_TYPE = "base_calculation_price_type";
        public static final String ITEM_TAX_RATE = "tax_rate";
    }
}
