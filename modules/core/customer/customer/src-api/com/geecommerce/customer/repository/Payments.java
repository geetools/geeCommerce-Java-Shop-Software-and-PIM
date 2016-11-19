package com.geecommerce.customer.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.Payment;

import java.util.List;

public interface Payments extends Repository {
    public List<Payment> thatBelongTo(Customer customer);
}
