package com.geecommerce.price.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.pojo.PriceResult;

public interface PriceService extends Service {
    public Price getPrice(Id priceId);

    public List<Price> getPrices(Id productId);

    public List<Price> getPrices(Id... productId);

    public PriceResult getPriceFor(Id productId, String currencyCode);

    public PriceResult getPriceFor(Id productId, String currencyCode, Id... childProductIds);

    public Map<Id, PriceResult> getPricesFor(Map<Id, Id[]> productIdMap, String currencyCode);

    public Price createPrice(Price price);

    public void updatePrice(Price price);

    public void removePrice(Price price);
}
