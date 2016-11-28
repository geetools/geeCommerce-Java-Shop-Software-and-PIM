package com.geecommerce.core.batch.dataimport;

import java.util.Map;
import java.util.Set;

import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ImportProfile {
    public Id getId();

    public ImportProfile setId(Id id);

    public Id getTargetObjectId();

    public ImportProfile setTargetObjectId(Id targetObjectId);

    public ContextObject<String> getName();

    public ImportProfile setName(ContextObject<String> name);

    public Map<String, ImportField> getFieldMapping();

    public ImportProfile setFieldMapping(Map<String, ImportField> fieldMapping);

    public Map<String, String> getSettings();

    public ImportProfile setSettings(Map<String, String> settings);

    public String getSetting(String key);

    public ImportProfile putSetting(String key, String value);

    public Set<Id> getToScopeIds();

    public ImportProfile setToScopeIds(Set<Id> toScopeIds);

    public ImportProfile addToScopeId(Id toScopeId);

    public Set<String> getDataLanguages();

    public ImportProfile setDataLanguages(Set<String> dataLanguages);

    public ImportProfile addDataLanguage(String dataLanguage);

    public Set<Id> getDataScopeIds();

    public void setDataScopeIds(Set<Id> dataScopeIds);

    public ImportProfile addDataScopeId(Id dataScopeId);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String NAME = "name";
        public static final String FIELD_MAPPING = "field_map";
        public static final String SETTINGS = "settings";
        public static final String TO_SCOPES = "to_scopes";
        public static final String DATA_LANGUAGES = "data_languages";
        public static final String DATA_SCOPES = "data_scopes";
    }
}
