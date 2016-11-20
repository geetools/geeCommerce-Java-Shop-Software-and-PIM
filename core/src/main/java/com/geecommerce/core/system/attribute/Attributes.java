package com.geecommerce.core.system.attribute;

import com.geecommerce.core.App;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;

public class Attributes {
    public static Id getAttributeId(Id attrTargetObjectId, String attributeCode) {
        Attribute attr = attributeService().getAttribute(attrTargetObjectId, attributeCode);

        return attr == null ? null : attr.getId();
    }

    public static Id getAttributeId(Class<? extends AttributeSupport> modelClass, String attributeCode) {

        AttributeTargetObject targetObject = attributeService()
            .getAttributeTargetObject(Reflect.getModelInterface(modelClass));

        Attribute attr = null;

        if (targetObject != null)
            attr = attributeService().getAttribute(targetObject, attributeCode);

        return attr == null ? null : attr.getId();
    }

    private static AttributeService attributeService() {
        return App.get().service(AttributeService.class);
    }
}
