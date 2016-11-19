package com.geecommerce.customer.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.ViewedProduct;

public interface ViewedProducts extends Repository {
    public List<ViewedProduct> thatBelongTo(Customer customer, int limit);
}
