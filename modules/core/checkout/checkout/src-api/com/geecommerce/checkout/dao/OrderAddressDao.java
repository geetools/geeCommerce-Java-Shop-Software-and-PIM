package com.geecommerce.checkout.dao;

import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.type.Id;

public interface OrderAddressDao extends Dao {
    public OrderAddress getLastInvoiceAddress(Id customerId);

    public OrderAddress getLastTransferredInvoiceAddress(Id customerId);
}
