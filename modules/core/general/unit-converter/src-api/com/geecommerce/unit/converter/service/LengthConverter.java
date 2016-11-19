package com.geecommerce.unit.converter.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.unit.converter.enums.LengthUnit;

public interface LengthConverter extends Service {
    Double convert(Double value, LengthUnit from, LengthUnit to);

    Double convert(Double value, LengthUnit to);

    LengthUnit defaultUnit();
}
