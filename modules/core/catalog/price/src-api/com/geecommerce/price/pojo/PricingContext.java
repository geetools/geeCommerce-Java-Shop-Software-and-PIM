package com.geecommerce.price.pojo;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;

public interface PricingContext extends Pojo {
    public static final String SESSION_KEY = "gc.price.ctx";

    public Id getCustomerId();

    public PricingContext setCustomerId(Id customerId);

    public List<Id> getCustomerGroupIds();

    public PricingContext setCustomerGroupIds(List<Id> customerGroupIds);

    public String getCurrency();

    public PricingContext setCurrency(String currency);

    public Set<String> getPriceTypesToInclude();

    public PricingContext setPriceTypesToInclude(List<PriceType> priceTypesToInclude);

    public PricingContext addPriceTypeToInclude(PriceType priceType);

    public PricingContext removePriceTypeToInclude(PriceType priceType);

    public Set<String> getPriceTypesToExclude();

    public PricingContext setPriceTypesToExclude(List<PriceType> priceTypesToExclude);

    public PricingContext addPriceTypeToExclude(PriceType priceType);

    public PricingContext removePriceTypeToExclude(PriceType priceType);

    public boolean isPriceAvailable(Price price);
}
