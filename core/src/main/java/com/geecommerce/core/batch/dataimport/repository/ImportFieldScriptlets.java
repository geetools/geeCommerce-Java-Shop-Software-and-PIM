package com.geecommerce.core.batch.dataimport.repository;

import com.geecommerce.core.batch.dataimport.model.ImportFieldScriptlet;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;

public interface ImportFieldScriptlets extends Repository {

    ImportFieldScriptlet havingFieldName(AttributeTargetObject targetObject, String name);

}
