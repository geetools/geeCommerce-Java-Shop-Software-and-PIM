package com.geecommerce.customer.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.Payment;

public interface Payments extends Repository {
    public List<Payment> thatBelongTo(Customer customer);
}
