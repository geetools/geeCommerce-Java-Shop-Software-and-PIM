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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("attribute_target_objects")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeTargetObject")
public class DefaultAttributeTargetObject extends AbstractModel implements AttributeTargetObject {
    private static final long serialVersionUID = -8274544676101969159L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.CODE)
    protected String code = null;

    @Column(Col.NAME)
    protected ContextObject<String> name = null;

    @Column(Col.TYPES)
    protected Set<String> types = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AttributeTargetObject setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public AttributeTargetObject setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public AttributeTargetObject setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public Set<String> getTypes() {
        return types;
    }

    @Override
    public AttributeTargetObject setTypes(Set<String> types) {
        this.types = types;
        return this;
    }

    @Override
    public AttributeTargetObject setType(String type) {
        types = new LinkedHashSet<>();
        types.add(type);
        return this;
    }

    @Override
    public AttributeTargetObject addType(String type) {
        if (types == null) {
            types = new LinkedHashSet<>();
        }

        types.add(type);

        return this;
    }

    @Override
    public String toString() {
        return "DefaultAttributeTargetObject [id=" + id + ", name=" + name + ", types=" + types + "]";
    }
}
