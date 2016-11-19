package com.geecommerce.checkout.repository;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.core.service.api.Repository;

import java.util.List;

public interface OrderShipmentItems extends Repository {
    public List<OrderShipmentItem> thatBelongTo(OrderShipment orderShipment);
}
