package com.geecommerce.core.batch.dataimport.helper;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.type.Id;

public interface AttributeHelper extends Helper {
    AttributeTargetObject targetObject(AttributeSupport model);

    AttributeTargetObject targetObject(Class<? extends AttributeSupport> modelClass);

    boolean optionExists(AttributeTargetObject targetObject, String code, String value, String language);

    Id optionId(AttributeTargetObject targetObject, String code, String value, String language);

    boolean optionsExistForOtherLanguages(AttributeTargetObject targetObject, String code, String notLanguage);
}
