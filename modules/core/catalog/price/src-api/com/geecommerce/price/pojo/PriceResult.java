package com.geecommerce.price.pojo;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.price.model.Price;

public interface PriceResult extends Pojo {
    public PriceResult init(List<Price> prices, List<Price> childPrices);

    public Double getPrice(String type);

    public Double getPrice(String type, Integer qty);

    public Double getPrice(String type, Integer forQty, PricingContext pricingCtx);

    public Double getFinalPrice();

    public Double getFinalPrice(PricingContext pricingCtx);

    public Double getFinalPrice(Integer forQty);

    public Double getFinalPrice(Integer forQty, PricingContext pricingCtx);

    public Price getFinalPriceFor(Integer qty);

    public Price getFinalPriceFor(Integer qty, PricingContext pricingCtx);

    public Double getLowestFinalPrice();

    public Double getLowestFinalPrice(Integer forQty);

    public Double getLowestFinalPrice(PricingContext pricingCtx);

    public Double getLowestFinalPrice(Integer forQty, PricingContext pricingCtx);

    Price getLowestFinalPriceFor();

    public Price getLowestFinalPriceFor(Integer qty);

    public Price getLowestFinalPriceFor(PricingContext pricingCtx);

    public Price getLowestFinalPriceFor(Integer qty, PricingContext pricingCtx);

    public Double getHighestFinalPrice();

    public Double getHighestFinalPrice(PricingContext pricingCtx);

    public Double getHighestFinalPrice(Integer forQty);

    public Double getHighestFinalPrice(Integer forQty, PricingContext pricingCtx);

    public Price getHighestFinalPriceFor(Integer qty);

    public Price getHighestFinalPriceFor(Integer qty, PricingContext pricingCtx);

    public Map<String, Double> getValidPrices();

    public Map<String, Double> getValidPrices(PricingContext pricingCtx);

    public Map<String, Double> getValidPrices(Integer forQty);

    public Map<String, Double> getValidPrices(Integer forQty, PricingContext pricingCtx);

    public Map<String, Double> getLowestValidPrices();

    public Map<String, Double> getLowestValidPrices(PricingContext pricingCtx);

    public Map<String, Double> getLowestValidPrices(Integer forQty);

    public Map<String, Double> getLowestValidPrices(Integer forQty, PricingContext pricingCtx);

    public boolean hasMultipleFinalPrices();

    public boolean hasAnyValidPrice();

    public boolean hasValidPrice();
}
