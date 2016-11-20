package com.geecommerce.customer.helper;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

@Helper
public class DefaultViewedProductHelper implements ViewedProductHelper {
    @Inject
    protected App app;

    protected static final String VIEWED_PRODUCTS_KEY = "_cb_vp";

    protected static final int STORE_NUM_VIEWED_PRODUCTS = 10;

    @Override
    public void rememberViewedProduct(Id productId) {
        // Get currently stored viewed products from cookie.
        List<Id> viewedProductIds = getViewedProductIds();

        // Remove productId in case it already exists in list.
        viewedProductIds.remove(productId);

        // Add it to the beginning of the list.
        viewedProductIds.add(0, productId);

        // Update the cookie.
        app.cookieSet(VIEWED_PRODUCTS_KEY,
            Joiner.on('|').join(viewedProductIds.subList(0, viewedProductIds.size() > STORE_NUM_VIEWED_PRODUCTS
                ? STORE_NUM_VIEWED_PRODUCTS : viewedProductIds.size())));
    }

    @Override
    public List<Id> getViewedProductIds() {
        List<Id> ids = null;

        String idStr = app.cookieGet(VIEWED_PRODUCTS_KEY);

        if (idStr != null && !"".equals(idStr.trim())) {
            ids = Lists.newArrayList(Id.toIds(idStr, "\\|"));
        } else {
            ids = new ArrayList<>();
        }

        return ids;
    }
}
