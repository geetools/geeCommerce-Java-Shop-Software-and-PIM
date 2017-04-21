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

package com.geecommerce.core.system.attribute.model;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AttributeTargetObject extends Model {

    Id getId();

    AttributeTargetObject setId(Id id);

    String getCode();

    AttributeTargetObject setCode(String code);

    String getModule();

    AttributeTargetObject setModule(String module);

    ContextObject<String> getName();

    AttributeTargetObject setName(ContextObject<String> name);

    String getType();

    AttributeTargetObject setType(String type);

    Class<? extends AttributeSupport> toModelType();

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String MODULE = "module";
        public static final String NAME = "name";
        public static final String TYPE = "type";
    }
}
