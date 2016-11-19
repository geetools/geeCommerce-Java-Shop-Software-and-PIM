package com.geecommerce.unit.converter.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.unit.converter.enums.DataAmountUnit;

public interface DataAmountConverter extends Service {
    Double convert(Double value, DataAmountUnit from, DataAmountUnit to);

    Double convert(Double value, DataAmountUnit to);

    DataAmountUnit defaultUnit();
}
