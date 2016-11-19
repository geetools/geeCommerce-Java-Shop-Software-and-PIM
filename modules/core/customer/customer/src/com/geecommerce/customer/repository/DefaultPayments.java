package com.geecommerce.customer.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.Payment;

@Repository
public class DefaultPayments extends AbstractRepository implements Payments {
    @Override
    public List<Payment> thatBelongTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.CUSTOMER_ID, customer.getId());

        return find(Payment.class, filter, QueryOptions.builder().sortBy(GlobalColumn.ID).build());
    }

}
