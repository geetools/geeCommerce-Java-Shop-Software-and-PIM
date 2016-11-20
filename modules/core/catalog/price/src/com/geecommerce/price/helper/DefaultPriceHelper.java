package com.geecommerce.price.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.pojo.PricingContext;
import com.google.inject.Inject;

@Helper
public class DefaultPriceHelper implements PriceHelper {
    @Inject
    protected App app;

    @Override
    public PricingContext getPricingContext() {
        return getPricingContext(true);
    }

    @Override
    public PricingContext getPricingContext(boolean createIfNotExists) {
        PricingContext pricingCtx = app.sessionGet(PricingContext.SESSION_KEY);

        if (pricingCtx == null && createIfNotExists) {
            pricingCtx = app.pojo(PricingContext.class);

            if (!app.isInObserverThread() && app.servletRequest() != null && !app.isAPIRequest())
                app.sessionSet(PricingContext.SESSION_KEY, pricingCtx);
        }

        return pricingCtx;
    }

    @Override
    public List<Price> filterPrices(List<Price> pricesToFilter, Id... forProductIds) {
        List<Price> filteredPrices = new ArrayList<>();

        List<Id> productIds = Arrays.asList(forProductIds);

        for (Price price : pricesToFilter) {
            if (productIds.contains(price.getProductId())) {
                filteredPrices.add(price);
            }
        }

        return filteredPrices;
    }
}
