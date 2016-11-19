package com.geecommerce.core.web.stripes.validation;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

import com.geecommerce.core.App;
import com.google.inject.Inject;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class CharsetTypeConverter implements TypeConverter<String> {
    @Inject
    protected App app;

    protected static final String VALID_INPUT_CHARSET_CONFIG_KEY = "core/web/form/valid_input_charset";

    public void setLocale(Locale locale) {
    }

    public String convert(String input, Class<? extends String> targetType, Collection<ValidationError> errors) {
        String validInputCharset = app.cpStr_(VALID_INPUT_CHARSET_CONFIG_KEY);

        // Do not do any checking if no charset has been configured.
        if (validInputCharset != null) {
            boolean isValidCharset = Charset.forName(validInputCharset).newEncoder().canEncode(input);

            if (!isValidCharset)
                errors.add(new ScopedLocalizableError("converter.charset", "invalidCharset"));
        }

        // We do not change the value - we just want to make sure that it has
        // the correct charset.
        return input;
    }
}
