package com.geecommerce.core.web.stripes.converter;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.validation.DefaultTypeConverterFactory;

import com.geecommerce.core.type.Id;

public class TypeConverterFactory extends DefaultTypeConverterFactory {
    public void init(final Configuration configuration) {
	super.init(configuration);

	add(Id.class, IdTypeConverter.class);
    }
}
