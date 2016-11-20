package com.geecommerce.core.web.stripes.converter;

import com.geecommerce.core.type.Id;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.validation.DefaultTypeConverterFactory;

public class TypeConverterFactory extends DefaultTypeConverterFactory {
    public void init(final Configuration configuration) {
        super.init(configuration);

        add(Id.class, IdTypeConverter.class);
    }
}
