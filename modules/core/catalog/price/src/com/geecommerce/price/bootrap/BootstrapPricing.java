package com.geecommerce.price.bootrap;

import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.bootstrap.AbstractBootstrap;
import com.geecommerce.core.bootstrap.annotation.Bootstrap;
import com.geecommerce.price.pojo.PricingContext;

@Bootstrap
public class BootstrapPricing extends AbstractBootstrap {
    private static final String DEFAULT_PRICING_CONTEXT_KEY = "defaultPricingCtx";

    @Override
    public void init() {
        HttpServletRequest request = App.get().getServletRequest();

        PricingContext defaultPricingCtx = App.get().getPojo(PricingContext.class);

        request.setAttribute(DEFAULT_PRICING_CONTEXT_KEY, defaultPricingCtx);
    }
}
