package com.geecommerce.price.helper;

import java.util.List;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.pojo.PricingContext;

public interface PriceHelper extends Helper {
    public PricingContext getPricingContext();

    public PricingContext getPricingContext(boolean createIfNotExists);

    public List<Price> filterPrices(List<Price> pricesToFilter, Id... forProductIds);
}
