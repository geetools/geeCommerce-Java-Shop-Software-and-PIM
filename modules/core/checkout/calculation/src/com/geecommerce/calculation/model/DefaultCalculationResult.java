package com.geecommerce.calculation.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.geecommerce.core.service.annotation.Injectable;
import com.geecommerce.core.type.Id;

@Injectable
public class DefaultCalculationResult extends LinkedHashMap<String, Object>
    implements CalculationResult, Map<String, Object> {
    private static final long serialVersionUID = -8049048059067567450L;

    @Override
    public CalculationItemResult getItemResult(String productId) {
        return getItemResults().get(Id.parseId(productId));
    }

    @Override
    public CalculationItemResult getItemResult(Id productId) {
        return getItemResults().get(productId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Id, CalculationItemResult> getItemResults() {
        return (Map<Id, CalculationItemResult>) super.get("itemResults");
    }

    @Override
    public CalculationResult setItemResults(Map<Id, CalculationItemResult> itemResults) {
        for (CalculationItemResult itemResult : itemResults.values()) {
            itemResult.put("subItemResults", itemResult.getSubItemResults());
        }
        super.put("itemResults", itemResults);
        return this;
    }

    @Override
    public Map<String, Object> getResults() {
        return this;
    }

    @Override
    public CalculationResult setResults(Map<String, Object> results) {
        this.putAll(results);
        return this;
    }

    @Override
    public CalculationResult fromJSON(String json) throws JSONException {
        if (json == null)
            return null;

        JSONObject jsonObject = new JSONObject(json);

        Iterator<String> it = jsonObject.keys();

        while (it.hasNext()) {
            String key = it.next();

            if ("itemResults".equals(key)) {
                Map<Id, CalculationItemResult> itemResults = new LinkedHashMap<>();

                JSONObject itemResult = jsonObject.getJSONObject("itemResults");

                Iterator<String> itemResultKeys = itemResult.keys();

                // Iterate though outer-most items. Typically this contains the
                // cart-totals and cart-items.
                while (itemResultKeys.hasNext()) {
                    String itemResultKey = itemResultKeys.next();

                    // Item data is stored in a map and the key is the
                    // productId.
                    Id productId = Id.valueOf(itemResultKey);

                    // Get the item qty and calculated totals.
                    JSONObject itemResultData = itemResult.getJSONObject(itemResultKey);

                    Iterator<String> itemResultDataKeys = itemResultData.keys();

                    // We store the item data in a CalculationItemResult object.
                    // CalculationItemResult calcItemResult =
                    // app.getInjectable(CalculationItemResult.class);
                    CalculationItemResult calcItemResult = new DefaultCalculationItemResult();

                    while (itemResultDataKeys.hasNext()) {
                        String itemResultDataKey = itemResultDataKeys.next();

                        if ("article_id".equals(itemResultDataKey)) {
                            calcItemResult.put(itemResultDataKey, Id.valueOf(itemResultData.get(itemResultDataKey)));
                        } else if ("qty".equals(itemResultDataKey)) {
                            calcItemResult.put(itemResultDataKey, itemResultData.getInt(itemResultDataKey));
                        } else if ("subItemResults".equals(itemResultDataKey)) {
                            JSONObject subItemResult = itemResultData.getJSONObject("subItemResults");

                            Iterator<String> subItemResultKeys = subItemResult.keys();

                            while (subItemResultKeys.hasNext()) {
                                String calcType = subItemResultKeys.next();

                                JSONObject subItemResultData = subItemResult.getJSONObject(calcType);

                                Iterator<String> subItemResultDataKeys = subItemResultData.keys();

                                while (subItemResultDataKeys.hasNext()) {
                                    String subItemResultDataKey = subItemResultDataKeys.next();

                                    if ("qty".equals(subItemResultDataKey)) {
                                        calcItemResult.addSubItemResult(calcType, subItemResultDataKey,
                                            subItemResultData.getInt(subItemResultDataKey));
                                    } else if ("price_type".equals(subItemResultDataKey)) {
                                        calcItemResult.addSubItemResult(calcType, subItemResultDataKey,
                                            subItemResultData.getString(subItemResultDataKey));
                                    } else {
                                        calcItemResult.addSubItemResult(calcType, subItemResultDataKey,
                                            subItemResultData.getDouble(subItemResultDataKey));
                                    }
                                }
                            }
                        } else {
                            calcItemResult.put(itemResultDataKey, itemResultData.getDouble(itemResultDataKey));
                        }
                    }

                    // Hack to make it visible for json conversion.
                    calcItemResult.put("subItemResults", calcItemResult.getSubItemResults());

                    itemResults.put(productId, calcItemResult);
                }

                put("itemResults", itemResults);
            } else {
                put(key, jsonObject.getDouble(key));
            }
        }

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
