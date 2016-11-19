package com.geecommerce.core.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Pojo;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

public class AbstractPojo implements Pojo {
    private static final long serialVersionUID = -2788239705086109894L;

    @Inject
    protected App app;

    @JsonIgnore
    private Map<String, Class<?>> __cbTransientServiceFields = new HashMap<>();

    protected AbstractPojo() {
        Map<String, Field> transientServiceFields = Reflect.getTransientServiceFields(this.getClass());

        Set<String> keys = transientServiceFields.keySet();

        for (String fieldName : keys) {
            __cbTransientServiceFields.put(fieldName, transientServiceFields.get(fieldName).getType());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        app.injectMembers(this);

        if (__cbTransientServiceFields != null && !__cbTransientServiceFields.isEmpty()) {
            Set<String> fieldNames = __cbTransientServiceFields.keySet();

            for (String fieldName : fieldNames) {
                Reflect.setField(this, fieldName, app.inject(__cbTransientServiceFields.get(fieldName)));
            }
        }
    }
}
