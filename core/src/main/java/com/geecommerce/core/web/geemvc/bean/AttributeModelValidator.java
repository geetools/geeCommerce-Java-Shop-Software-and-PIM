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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.boon.Str;

import com.geecommerce.core.Char;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geemvc.handler.RequestHandler;
import com.geemvc.validation.Errors;
import com.geemvc.validation.ValidationContext;
import com.geemvc.validation.Validator;
import com.geemvc.validation.annotation.CheckBean;

@CheckBean(AttributeSupport.class)
public class AttributeModelValidator implements Validator {

    @Override
    public Object validate(RequestHandler requestHandler, ValidationContext validationCtx, Errors errors) {

        System.out.println("VxAAAAAAAAAAAAAALIDATINGxx !!! ------> " + validationCtx.typedValues());

        Map<String, Object> typedValues = validationCtx.typedValues();

        for (Map.Entry<String, Object> entry : typedValues.entrySet()) {
            Object value = entry.getValue();

            if (value == null)
                continue;

            if (value instanceof AttributeSupport) {
                AttributeSupport attrSupport = (AttributeSupport) value;

                List<AttributeValue> attrValues = attrSupport.getAttributes();

                for (AttributeValue av : attrValues) {
                    Attribute attr = av.getAttribute();

                    if (attr != null) {
                        // Attribute is mandatory and should therefore not be empty.
                        if ((av == null || av.getVal() == null) && attr.getInputType() == InputType.MANDATORY) {
                            String fieldName = fieldName(entry.getKey(), attr, validationCtx);

                            if (fieldName != null) {
                                errors.add(fieldName, "validation.error.required", attr.getFrontendLabel());
                            } else {
                                errors.add("validation.error.required", attr.getFrontendLabel());
                            }
                        } else if ((av == null || av.getVal() == null) && attr.getInputType() == InputType.OPTOUT && !av.isOptOut()) {
                            // Attribute is an opt-out attribute, but it has not been opted out and should therefore not be empty.
                            String fieldName = fieldName(entry.getKey(), attr, validationCtx);

                            if (fieldName != null) {
                                errors.add(fieldName, "validation.error.required", attr.getFrontendLabel());
                            } else {
                                errors.add("validation.error.required", attr.getFrontendLabel());
                            }
                        } else if (attr.getValidationPattern() != null && !Str.isEmpty(attr.getValidationPattern().getVal())) {
                            String pattern = attr.getValidationPattern().getVal();

                            Pattern p = Pattern.compile(pattern);
                            Matcher m = p.matcher(String.valueOf(av.getVal()));

                            if (!m.matches()) {
                                // Attribute is an opt-out attribute, but it has not been opted out and should therefore not be empty.
                                String fieldName = fieldName(entry.getKey(), attr, validationCtx);

                                if (fieldName != null) {
                                    errors.add(fieldName, "validation.error.is", attr.getFrontendLabel());
                                } else {
                                    errors.add("validation.error.is", attr.getFrontendLabel());
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    protected String fieldName(String modelParamName, Attribute attribute, ValidationContext validationCtx) {
        Map<String, String[]> paramMap = validationCtx.requestCtx().getParameterMap();

        String fieldName = new StringBuilder(modelParamName).append(Char.DOT).append("attr:").append(attribute.getCode()).toString();

        if (paramMap.containsKey(fieldName))
            return fieldName;

        fieldName = null;

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if (entry.getKey().startsWith(modelParamName) && entry.getKey().endsWith(new StringBuilder("attr:").append(attribute.getCode()).toString()))
                return entry.getKey();
        }

        return null;
    }
}
