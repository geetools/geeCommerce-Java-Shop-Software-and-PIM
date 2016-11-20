package com.geecommerce.core.json.genson;

import static com.owlike.genson.reflect.TypeUtil.getRawClass;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.owlike.genson.Genson;
import com.owlike.genson.reflect.BaseBeanDescriptorProvider;
import com.owlike.genson.reflect.BeanCreator;
import com.owlike.genson.reflect.BeanDescriptor;
import com.owlike.genson.reflect.BeanMutatorAccessorResolver;
import com.owlike.genson.reflect.BeanPropertyFactory;
import com.owlike.genson.reflect.PropertyAccessor;
import com.owlike.genson.reflect.PropertyMutator;
import com.owlike.genson.reflect.PropertyNameResolver;

public class DefaultBeanDescriptorProvider extends BaseBeanDescriptorProvider {
    public DefaultBeanDescriptorProvider(ContextualConverterFactory ctxConverterFactory,
        BeanPropertyFactory propertyFactory, BeanMutatorAccessorResolver mutatorAccessorResolver,
        PropertyNameResolver nameResolver, boolean useGettersAndSetters, boolean useFields,
        boolean favorEmptyCreators) {
        super(ctxConverterFactory, propertyFactory, mutatorAccessorResolver, nameResolver, useGettersAndSetters,
            useFields, favorEmptyCreators);
    }

    @Override
    protected <T> BeanDescriptor<T> create(Class<T> forClass, Type ofType, BeanCreator creator,
        List<PropertyAccessor> accessors, Map<String, PropertyMutator> mutators, Genson genson) {
        return new DefaultBeanDescriptor<T>(forClass, getRawClass(ofType), accessors, mutators, creator,
            genson.failOnMissingProperty());
    }

    @Override
    public void provideBeanPropertyAccessors(Type ofType, Map<String, LinkedList<PropertyAccessor>> accessorsMap,
        Genson genson) {
        for (Class<?> clazz = getImplementationClass(getRawClass(ofType)); clazz != null
            && !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
            // first lookup for fields
            if (useFields)
                provideFieldAccessors(clazz, accessorsMap, ofType, genson);
            // and now search methods (getters)
            if (useGettersAndSetters)
                provideMethodAccessors(clazz, accessorsMap, ofType, genson);
        }
    }

    @Override
    public void provideBeanPropertyMutators(Type ofType, Map<String, LinkedList<PropertyMutator>> mutatorsMap,
        Genson genson) {
        for (Class<?> clazz = getImplementationClass(getRawClass(ofType)); clazz != null
            && !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
            // first lookup for fields
            if (useFields)
                provideFieldMutators(clazz, mutatorsMap, ofType, genson);
            // and now search methods (getters)
            if (useGettersAndSetters)
                provideMethodMutators(clazz, mutatorsMap, ofType, genson);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?> getImplementationClass(Class<?> clazz) {
        if (clazz == null)
            return clazz;

        Class<?> implClass = null;

        if (clazz.isInterface() && isModel(clazz)) {
            Model m = App.get().inject((Class<Model>) clazz);

            if (m != null)
                implClass = m.getClass();
        } else if (clazz.isInterface() && isInjectable(clazz)) {
            Injectable i = App.get().inject((Class<Injectable>) clazz);

            if (i != null)
                implClass = i.getClass();
        }

        return implClass == null ? clazz : implClass;
    }

    private boolean isInjectable(Class<?> type) {
        return Injectable.class.isAssignableFrom(type);
    }

    private boolean isModel(Class<?> type) {
        return Model.class.isAssignableFrom(type);
    }
}
