package com.geecommerce.core.rest.jersey.inject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.Provider;

import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Model;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.MoreTypes.ParameterizedTypeImpl;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Profile
@Provider
@Singleton
public class ModelInjectableProvider extends AbstractHttpContextInjectable<Object> implements InjectableProvider<ModelParam, Type> {
    @Inject
    protected App app;

    protected final Type type;

    public ModelInjectableProvider() {
        type = null;
    }

    public ModelInjectableProvider(Type type) {
        this.type = type;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Undefined;
    }

    @Override
    public Injectable<Object> getInjectable(ComponentContext ic, ModelParam mp, Type type) {
        if (type instanceof Class && Model.class.isAssignableFrom((Class<?>) type)) {
            return new ModelInjectableProvider(type);
        } else if (type instanceof ParameterizedType) {
            List<Class<?>> genTypes = Reflect.getGenericType(type);

            Class<?> genType = genTypes != null && genTypes.size() > 0 ? genTypes.get(0) : null;

            if (genType != null && Model.class.isAssignableFrom(genType)) {
                return new ModelInjectableProvider(type);
            }
        }

        return null;
    }

    @Override
    public Object getValue(HttpContext ctx) {
        HttpRequestContext request = ctx.getRequest();

        Object deserialized = null;

        if (HttpMethod.POST.equals(request.getMethod()) || HttpMethod.PUT.equals(request.getMethod())) {
            if (type instanceof Class && Model.class.isAssignableFrom((Class<?>) type)) {
                // We cannot actually use this instance with jersey, but at
                // least we know
                // which implementation is to be used for the given interface.
                deserialized = (Model) app.inject((Class<?>) type);

                if (deserialized != null) {
                    // Let jersey instantiate and populate the model object
                    // using
                    // the implementation object we located via guice.
                    deserialized = request.getEntity(deserialized.getClass());

                    // Because jersey creates a new instance, instead of using
                    // the one
                    // created by guice, we inject the members manually
                    // afterwards.
                    if (deserialized != null)
                        app.injectMembers(deserialized);
                }
            } else if (type instanceof ParameterizedType) {
                List<Class<?>> genTypes = Reflect.getGenericType(type);

                Class<?> genType = genTypes != null && genTypes.size() > 0 ? genTypes.get(0) : null;

                if (genType != null && Model.class.isAssignableFrom(genType)) {
                    Model m = (Model) app.inject((Class<?>) genType);

                    // We need to create the generic type manually.
                    ParameterizedTypeImpl genericType = new ParameterizedTypeImpl(null, List.class, new Type[] { m.getClass() });

                    deserialized = request.getEntity(List.class, genericType, List.class.getAnnotations());
                }
            }
        }

        return deserialized;
    }
}
