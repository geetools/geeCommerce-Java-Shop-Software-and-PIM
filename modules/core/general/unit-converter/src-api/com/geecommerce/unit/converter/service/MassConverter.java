package com.geecommerce.unit.converter.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.unit.converter.enums.MassUnit;

public interface MassConverter extends Service {
    Double convert(Double value, MassUnit from, MassUnit to);

    Double convert(Double value, MassUnit to);

    MassUnit defaultUnit();
}
