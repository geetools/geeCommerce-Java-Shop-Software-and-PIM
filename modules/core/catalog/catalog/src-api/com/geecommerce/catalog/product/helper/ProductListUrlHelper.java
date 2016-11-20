package com.geecommerce.catalog.product.helper;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;

public interface ProductListUrlHelper extends Helper {

    public void generateUniqueUri(ProductList productList, UrlRewrite urlRewrite, boolean empty);

}
