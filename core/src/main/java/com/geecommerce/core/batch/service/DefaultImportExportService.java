package com.geecommerce.core.batch.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.batch.dataimport.model.ImportFieldScriptlet;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.batch.dataimport.repository.ImportFieldScriptlets;
import com.geecommerce.core.batch.dataimport.repository.ImportProfiles;
import com.geecommerce.core.batch.dataimport.repository.ImportTokens;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Service
public class DefaultImportExportService implements ImportExportService {
    protected final ImportTokens importTokens;
    protected final ImportProfiles importProfiles;
    protected final ImportFieldScriptlets importFieldScriptlets;
    protected final AttributeService attributeService;

    protected List<String> systemFields = Arrays
        .asList(new String[] { "_action", "_id", "_parent_id", "_type", "_sub_type", "_scope_code", "_scope_type", "_merchant", "_store", "_view", "_request_context", "_language" });

    @Inject
    protected App app;

    @Inject
    public DefaultImportExportService(ImportTokens importTokens, ImportProfiles importProfiles, ImportFieldScriptlets importFieldScriptlets, AttributeService attributeService) {
        this.importTokens = importTokens;
        this.importProfiles = importProfiles;
        this.importFieldScriptlets = importFieldScriptlets;
        this.attributeService = attributeService;
    }

    @Override
    public ImportProfile newDefaultImportProfile(Set<String> headers, AttributeTargetObject attrTargetObject, String importToken) {
        ImportProfile importProfile = app.model(ImportProfile.class).setToken(importToken);

        for (String header : headers) {
            String _header = header.trim();

            ImportFieldScriptlet importFieldScriptlet = importFieldScriptlets.havingFieldName(attrTargetObject, _header);

            System.out.println("*************** importFieldScriptlet: " + _header + " -> " + importFieldScriptlet);

            if ((!header.startsWith(Str.UNDERSCORE) || importFieldScriptlet == null) && !systemFields.contains(_header)) {
                Attribute attr = attributeService.getAttribute(attrTargetObject, _header);

                System.out.println(header + " 1--> " + (attr == null ? null : attr.getBackendLabel().getClosestValue()));

                if (attr != null) {
                    importProfile.addField(_header, null, _header, true);
                } else {
                    importProfile.addField(_header, null, _header, false);
                }
            }
        }

        importProfile = importProfiles.add(importProfile);

        System.out.println("*************** importProfile: " + importProfiles.findById(ImportProfile.class, importProfile.getId()));

        return importProfile;
    }

    @Override
    public ImportFieldScriptlet createImportFieldScript(ImportFieldScriptlet importFieldScriptlet) {
        return importFieldScriptlets.add(importFieldScriptlet);
    }


    @Override
    public ImportToken getImportToken(String token) {
        return importTokens.havingToken(token);
    }
    
    @Override
    public ImportProfile getImportProfile(String token) {
        return importProfiles.havingToken(token);
    }

    @Override
    public ImportProfile getImportProfile(Id id) {
        return importProfiles.findById(ImportProfile.class, id);
    }
}
