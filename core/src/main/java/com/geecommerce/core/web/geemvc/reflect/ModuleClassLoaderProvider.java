package com.geecommerce.core.web.geemvc.reflect;

import com.geecommerce.core.App;
import com.geemvc.reflect.ClassLoaderProvider;

/**
 * Created by Michael on 08.07.2016.
 */
public class ModuleClassLoaderProvider implements ClassLoaderProvider {
    @Override
    public ClassLoader provide() {
        Class<?> controllerClass = App.get().getControllerClass();
        return controllerClass == null ? getClass().getClassLoader() : controllerClass.getClassLoader();
    }
}
