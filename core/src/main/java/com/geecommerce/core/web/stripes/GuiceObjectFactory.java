package com.geecommerce.core.web.stripes;

import java.util.Set;

import com.geecommerce.core.inject.ModuleInjector;
import com.google.inject.Injector;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.controller.DefaultObjectFactory;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.ReflectUtil;

/**
 * Overrides the default Stripes object factory in order to enable dependency
 * injection by Guice.
 * 
 * @author Michael Delamere
 */
public class GuiceObjectFactory extends DefaultObjectFactory {
    @Override
    public <T> T newInstance(Class<T> clazz) {
        try {
            Set<Class<?>> interfaces = ReflectUtil.getImplementedInterfaces(clazz);

            if (interfaces == null || interfaces.size() == 0 || !interfaces.contains(ActionBean.class)) {
                return super.newInstance(clazz);
            }

            Injector injector = ModuleInjector.get();

            if (clazz.isInterface())
                return postProcess(newInterfaceInstance(clazz));
            else
                return postProcess(injector.getInstance(clazz));
        } catch (Throwable t) {
            throw new StripesRuntimeException("Could not instantiate " + clazz, t);
        }
    }
}
