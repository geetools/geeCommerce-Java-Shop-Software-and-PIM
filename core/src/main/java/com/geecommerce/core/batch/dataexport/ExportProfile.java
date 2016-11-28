package com.geecommerce.core.batch.dataexport;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ExportProfile {
    Id getId();

    ExportProfile setId(Id id);

    Id getTargetObjectId();

    ExportProfile setTargetObjectId(Id targetObjectId);

    ContextObject<String> getName();

    ExportProfile setName(ContextObject<String> name);

    List<ExportField> getFields();

    ExportProfile setFields(List<ExportField> fields);

    Map<String, String> getSettings();

    ExportProfile setSettings(Map<String, String> settings);

    String getSetting(String key);

    ExportProfile putSetting(String key, String value);

    Set<Id> getScopeIds();

    ExportProfile setScopeIds(Set<Id> scopeIds);

    ExportProfile addScopeId(Id scopeId);

    Set<String> getLanguages();

    ExportProfile setLanguages(Set<String> languages);

    ExportProfile addLanguage(String language);

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String NAME = "name";
        public static final String FIELDS = "fields";
        public static final String SETTINGS = "settings";
        public static final String SCOPES = "scopes";
        public static final String LANGUAGES = "languages";
    }
}
