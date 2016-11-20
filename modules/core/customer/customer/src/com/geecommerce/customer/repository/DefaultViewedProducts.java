package com.geecommerce.customer.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.ViewedProduct;

@Repository
public class DefaultViewedProducts extends AbstractRepository implements ViewedProducts {
    @Override
    public List<ViewedProduct> thatBelongTo(Customer customer, int limit) {
        if (customer == null || customer.getId() == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(ViewedProduct.Column.CUSTOMER_ID, customer.getId());

        return find(ViewedProduct.class, filter,
            QueryOptions.builder().limitTo(limit).sortByDesc(ViewedProduct.Column.VIEWED_ON).build());
    }
}
