package com.geecommerce.core.web.stripes;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.stripes.localization.DefaultLocalizationBundleFactory;

import com.geecommerce.core.web.bundle.UTF8Control;

public class LocalizationBundleFactory extends DefaultLocalizationBundleFactory {
    private static final String DEFAULT_STRIPES_RESOURCES = "StripesResources";

    @Override
    public ResourceBundle getErrorMessageBundle(Locale locale) throws MissingResourceException {
	try {
	    if (locale == null) {
		return ResourceBundle.getBundle(DEFAULT_STRIPES_RESOURCES, new UTF8Control());
	    } else {
		return ResourceBundle.getBundle(DEFAULT_STRIPES_RESOURCES, locale, new UTF8Control());
	    }
	} catch (MissingResourceException mre) {
	    throw new RuntimeException(mre);
	}
    }

    @Override
    public ResourceBundle getFormFieldBundle(Locale locale) throws MissingResourceException {
	try {
	    if (locale == null) {
		return ResourceBundle.getBundle(DEFAULT_STRIPES_RESOURCES, new UTF8Control());
	    } else {
		return ResourceBundle.getBundle(DEFAULT_STRIPES_RESOURCES, locale, new UTF8Control());
	    }
	} catch (MissingResourceException mre) {
	    throw new RuntimeException(mre);
	}
    }
}
