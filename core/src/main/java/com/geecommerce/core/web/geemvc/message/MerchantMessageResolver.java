package com.geecommerce.core.web.geemvc.message;

import java.util.Locale;
import java.util.ResourceBundle;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.geemvc.annotation.Adapter;
import com.geemvc.i18n.message.MessageResolver;
import com.google.inject.Singleton;

@Singleton
@Adapter(weight = 10)
public class MerchantMessageResolver implements MessageResolver {

    protected String PATH_PREFIX = "locale.";

    @Override
    public String resolve(String messageKey, String bundleName, Locale locale) {
        String message = null;

        try {
            Class<?> controllerClass = App.get().getControllerClass();
            ClassLoader moduleClassLoader = Reflect.getModuleClassLoader(controllerClass);

            String moduleCode = null;

            if (moduleClassLoader != null) {
                Module m = ((ModuleClassLoader) moduleClassLoader).getModule();

                if (m != null) {
                    moduleCode = !Str.isEmpty(m.getCode()) ? m.getCode() : m.getName();
                }
            }

            ClassLoader classLoader = Reflect.getMerchantClassLoader();

            if (classLoader != null) {

                // ------------------------------------------------------------------------------------------------
                // First we attempt to load message from bundle
                // projects/<project>/classes/locale/<module>/.
                // ------------------------------------------------------------------------------------------------

                if (moduleCode != null) {
                    try {
                        // Attempt to load bundle from merchant classpath.
                        ResourceBundle bundle = ResourceBundle
                            .getBundle(PATH_PREFIX + moduleCode + Str.DOT + bundleName, locale, classLoader);
                        message = bundle.getString(messageKey);
                    } catch (Throwable t) {
                    }
                }

                // ------------------------------------------------------------------------------------------------
                // Next we attempt to load message from bundle
                // projects/<project>/classes/locale/.
                // ------------------------------------------------------------------------------------------------
                if (message == null) {
                    try {
                        // Attempt to load bundle from merchant classpath.
                        ResourceBundle bundle = ResourceBundle.getBundle(PATH_PREFIX + bundleName, locale, classLoader);
                        message = bundle.getString(messageKey);
                    } catch (Throwable t) {

                    }
                }
            }

        } catch (Throwable t) {

        }

        return message;
    }
}
