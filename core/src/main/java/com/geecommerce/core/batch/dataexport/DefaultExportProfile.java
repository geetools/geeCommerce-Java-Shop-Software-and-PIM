package com.geecommerce.core.batch.dataexport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("export_profiles")
public class DefaultExportProfile extends AbstractModel implements ExportProfile {
    private static final long serialVersionUID = 9037773558035360383L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.TARGET_OBJECT_ID)
    protected Id targetObjectId = null;

    @Column(Col.NAME)
    protected ContextObject<String> name;

    @Column(Col.FIELDS)
    protected List<ExportField> fields;

    @Column(Col.SETTINGS)
    protected Map<String, String> settings;

    @Column(Col.SCOPES)
    protected Set<Id> scopeIds;

    @Column(Col.LANGUAGES)
    protected Set<String> languages;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ExportProfile setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public ExportProfile setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public ExportProfile setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public List<ExportField> getFields() {
        return fields;
    }

    @Override
    public ExportProfile setFields(List<ExportField> fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public Map<String, String> getSettings() {
        return settings;
    }

    @Override
    public ExportProfile setSettings(Map<String, String> settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public String getSetting(String key) {
        return settings == null ? null : settings.get(key);
    }

    @Override
    public ExportProfile putSetting(String key, String value) {
        if (settings == null)
            settings = new HashMap<>();

        settings.put(key, value);

        return this;
    }

    @Override
    public Set<Id> getScopeIds() {
        return scopeIds;
    }

    @Override
    public ExportProfile setScopeIds(Set<Id> scopeIds) {
        this.scopeIds = scopeIds;
        return this;
    }

    @Override
    public ExportProfile addScopeId(Id scopeId) {
        if (scopeIds == null)
            scopeIds = new HashSet<>();

        scopeIds.add(scopeId);

        return this;
    }

    @Override
    public Set<String> getLanguages() {
        return languages;
    }

    @Override
    public ExportProfile setLanguages(Set<String> languages) {
        this.languages = languages;
        return this;
    }

    @Override
    public ExportProfile addLanguage(String language) {
        if (languages == null)
            languages = new HashSet<>();

        languages.add(language);

        return this;
    }

    @Override
    public String toString() {
        return "DefaultExportProfile [id=" + id + ", targetObjectId=" + targetObjectId + ", name=" + name + ", fields=" + fields + ", settings=" + settings + ", scopeIds=" + scopeIds + ", languages="
            + languages + "]";
    }
}
