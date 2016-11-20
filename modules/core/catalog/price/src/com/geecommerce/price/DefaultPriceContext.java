package com.geecommerce.price;

import com.geecommerce.core.service.annotation.Injectable;
import com.geecommerce.core.type.Id;

@Injectable
public class DefaultPriceContext implements PriceContext {
    private static final long serialVersionUID = 3966201217054398980L;
    private Id customerId = null;
    private Id customerGroupId = null;
    private Id requestContextId = null;
    private Integer quantity = null;

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public PriceContext forCustomer(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    public Id getCustomerGroupId() {
        return customerGroupId;
    }

    @Override
    public PriceContext inCustomerGroup(Id customerGroupId) {
        this.customerGroupId = customerGroupId;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public PriceContext inRequestContext(Id requestContextId) {
        this.requestContextId = requestContextId;
        return this;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public PriceContext forQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
