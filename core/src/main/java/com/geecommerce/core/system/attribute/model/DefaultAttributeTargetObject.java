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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geemodule.api.Module;
import com.google.inject.Inject;

@Model("attribute_target_objects")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeTargetObject")
public class DefaultAttributeTargetObject extends AbstractModel implements AttributeTargetObject {
    private static final long serialVersionUID = -8274544676101969159L;

    @Inject
    protected App app;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.CODE)
    protected String code = null;

    @Column(Col.MODULE)
    protected String module = null;

    @Column(Col.NAME)
    protected ContextObject<String> name = null;

    @Column(Col.TYPE)
    protected String type = null;

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
    public String getModule() {
        return module;
    }

    @Override
    public AttributeTargetObject setModule(String module) {
        this.module = module;
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
    public String getType() {
        return type;
    }

    @Override
    public AttributeTargetObject setType(String type) {
        this.type = type;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends AttributeSupport> toModelType() {
        Class<? extends AttributeSupport> clazz = null;

        if (module != null) {
            Module m = app.moduleLoader().getLoadedModuleByCode(module);

            if (m != null) {
                try {
                    clazz = (Class<? extends AttributeSupport>) m.loadClass(type);
                } catch (ClassNotFoundException e) {
                }
            }

            if (clazz == null && m == null) {
                m = app.moduleLoader().getLoadedModule(module);

                if (m != null) {
                    try {
                        clazz = (Class<? extends AttributeSupport>) m.loadClass(type);
                    } catch (ClassNotFoundException e) {
                    }
                }
            }
        }

        if (clazz == null) {
            try {
                clazz = (Class<? extends AttributeSupport>) app.moduleLoader().lookup(type);
            } catch (ClassNotFoundException e) {
            }
        }

        return clazz;
    }

    @Override
    public String toString() {
        return "DefaultAttributeTargetObject [id=" + id + ", name=" + name + ", type=" + type + "]";
    }
}
