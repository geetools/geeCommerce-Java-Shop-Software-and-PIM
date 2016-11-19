package com.geecommerce.catalog.product.repository;

import java.util.List;

import com.geecommerce.catalog.product.model.ProductLinkType;
import com.geecommerce.core.service.api.Repository;

public interface ProductLinkTypes extends Repository {
    public ProductLinkType thatBelongTo(String code);

    public List<ProductLinkType> thatBelongTo();
}
