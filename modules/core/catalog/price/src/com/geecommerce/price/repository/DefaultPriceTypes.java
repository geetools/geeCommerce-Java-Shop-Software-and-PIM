package com.geecommerce.price.repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.price.model.PriceType;

@Repository
public class DefaultPriceTypes extends AbstractRepository implements PriceTypes {
    @Override
    public Map<String, PriceType> priceTypes() {
        List<PriceType> priceTypes = multiContextFind(PriceType.class, new HashMap<String, Object>(),
            PriceType.Col.CODE, QueryOptions.builder().sortBy(PriceType.Col.PRIORITY).build());

        Map<String, PriceType> priceTypeMap = new LinkedHashMap<>();

        if (priceTypes != null && priceTypes.size() > 0) {
            for (PriceType priceType : priceTypes) {
                priceTypeMap.put(priceType.getCode(), priceType);
            }
        }

        return priceTypeMap;
    }

    @Override
    public PriceType havingCode(String code) {
        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put(PriceType.Col.CODE, code);

        return multiContextFindOne(PriceType.class, filter);
    }
}
