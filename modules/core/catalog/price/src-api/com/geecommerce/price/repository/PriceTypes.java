package com.geecommerce.price.repository;

import java.util.Map;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.price.model.PriceType;

public interface PriceTypes extends Repository {
    public Map<String, PriceType> priceTypes();

    public PriceType havingCode(String code);
}
