package com.geecommerce.core.template.freemarker.model;

import com.geecommerce.core.type.Id;

import freemarker.template.AdapterTemplateModel;

public class IdTemplateModel implements AdapterTemplateModel {
    private final Id id;

    public IdTemplateModel(Id id) {
        this.id = id;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdaptedObject(Class clazz) {
        return id;
    }
}
