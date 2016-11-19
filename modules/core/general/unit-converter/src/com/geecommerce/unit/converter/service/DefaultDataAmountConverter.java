package com.geecommerce.unit.converter.service;

import static com.geecommerce.unit.converter.enums.DataAmountUnit.UNSUPPORTED;

import javax.measure.quantity.DataAmount;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.unit.converter.configuration.Key;
import com.geecommerce.unit.converter.enums.DataAmountUnit;
import com.google.inject.Inject;

@Service
public class DefaultDataAmountConverter implements DataAmountConverter {
    @Inject
    protected App app;

    @Override
    public Double convert(Double value, DataAmountUnit from, DataAmountUnit to) {
        if (value == null)
            return null;

        if (from.equals(UNSUPPORTED) || to.equals(UNSUPPORTED))
            return null;

        Unit fUnit = get(from);
        Unit tUnit = get(to);

        if (fUnit == null || tUnit == null)
            return null;

        Amount<DataAmount> m = Amount.valueOf(value, fUnit);
        return m.doubleValue(tUnit);
    }

    @Override
    public Double convert(Double value, DataAmountUnit to) {
        DataAmountUnit from = defaultUnit();
        return convert(value, from, to);
    }

    @Override
    public DataAmountUnit defaultUnit() {
        return DataAmountUnit.valueOf(app.cpStr_(Key.DATA_AMOUNT));
    }

    private Unit get(DataAmountUnit massUnit) {
        switch (massUnit) {
        case BYTE:
            return NonSI.BYTE;
        case KILOBYTE:
            return SI.KILO(NonSI.BYTE);
        case MEGABYTE:
            return SI.MEGA(NonSI.BYTE);
        case GIGABYTE:
            return SI.GIGA(NonSI.BYTE);
        default:
            return null;
        }
    }
}
