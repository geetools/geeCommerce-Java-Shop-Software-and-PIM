package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.dao.OrderAddressDao;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Repository
public class DefaultOrderAddresses extends AbstractRepository implements OrderAddresses {
    private final OrderAddressDao dao;

    @Inject
    public DefaultOrderAddresses(OrderAddressDao dao) {
        this.dao = dao;
    }

    @Override
    public Dao dao() {
        return this.dao;
    }

    @Override
    public List<OrderAddress> thatBelongTo(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderAddress.Column.ORDER_ID, order.getId());

        List<OrderAddress> orderAddresses = find(OrderAddress.class, filter);

        return orderAddresses;
    }

    @Override
    public OrderAddress lastInvoiceAddress(Id customerId) {
        if (customerId == null)
            return null;

        return dao.getLastInvoiceAddress(customerId);
    }

    @Override
    public OrderAddress lastTransferredInvoiceAddress(Id customerId) {
        if (customerId == null)
            return null;

        return dao.getLastTransferredInvoiceAddress(customerId);
    }
}
