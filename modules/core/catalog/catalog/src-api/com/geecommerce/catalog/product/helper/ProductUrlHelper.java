package com.geecommerce.catalog.product.helper;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;

public interface ProductUrlHelper extends Helper {

    public void generateUniqueUri(Product product, UrlRewrite urlRewrite, boolean empty);

}
