package com.geecommerce.core.json.genson;

import java.beans.Transient;
import java.lang.reflect.Method;

import com.owlike.genson.Trilean;
import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.reflect.BeanMutatorAccessorResolver;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;

public class DefaultMutatorAccessorResolver extends BeanMutatorAccessorResolver.PropertyBaseResolver {
    @Override
    public Trilean isMutator(Method method, Class<?> fromClass) {
        if (Reflect.isSetter(method))
            return Trilean.TRUE;

        else
            return Trilean.UNKNOWN;
    }

    @Override
    public Trilean isAccessor(Method method, Class<?> fromClass) {
        // We are not interested in Object methods, so we might as well stop here.
        if (Object.class.equals(method.getDeclaringClass()))
            return Trilean.FALSE;

        // We only want to do the property check for model objects.
        if ((!isModel(fromClass) && !isInjectable(fromClass)) || AttributeValue.class.isAssignableFrom(fromClass)) {
            return Reflect.isGetter(method) ? Trilean.TRUE : Trilean.FALSE;
        }

        if (!Reflect.isGetter(method) || Reflect.isAnnotationPresent(method, fromClass, JsonIgnore.class) || Reflect.isAnnotationPresent(method, fromClass, Transient.class)
                || Reflect.isAnnotationPresent(method, fromClass, javax.persistence.Transient.class)) {
            return Trilean.FALSE;
        }

        // String propertyName = Reflect.toProperty(method);
        //
        // if (propertyName != null)
        // {
        // return isIgnore(propertyName) ? Trilean.FALSE : Trilean.TRUE;
        // }

        return Trilean.TRUE;
    }

    private boolean isInjectable(Class<?> type) {
        return Injectable.class.isAssignableFrom(type);
    }

    private boolean isModel(Class<?> type) {
        return Model.class.isAssignableFrom(type);
    }
}
