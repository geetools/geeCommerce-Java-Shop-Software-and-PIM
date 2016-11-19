package com.geecommerce.unit.converter.service;

import static com.geecommerce.unit.converter.enums.MassUnit.UNSUPPORTED;

import javax.measure.quantity.Mass;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.unit.converter.configuration.Key;
import com.geecommerce.unit.converter.enums.MassUnit;
import com.google.inject.Inject;

@Service
public class DefaultMassConverter implements MassConverter {
    @Inject
    protected App app;

    @Override
    public Double convert(Double value, MassUnit from, MassUnit to) {
        if (value == null)
            return null;

        if (from.equals(UNSUPPORTED) || to.equals(UNSUPPORTED))
            return null;

        Unit fUnit = get(from);
        Unit tUnit = get(to);

        if (fUnit == null || tUnit == null)
            return null;

        Amount<Mass> m = Amount.valueOf(value, fUnit);
        return m.doubleValue(tUnit);
    }

    @Override
    public Double convert(Double value, MassUnit to) {
        MassUnit from = defaultUnit();
        return convert(value, from, to);
    }

    @Override
    public MassUnit defaultUnit() {
        return MassUnit.valueOf(app.cpStr_(Key.MASS));
    }

    private Unit get(MassUnit massUnit) {
        switch (massUnit) {
        case GRAM:
            return SI.GRAM;
        case KILOGRAM:
            return SI.KILOGRAM;
        case POUND:
            return NonSI.POUND;
        default:
            return null;
        }
    }
}
