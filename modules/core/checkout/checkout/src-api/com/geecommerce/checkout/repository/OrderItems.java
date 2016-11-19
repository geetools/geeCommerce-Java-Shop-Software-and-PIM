package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.core.service.api.Repository;

public interface OrderItems extends Repository {
    public List<OrderItem> thatBelongTo(Order order);
}
