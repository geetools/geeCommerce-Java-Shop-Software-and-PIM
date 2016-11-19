package com.geecommerce.cart.model;

import com.geecommerce.core.service.api.Injectable;

public interface ProductAvailability extends Injectable {
    public String getName();

    public void setName(String name);

    public String getStatus();

    public void setStatus(String status);

    public Boolean getIsAvailable();

    public void setIsAvailable(boolean isAvailable);
}
