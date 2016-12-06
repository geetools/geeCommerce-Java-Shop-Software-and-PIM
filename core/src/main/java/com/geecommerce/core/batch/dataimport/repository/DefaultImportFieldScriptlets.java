package com.geecommerce.core.batch.dataimport.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.batch.dataimport.model.ImportFieldScriptlet;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;

@Repository
public class DefaultImportFieldScriptlets extends AbstractRepository implements ImportFieldScriptlets {

    @Override
    public ImportFieldScriptlet havingFieldName(AttributeTargetObject targetObject, String name) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(ImportFieldScriptlet.Col.TARGET_OBJECT_ID, targetObject.getId());
        filter.put(ImportFieldScriptlet.Col.FIELD_NAME, name);

        return findOne(ImportFieldScriptlet.class, filter);
    }
}
