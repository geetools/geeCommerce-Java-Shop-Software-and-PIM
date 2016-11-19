package com.geecommerce.checkout.repository;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.core.service.api.Repository;

public interface OrderPayments extends Repository {
    public OrderPayment thatBelongTo(Order order);
}
