package com.geecommerce.core.web;

import java.io.Serializable;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.Models;
import com.geecommerce.core.service.api.Model;

public class SessionValue implements Serializable {
    private static final long serialVersionUID = 7485398070269747730L;

    private final Map<String, Object> map;
    private final Class<? extends Model> modelClass;

    @SuppressWarnings("unchecked")
    public <T extends Model> SessionValue(T instance) {
        String name = instance.getClass().getSimpleName();

        if (name.contains("$$EnhancerByGuice$$") || name.contains("cglib.proxy.$NoOp$1")) {
            modelClass = (Class<T>) instance.getClass().getSuperclass();
        } else {
            modelClass = (Class<T>) instance.getClass();
        }

        this.map = Models.toMap((Class<T>) modelClass, instance);
    }

    public Object get() {
        return _readResolve();
    }

    @SuppressWarnings("unchecked")
    private Object _readResolve() {
        Class<? extends Model> modelInterface = (Class<? extends Model>) Reflect.getInterface(modelClass);

        Model instance = App.get().model(modelInterface);

        Models.populate(instance.getClass(), instance, map);

        return instance;
    }
}
