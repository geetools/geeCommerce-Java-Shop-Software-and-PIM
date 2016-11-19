package com.geecommerce.calculation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Injectable;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Injectable
public class DefaultCalculationContext implements CalculationContext {
    private static final long serialVersionUID = -1144881953728541220L;

    @Inject
    protected App app;

    protected Map<String, ContextObject<Object>> configuration = new HashMap<>();
    protected Map<String, Object> parameters = new HashMap<>();
    protected List<Map<String, Object>> items = new ArrayList<>();
    protected Map<Id, Map<String, Object>> itemDiscounts = new HashMap<>();

    protected Map<Id, CalculationItemResult> itemResults = new HashMap<>();
    protected Map<String, Object> results = new HashMap<>();

    @Override
    public CalculationContext addParameter(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    @Override
    public CalculationContext addConfigurationProperty(String key, ContextObject<Object> value) {
        this.configuration.put(key, value);
        return this;
    }

    @Override
    public String cpStr_(String key) {
        return configuration.get(key) == null ? null : (String) configuration.get(key).getStr();
    }

    @Override
    public Double cpDouble_(String key) {
        return configuration.get(key) == null ? null : (Double) configuration.get(key).getDouble();
    }

    @Override
    public Integer cpInt_(String key) {
        return configuration.get(key) == null ? null : (Integer) configuration.get(key).getInteger();
    }

    @Override
    public Boolean cpBool_(String key) {
        return configuration.get(key) == null ? null : (Boolean) configuration.get(key).getBoolean();
    }

    @Override
    public String str_(String key) {
        return (String) parameters.get(key);
    }

    @Override
    public Double double_(String key) {
        return (Double) parameters.get(key);
    }

    @Override
    public Integer int_(String key) {
        return (Integer) parameters.get(key);
    }

    @Override
    public Boolean bool_(String key) {
        return (Boolean) parameters.get(key);
    }

    @Override
    public Object obj_(String key) {
        return parameters.get(key);
    }

    @Override
    public List<Map<String, Object>> getItems() {
        return items;
    }

    @Override
    public CalculationContext setItems(List<Map<String, Object>> items) {
        this.items = items;
        return this;
    }

    @Override
    public Map<Id, Map<String, Object>> getItemDiscounts() {
        return itemDiscounts;
    }

    @Override
    public CalculationContext setItemDiscounts(Map<Id, Map<String, Object>> itemDiscounts) {
        this.itemDiscounts = itemDiscounts;
        return this;
    }

    @Override
    public CalculationContext addItem(Map<String, Object> item) {
        this.items.add(item);
        return this;
    }

    @Override
    public Map<Id, CalculationItemResult> getItemResults() {
        return itemResults;
    }

    @Override
    public CalculationContext addItemResult(Id productId, String key, Object value) {
        CalculationItemResult itemResult = itemResults.get(productId);

        if (itemResult == null) {
            itemResult = app.getInjectable(CalculationItemResult.class);
            itemResults.put(productId, itemResult);
        }

        itemResult.put(key, value);

        return this;
    }

    @Override
    public CalculationContext addSubItemResult(Id productId, String subItemKey, String key, Object value) {
        CalculationItemResult itemResult = itemResults.get(productId);

        if (itemResult == null) {
            itemResult = app.getInjectable(CalculationItemResult.class);
            itemResults.put(productId, itemResult);
        }

        itemResult.addSubItemResult(subItemKey, key, value);
        return this;
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public CalculationContext setResults(Map<String, Object> results) {
        this.results = results;
        return this;
    }
}
