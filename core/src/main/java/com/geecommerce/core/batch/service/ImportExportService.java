package com.geecommerce.core.batch.service;

import java.util.Set;

import com.geecommerce.core.batch.dataimport.model.ImportFieldScriptlet;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.type.Id;

public interface ImportExportService extends Service {

    ImportProfile newDefaultImportProfile(Set<String> headers, AttributeTargetObject attrTargetObject, String importToken);

    ImportFieldScriptlet createImportFieldScript(ImportFieldScriptlet importFieldScriptlet);

    ImportProfile getImportProfile(String token);

    ImportProfile getImportProfile(Id id);

}
