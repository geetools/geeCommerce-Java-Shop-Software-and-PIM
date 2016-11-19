package com.geecommerce.price;

import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.type.Id;

public interface PriceContext extends Injectable {
    public Id getCustomerId();

    public PriceContext forCustomer(Id customerId);

    public Id getCustomerGroupId();

    public PriceContext inCustomerGroup(Id customerGroupId);

    public Id getRequestContextId();

    public PriceContext inRequestContext(Id requestContextId);

    public Integer getQuantity();

    public PriceContext forQuantity(Integer quantity);
}
