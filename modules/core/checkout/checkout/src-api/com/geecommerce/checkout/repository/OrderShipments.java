package com.geecommerce.checkout.repository;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.core.service.api.Repository;

import java.util.List;

public interface OrderShipments extends Repository {
    public List<OrderShipment> thatBelongTo(Order order);
}
