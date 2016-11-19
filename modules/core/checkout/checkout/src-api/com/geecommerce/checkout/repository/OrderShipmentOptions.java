package com.geecommerce.checkout.repository;

import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentOption;
import com.geecommerce.core.service.api.Repository;

import java.util.List;

public interface OrderShipmentOptions extends Repository {
    public List<OrderShipmentOption> thatBelongTo(OrderShipment orderShipment);
}
