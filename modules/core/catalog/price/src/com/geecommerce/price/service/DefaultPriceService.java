package com.geecommerce.price.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.helper.PriceHelper;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.price.repository.Prices;
import com.google.inject.Inject;

@Service
public class DefaultPriceService implements PriceService {
    @Inject
    protected App app;

    protected final Prices prices;
    protected final PriceHelper priceHelper;

    @Inject
    public DefaultPriceService(Prices prices, PriceHelper priceHelper) {
        this.prices = prices;
        this.priceHelper = priceHelper;
    }

    @Override
    public Price getPrice(Id priceId) {
        return prices.findById(Price.class, priceId);
    }

    @Override
    public List<Price> getPrices(Id productId) {
        return prices.belongingToProduct(productId);
    }

    @Override
    public List<Price> getPrices(Id... productIds) {
        return prices.belongingToProducts(productIds);
    }

    @Override
    public PriceResult getPriceFor(Id productId, String currencyCode) {
        return getPriceFor(productId, currencyCode, (Id[]) null);
    }

    @Override
    public PriceResult getPriceFor(Id productId, String currencyCode, Id... childProductIds) {
        if (productId == null || currencyCode == null)
            throw new IllegalArgumentException("The parameters productId and currencyCode cannot be null when getting prices.");

        List<Price> productPrices = prices.belongingToProduct(productId, currencyCode);
        List<Price> childProductPrices = null;

        if (childProductIds != null && childProductIds.length > 0)
            childProductPrices = prices.belongingToProducts(childProductIds);

        PriceResult priceResult = app.getPojo(PriceResult.class).init(productPrices, childProductPrices);

        return priceResult;
    }

    @Override
    public Map<Id, PriceResult> getPricesFor(Map<Id, Id[]> productIdMap, String currencyCode) {
        if (productIdMap == null || productIdMap.size() == 0 || currencyCode == null)
            throw new IllegalArgumentException("The parameters productIdMap and currencyCode cannot be null");

        // long start = System.currentTimeMillis();

        Set<Id> parentProductIds = productIdMap.keySet();

        // Collect all of the productIds, so that we can get all the prices in
        // one go.
        Set<Id> allProductIds = new HashSet<>();
        allProductIds.addAll(parentProductIds);

        for (Id parentProductId : parentProductIds) {
            Id[] childProductIds = productIdMap.get(parentProductId);
            allProductIds.addAll(Arrays.asList(childProductIds));
        }

        // System.out.println(" pppp#1: " + (System.currentTimeMillis() -
        // start));

        // Fetch the prices for all the collected productIds.
        List<Price> allProductPrices = prices.belongingToProducts(allProductIds.toArray(new Id[allProductIds.size()]), currencyCode);

        // System.out.println(" pppp#2: " + (System.currentTimeMillis() -
        // start));

        // Now that we have all the prices we filter the complete list, so that
        // we have
        // the relations that were originally past to this method. We can then
        // create a
        // PriceResult object for each product.
        Map<Id, PriceResult> priceResults = new HashMap<>();

        for (Id parentProductId : parentProductIds) {
            priceResults.put(parentProductId,
                app.getPojo(PriceResult.class).init(priceHelper.filterPrices(allProductPrices, parentProductId), priceHelper.filterPrices(allProductPrices, productIdMap.get(parentProductId))));
        }

        // System.out.println(" pppp#end: " + (System.currentTimeMillis() -
        // start));

        // System.out.println("*** preload prices took: " +
        // (System.currentTimeMillis() - start));

        return priceResults;
    }

    @Override
    public Price createPrice(Price price) {
        return prices.add(price);
    }

    @Override
    public void updatePrice(Price price) {
        prices.update(price);
    }

    @Override
    public void removePrice(Price price) {
        prices.remove(price);
    }
}
