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

package com.geecommerce.core.web.geemvc.bean;

import java.util.List;

import org.boon.Str;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.Id;
import com.geemvc.Char;
import com.geemvc.annotation.Adapter;
import com.geemvc.converter.ConverterContext;
import com.geemvc.converter.bean.AbstractBeanConverter;
import com.geemvc.converter.bean.BeanConverterAdapter;
import com.google.inject.Singleton;

@Adapter
@Singleton
public class AttributeModelConverter extends AbstractBeanConverter implements BeanConverterAdapter<AttributeSupport> {
    protected String ATTR_PREFIX = "attr:";

    @Override
    public AttributeModelConverter bindProperty(AttributeSupport beanInstance, String expression, List<String> value,
        ConverterContext converterCtx) {
        super._bindProperty(beanInstance, expression, value);
        return this;
    }

    @Override
    public AttributeModelConverter bindProperty(AttributeSupport beanInstance, String expression, String value,
        ConverterContext converterCtx) {
        int dotPos = expression.lastIndexOf(Char.DOT);
        String propertyExpression = expression.substring(dotPos + 1);

        if (propertyExpression.startsWith(ATTR_PREFIX)) {
            String attributeCode = propertyExpression.substring(5);

            Attribute attr = beanInstance.getAttributeDefinition(attributeCode);

            if (attr == null)
                return this;

            if (attr.isOptionAttribute() && !Str.isEmpty(value)) {
                try {

                    if (attr.isAllowMultipleValues()) {
                        beanInstance.addAttribute(attributeCode, Id.valueOf(value));
                    } else {
                        beanInstance.setAttribute(attributeCode, Id.valueOf(value));
                    }
                } catch (NumberFormatException e) {
                    converterCtx.errors().add(expression, "validation.error.invalid.option", value);
                }
            } else {
                try {
                    if (attr.isAllowMultipleValues()) {
                        beanInstance.addAttribute(attributeCode, value);
                    } else {
                        beanInstance.setAttribute(attributeCode, value);
                    }
                } catch (NumberFormatException e) {
                    converterCtx.errors().add(expression, "javax.validation.constraints.Digits.message", value);
                }
            }
        } else {
            super._bindProperty(beanInstance, expression, value);
        }

        return this;
    }

    @Override
    public AttributeModelConverter bindProperties(List<String> values, String beanName, AttributeSupport beanInstance,
        ConverterContext converterCtx) {
        super._bindProperties(values, beanName, beanInstance);
        return this;
    }

    @Override
    public AttributeSupport fromStrings(List<String> values, String beanName, Class<AttributeSupport> beanType,
        ConverterContext converterCtx) {
        AttributeSupport beanInstance = null;

        if (beanName != null && beanType != null) {
            beanInstance = newInstance(beanType, converterCtx);

            for (String val : values) {
                int equalsPos = val.indexOf(Char.EQUALS);
                String propertyExpression = val.substring(0, equalsPos);
                String properyValue = val.substring(equalsPos + 1);

                bindProperty(beanInstance, propertyExpression, properyValue, converterCtx);
            }
        }

        return beanInstance;
    }

    @Override
    public AttributeSupport fromStrings(List<String> values, String beanName, Class<AttributeSupport> beanType,
        int index, ConverterContext converterCtx) {
        return (AttributeSupport) super._fromStrings(values, beanName, beanType, index);
    }

    @Override
    public AttributeSupport fromStrings(List<String> values, String beanName, Class<AttributeSupport> beanType,
        int index, String mapKey, ConverterContext converterCtx) {
        return (AttributeSupport) super._fromStrings(values, beanName, beanType, index, mapKey);
    }

    @Override
    public AttributeSupport fromStrings(List<String> values, String beanName, Class<AttributeSupport> beanType,
        String mapKey, ConverterContext converterCtx) {
        return (AttributeSupport) super._fromStrings(values, beanName, beanType, mapKey);
    }

    @Override
    public AttributeSupport fromStrings(List<String> values, String beanName, Class<AttributeSupport> beanType,
        String mapKey, int index, ConverterContext converterCtx) {
        return (AttributeSupport) super._fromStrings(values, beanName, beanType, mapKey, index);
    }

    @Override
    public AttributeSupport newInstance(Class<AttributeSupport> beanType, ConverterContext converterCtx) {
        return (AttributeSupport) super._newInstance(beanType);
    }
}
