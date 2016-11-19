package com.geecommerce.price.dao;

import java.util.Map;

import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.type.Id;

public interface PriceDao extends Dao {
    public Map<String, Object> getPriceData(Id productId, Id requestCtxId);
}
