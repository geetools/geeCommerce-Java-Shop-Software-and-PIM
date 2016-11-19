package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultOrderItems extends AbstractRepository implements OrderItems {
    @Override
    public List<OrderItem> thatBelongTo(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderItem.Column.ORDER_ID, order.getId());

        List<OrderItem> orderItems = find(OrderItem.class, filter);

        return orderItems;
    }
}
