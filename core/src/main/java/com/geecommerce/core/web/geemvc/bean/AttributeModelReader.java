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

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geemvc.annotation.Adapter;
import com.geemvc.reader.bean.AbstractBeanReader;
import com.geemvc.reader.bean.BeanReaderAdapter;
import com.google.inject.Singleton;

@Adapter
@Singleton
public class AttributeModelReader extends AbstractBeanReader implements BeanReaderAdapter<AttributeSupport> {
    protected String ATTR_PREFIX = "attr:";

    @Override
    public Object lookup(String expression, AttributeSupport beanInstance) {
        if (expression.startsWith(ATTR_PREFIX)) {
            String attributeCode = expression.substring(5);
            AttributeValue av = beanInstance.getAttribute(attributeCode);

            if (av == null)
                return null;

            Attribute attr = beanInstance.getAttributeDefinition(attributeCode);

            if (attr == null)
                return null;

            if (attr.isOptionAttribute()) {
                if (attr.isAllowMultipleValues()) {
                    return av.getOptionIds();
                } else {
                    return av.getOptionId();
                }
            } else {
                return av.getVal();
            }
        } else {
            return super._lookup(expression, beanInstance);
        }
    }
}
