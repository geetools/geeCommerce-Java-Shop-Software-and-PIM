package com.geecommerce.calculation.model;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CalculationContext extends Injectable {
    public CalculationContext addParameter(String key, Object value);

    public CalculationContext addConfigurationProperty(String key, ContextObject<Object> value);

    public String cpStr_(String key);

    public Double cpDouble_(String key);

    public Integer cpInt_(String key);

    public Boolean cpBool_(String key);

    public String str_(String key);

    public Double double_(String key);

    public Integer int_(String key);

    public Boolean bool_(String key);

    public Object obj_(String key);

    public List<Map<String, Object>> getItems();

    public CalculationContext setItems(List<Map<String, Object>> items);

    public Map<Id, Map<String, Object>> getItemDiscounts();

    public CalculationContext setItemDiscounts(Map<Id, Map<String, Object>> items);

    public CalculationContext addItem(Map<String, Object> item);

    public Map<Id, CalculationItemResult> getItemResults();

    public CalculationContext addItemResult(Id productId, String key, Object value);

    public CalculationContext addSubItemResult(Id productId, String subItemKey, String key, Object value);

    public Map<String, Object> getResults();

    public CalculationContext setResults(Map<String, Object> results);
}
