package com.geecommerce.customer.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.customer.model.Customer;

@Repository
public class DefaultCustomers extends AbstractRepository implements Customers {
    @Override
    public List<Customer> thatBelongTo(Store store) {
        return null;
    }

    @Override
    public Customer withEmail(String email) {
        if (email == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(Customer.Col.EMAIL, email);

        return findOne(Customer.class, filter);
    }
}
