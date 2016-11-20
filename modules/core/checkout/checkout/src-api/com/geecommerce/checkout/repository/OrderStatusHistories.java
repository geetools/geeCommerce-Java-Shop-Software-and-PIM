package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderStatusHistory;
import com.geecommerce.core.service.api.Repository;

public interface OrderStatusHistories extends Repository {
    public List<OrderStatusHistory> thatBelongTo(Order order);

    public OrderStatusHistory getHistoryForStatus(Order order, OrderStatus orderStatus);

    public OrderStatusHistory getLastStatusHistory(Order order);

}
