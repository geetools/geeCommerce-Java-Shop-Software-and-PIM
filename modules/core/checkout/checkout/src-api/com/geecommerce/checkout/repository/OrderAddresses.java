package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

public interface OrderAddresses extends Repository {
    public List<OrderAddress> thatBelongTo(Order order);

    public OrderAddress lastInvoiceAddress(Id customerId);

    public OrderAddress lastTransferredInvoiceAddress(Id customerId);
}
