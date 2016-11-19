package com.geecommerce.price.repository;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;

public interface Prices extends Repository {
    public Map<String, Object> getPriceData(Id productId, RequestContext requestCtx);

    public List<Price> belongingToProduct(Id productId, String currencyCode);

    public List<Price> belongingToProducts(Id[] productIds, String currencyCode);

    public List<Price> belongingToProduct(Id productId);

    public List<Price> belongingToProducts(Id... productIds);
}
