package com.geecommerce.core.web.stripes.converter;

import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import org.apache.commons.lang3.math.NumberUtils;

import com.geecommerce.core.type.Id;

public class IdTypeConverter implements TypeConverter<Id> {
    @Override
    public Id convert(String input, Class<? extends Id> targetType, Collection<ValidationError> errors) {
	if (NumberUtils.isNumber(input)) {
	    return Id.parseId(input);
	}

	return null;
    }

    @Override
    public void setLocale(Locale locale) {
	// do nothing
    }
}
