/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geecommerce.core.web.geemvc.bind.param.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.geecommerce.core.web.geemvc.bind.param.annotation.ModelParam;
import com.geemvc.HttpMethod;
import com.geemvc.RequestContext;
import com.geemvc.annotation.Adapter;
import com.geemvc.bind.param.ParamAdapters;
import com.geemvc.bind.param.ParamContext;
import com.geemvc.bind.param.TypedParamAdapter;
import com.geemvc.converter.ConverterContext;
import com.geemvc.converter.SimpleConverter;
import com.geemvc.converter.bean.BeanConverterAdapter;
import com.geemvc.converter.bean.BeanConverterAdapterFactory;
import com.geemvc.data.DataAdapter;
import com.geemvc.data.DataAdapterFactory;
import com.geemvc.reflect.ReflectionProvider;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
@Adapter
public class ModelParamAdapter implements TypedParamAdapter<ModelParam> {
    protected ParamAdapters paramAdapters;
    protected DataAdapterFactory dataAdapterFactory;
    protected ReflectionProvider reflectionProvider;
    protected SimpleConverter simpleConverter;
    protected BeanConverterAdapterFactory beanConverterAdapterFactory;

    @Inject
    protected Injector injector;

    @Inject
    protected ModelParamAdapter(ParamAdapters paramAdapters, DataAdapterFactory dataAdapterFactory, ReflectionProvider reflectionProvider, SimpleConverter simpleConverter, BeanConverterAdapterFactory beanConverterAdapterFactory) {
        this.paramAdapters = paramAdapters;
        this.dataAdapterFactory = dataAdapterFactory;
        this.reflectionProvider = reflectionProvider;
        this.simpleConverter = simpleConverter;
        this.beanConverterAdapterFactory = beanConverterAdapterFactory;
    }

    @Override
    public String getName(ModelParam modelParam) {
        return modelParam.value() == null ? modelParam.name() : modelParam.value();
    }

    @Override
    public List<String> getValue(ModelParam modelParam, String paramName, ParamContext paramCtx) {
        RequestContext requestCtx = paramCtx.requestCtx();

        Map<String, String[]> pathParameterMap = requestCtx.getPathParameters();

        String[] values = null;

        if (pathParameterMap != null && !pathParameterMap.isEmpty()) {
            values = pathParameterMap.get(paramName);

            if (values == null) {
                Map<String, String[]> parameterMap = requestCtx.getParameterMap();
                values = parameterMap.get(paramName);
            }
        }

        return values == null ? null : Arrays.asList(values);
    }

    @Override
    public Object getTypedValue(ModelParam modelParam, String paramName, ParamContext paramCtx) {
        // What bean type are we working with?
        Class<?> beanClass = reflectionProvider.getPrimaryType(paramCtx.methodParam().type(), paramCtx.methodParam().parameterizedType());

        Object entity = null;

        // First we make sure that the bean-class really is a bean and not some simple type.
        if (!reflectionProvider.isSimpleType(beanClass)) {
            // Attempt to automatically resolve the correct data adapter. For example, if the bean has been annotated
            // with javax.persistence.Entity then we should end up with the JpaDataAdapter.
            DataAdapter dataAdapter = dataAdapterFactory.create(beanClass);

            if (dataAdapter != null) {
                // Now we attempt to automatically retrieve the bean's identifiers. For example in JPA they may be
                // marked with the @Id annotation.
                Map<String, Class<?>> beanIdentifiers = dataAdapter.beanIdentifiers(beanClass);

                if (beanIdentifiers.isEmpty())
                    throw new IllegalStateException("Unable to auto fetch bean '" + beanClass.getName() + "' from database because no identifier is present.");

                if (beanIdentifiers.size() > 1)
                    throw new IllegalStateException("Unable to auto fetch bean '" + beanClass.getName() + "' from database because only a single identifier is supported.");

                // Currently we only support a single primary key.
                Entry<String, Class<?>> identifier = beanIdentifiers.entrySet().iterator().next();

                // Once we have got the name of the primary key field, we attempt to find a matching parameter in the
                // current request.
                List<String> idValue = getValue(modelParam, identifier.getKey(), paramCtx);

                RequestContext requestCtx = paramCtx.requestCtx();

                if ((idValue == null || idValue.isEmpty()) && HttpMethod.PUT.equals(requestCtx.getMethod()))
                    throw new IllegalStateException("You must provide a valid bean identifier for bean '" + beanClass.getName() + "' when using the @Data param annotation and HTTP methodd 'PUT'.");

                // We only deal with primary key types that the SimpleConverter can convert.
                if (idValue != null && !idValue.isEmpty() && simpleConverter.canConvert(identifier.getValue()))
                    entity = dataAdapter.fetch(beanClass, simpleConverter.fromString(idValue.get(0), identifier.getValue()));

                // if the entity is null and we are dealing with a POST request, we attempt to create a new bean
                // instance.
                if (entity == null && HttpMethod.POST.equals(requestCtx.getMethod()))
                    entity = injector.getInstance(beanClass);

                // Now if we have a bean instance and the HTTP-Method is either POST or PUT, we can look for parameters
                // in the current request to populate the bean with.
                if (entity != null && HttpMethod.POST.equals(requestCtx.getMethod()) || HttpMethod.PUT.equals(requestCtx.getMethod())) {
                    List<String> beanData = paramAdapters.getRequestValues(paramName, paramCtx.requestCtx());

                    // Bind the properties if any were found.
                    if (beanData != null && !beanData.isEmpty()) {
                        BeanConverterAdapter beanConverter = beanConverterAdapterFactory.create(beanClass, null);

                        if (beanConverter != null) {
                            ConverterContext converterCtx = injector.getInstance(ConverterContext.class).build(paramName, beanClass, null, requestCtx, paramCtx.requestValues(), paramCtx.errors(), paramCtx.notices());

                            beanConverter.bindProperties(beanData, paramName, entity, converterCtx);
                        } else {
                            throw new IllegalStateException(
                                    "Unable to find a compatible bean converter for the bean '" + beanClass.getName() + "' which is needed for the @ModelParam(" + paramName + ") param when the HTTP method is either 'POST' or 'PUT'.");
                        }
                    }
                }
            }
        }

        return entity;
    }
}
