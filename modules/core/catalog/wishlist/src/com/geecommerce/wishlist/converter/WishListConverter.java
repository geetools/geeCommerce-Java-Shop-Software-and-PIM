package com.geecommerce.wishlist.converter;

import java.util.Collection;
import java.util.Locale;

import com.geecommerce.core.util.Json;
import com.geecommerce.wishlist.model.WishListJson;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

public class WishListConverter implements TypeConverter<WishListJson> {

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public WishListJson convert(String s, Class<? extends WishListJson> aClass,
        Collection<ValidationError> validationErrors) {
        WishListJson item = Json.fromJson(s, WishListJson.class);
        return item;
    }
}
