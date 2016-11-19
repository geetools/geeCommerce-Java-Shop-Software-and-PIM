package com.geecommerce.catalog.product.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.ProductLinkType;
import com.geecommerce.catalog.product.model.ProductLinkType.Column;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultProductLinkTypes extends AbstractRepository implements ProductLinkTypes {
    @Override
    public ProductLinkType thatBelongTo(String code) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Column.CODE, code);
        return findOne(ProductLinkType.class, filter);
    }

    @Override
    public List<ProductLinkType> thatBelongTo() {
        return find(ProductLinkType.class, null);
    }
}
