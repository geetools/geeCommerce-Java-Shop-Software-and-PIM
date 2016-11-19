package com.geecommerce.customer.helper;

import java.util.List;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.Id;

public interface ViewedProductHelper extends Helper {
    public void rememberViewedProduct(Id productId);

    public List<Id> getViewedProductIds();
}
