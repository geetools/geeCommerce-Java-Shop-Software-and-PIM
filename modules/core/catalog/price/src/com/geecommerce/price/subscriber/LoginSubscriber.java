package com.geecommerce.price.subscriber;

import com.geecommerce.core.message.Context;
import com.geecommerce.core.message.Subscriber;
import com.geecommerce.core.message.annotation.Subscribe;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.price.helper.PriceHelper;
import com.geecommerce.price.pojo.PricingContext;
import com.google.inject.Inject;

@Subscribe("customer:logged-in")
public class LoginSubscriber implements Subscriber {
    private final PriceHelper priceHelper;

    @Inject
    public LoginSubscriber(PriceHelper priceHelper) {
        this.priceHelper = priceHelper;
    }

    @Override
    public void onMessage(Context ctx) {
        Customer c = (Customer) ctx.get("customer");

        PricingContext pricingCtx = priceHelper.getPricingContext();

        pricingCtx.setCustomerId(c.getId()).setCustomerGroupIds(c.getCustomerGroupIds());
    }
}
