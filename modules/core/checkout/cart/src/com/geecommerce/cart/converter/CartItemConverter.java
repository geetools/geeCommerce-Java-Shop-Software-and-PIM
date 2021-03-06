package com.geecommerce.cart.converter;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.StringEscapeUtils;

import com.geecommerce.cart.model.CartItemJson;
import com.geecommerce.core.util.Json;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class CartItemConverter implements TypeConverter<CartItemJson> {
    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public CartItemJson convert(String s, Class<? extends CartItemJson> aClass,
        Collection<ValidationError> validationErrors) {
        CartItemJson item = Json.fromJson(StringEscapeUtils.unescapeHtml4(s), CartItemJson.class);
        return item;
    }
}
