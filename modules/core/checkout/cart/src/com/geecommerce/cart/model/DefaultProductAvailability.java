package com.geecommerce.cart.model;

import com.geecommerce.core.service.annotation.Injectable;

@Injectable
public class DefaultProductAvailability implements ProductAvailability {
    private String name = null;
    private String status = null;
    private Boolean available = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Boolean getIsAvailable() {
        return available;
    }

    @Override
    public void setIsAvailable(boolean isAvailable) {
        this.available = isAvailable;
    }
}
