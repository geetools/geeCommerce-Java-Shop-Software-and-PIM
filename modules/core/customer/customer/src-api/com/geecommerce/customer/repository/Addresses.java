package com.geecommerce.customer.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;

public interface Addresses extends Repository {
    public List<Address> thatBelongTo(Customer customer);
}
