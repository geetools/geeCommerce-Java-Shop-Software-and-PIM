package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.core.service.api.Repository;

public interface OrderShipments extends Repository {
    public List<OrderShipment> thatBelongTo(Order order);
}
