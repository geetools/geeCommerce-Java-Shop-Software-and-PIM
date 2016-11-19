package com.geecommerce.unit.converter.service;

import static com.geecommerce.unit.converter.enums.LengthUnit.UNSUPPORTED;

import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.unit.converter.configuration.Key;
import com.geecommerce.unit.converter.enums.LengthUnit;
import com.google.inject.Inject;

@Service
public class DefaultLengthConverter implements LengthConverter {
    @Inject
    protected App app;

    @Override
    public Double convert(Double value, LengthUnit from, LengthUnit to) {
        if (value == null)
            return null;

        if (from.equals(UNSUPPORTED) || to.equals(UNSUPPORTED))
            return null;

        Unit fUnit = get(from);
        Unit tUnit = get(to);

        if (fUnit == null || tUnit == null)
            return null;

        Amount<Length> m = Amount.valueOf(value, fUnit);
        return m.doubleValue(tUnit);
    }

    @Override
    public Double convert(Double value, LengthUnit to) {
        LengthUnit from = defaultUnit();
        return convert(value, from, to);
    }

    @Override
    public LengthUnit defaultUnit() {
        return LengthUnit.valueOf(app.cpStr_(Key.LENGTH));
    }

    private Unit get(LengthUnit lengthUnit) {
        switch (lengthUnit) {
        case CENTIMETER:
            return SI.CENTIMETER;
        case MILLIMETER:
            return SI.MILLIMETER;
        case METER:
            return SI.METER;
        case INCH:
            return NonSI.INCH;
        default:
            return null;
        }
    }
}
