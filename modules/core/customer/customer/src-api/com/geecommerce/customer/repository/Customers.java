package com.geecommerce.customer.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.customer.model.Customer;

public interface Customers extends Repository {
    public List<Customer> thatBelongTo(Store store);

    public Customer withEmail(String email);
}
