package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("import_field_scriptlets")
public class DefaultImportFieldScriptlet extends AbstractModel implements ImportFieldScriptlet {
    private static final long serialVersionUID = -4460138528948832699L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.TARGET_OBJECT_ID)
    protected Id targetObjectId = null;

    @Column(Col.FIELD_NAME)
    protected String fieldName = null;

    @Column(Col.SCRIPT)
    protected String script = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ImportFieldScriptlet setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public ImportFieldScriptlet setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public ImportFieldScriptlet setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public ImportFieldScriptlet setScript(String script) {
        this.script = script;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultImportFieldScript [id=" + id + ", fieldName=" + fieldName + ", script=" + script + "]";
    }
}
