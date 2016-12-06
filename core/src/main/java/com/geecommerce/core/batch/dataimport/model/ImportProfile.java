package com.geecommerce.core.batch.dataimport.model;

import java.util.Map;
import java.util.Set;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ImportProfile extends Model {
    Id getId();

    ImportProfile setId(Id id);

    Id getTargetObjectId();

    ImportProfile setTargetObjectId(Id targetObjectId);

    String getToken();

    ImportProfile setToken(String token);

    ContextObject<String> getName();

    ImportProfile setName(ContextObject<String> name);

    ImportProfile addField(String sourceColumnHeader, ContextObject<String> sourceColumnLabel, String destFieldExpression, boolean isAttribute);

    Map<String, ImportField> getFieldMapping();

    ImportProfile setFieldMapping(Map<String, ImportField> fieldMapping);

    Map<String, String> getSettings();

    ImportProfile setSettings(Map<String, String> settings);

    String getSetting(String key);

    ImportProfile putSetting(String key, String value);

    Set<Id> getToScopeIds();

    ImportProfile setToScopeIds(Set<Id> toScopeIds);

    ImportProfile addToScopeId(Id toScopeId);

    Set<String> getDataLanguages();

    ImportProfile setDataLanguages(Set<String> dataLanguages);

    ImportProfile addDataLanguage(String dataLanguage);

    Set<Id> getDataScopeIds();

    void setDataScopeIds(Set<Id> dataScopeIds);

    ImportProfile addDataScopeId(Id dataScopeId);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TOKEN = "token";
        public static final String NAME = "name";
        public static final String FIELD_MAPPING = "field_map";
        public static final String SETTINGS = "settings";
        public static final String TO_SCOPES = "to_scopes";
        public static final String DATA_LANGUAGES = "data_languages";
        public static final String DATA_SCOPES = "data_scopes";
    }
}
