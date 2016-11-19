package com.geecommerce.retail.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.AvailabilityText;

public interface AvailabilityTextService extends Service {
    public AvailabilityText createAvailabilityText(AvailabilityText availabilityText);

    public void update(AvailabilityText availabilityText);

    public AvailabilityText getAvailabilityText(Id id);
}
