package com.geecommerce.core.json.genson;

import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.GensonBundle;
import com.owlike.genson.reflect.AbstractBeanDescriptorProvider.ContextualConverterFactory;
import com.owlike.genson.reflect.BeanDescriptorProvider;
import com.owlike.genson.reflect.BeanMutatorAccessorResolver;
import com.owlike.genson.reflect.BeanPropertyFactory;
import com.owlike.genson.reflect.PropertyNameResolver;

public class DefaultGensonBundle extends GensonBundle {
    @Override
    public void configure(GensonBuilder builder) {
    }

    @Override
    public BeanDescriptorProvider createBeanDescriptorProvider(ContextualConverterFactory contextualConverterFactory, BeanPropertyFactory propertyFactory, BeanMutatorAccessorResolver propertyResolver, PropertyNameResolver nameResolver, GensonBuilder builder) {
	return new DefaultBeanDescriptorProvider(contextualConverterFactory, propertyFactory, propertyResolver, nameResolver, true, true, true);
    }
}
