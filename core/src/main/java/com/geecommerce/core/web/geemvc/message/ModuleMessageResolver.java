package com.geecommerce.core.web.geemvc.message;

import java.util.Locale;
import java.util.ResourceBundle;

import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geemvc.annotation.Adapter;
import com.geemvc.i18n.message.MessageResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Adapter(weight = 10)
public class ModuleMessageResolver implements MessageResolver {
    @Inject
    protected App app;

    protected String PATH_PREFIX = "locale/";

    @Override
    public String resolve(String messageKey, String bundleName, Locale locale) {
        try {
            // System.out.println("##############>>>>>>>>><<<<<>>>>>>>
            // COMMERCEBORD TRYING MESSAGE :::: " + messageKey + " IN " +
            // bundleName + " FOR LOCALE " + locale);

            Class<?> controllerClass = app.getControllerClass();
            ClassLoader classLoader = Reflect.getModuleClassLoader(controllerClass);

            if (classLoader != null) {
                // Attempt to load bundle from module classpath.
                ResourceBundle bundle = ResourceBundle.getBundle(PATH_PREFIX + bundleName, locale, classLoader);
                return bundle.getString(messageKey);
            }
        } catch (Throwable t) {

        }

        return null;
    }
}
