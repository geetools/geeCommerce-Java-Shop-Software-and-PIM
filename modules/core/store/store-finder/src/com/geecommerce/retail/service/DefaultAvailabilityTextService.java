package com.geecommerce.retail.service;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.AvailabilityText;
import com.geecommerce.retail.repository.AvailabilityTexts;
import com.google.inject.Inject;

@Service
public class DefaultAvailabilityTextService implements AvailabilityTextService {
    private final AvailabilityTexts availabilityTexts;

    @Inject
    public DefaultAvailabilityTextService(AvailabilityTexts availabilityTexts) {
        this.availabilityTexts = availabilityTexts;
    }

    @Override
    public AvailabilityText createAvailabilityText(AvailabilityText availabilityText) {
        return availabilityTexts.add(availabilityText);
    }

    @Override
    public void update(AvailabilityText availabilityText) {
        availabilityTexts.update(availabilityText);
    }

    @Override
    public AvailabilityText getAvailabilityText(Id id) {
        return availabilityTexts.findById(AvailabilityText.class, id);
    }
}
