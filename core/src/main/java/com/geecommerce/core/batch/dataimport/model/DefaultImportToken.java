package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("import_tokens")
public class DefaultImportToken extends AbstractModel implements ImportToken {
    private static final long serialVersionUID = 341972535760690542L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.TARGET_OBJECT_ID)
    protected Id targetObjectId = null;

    @Column(Col.TOKEN)
    protected String token;

    @Column(Col.FILE_PATH)
    protected String filePath;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ImportToken setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public ImportToken setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public ImportToken setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public ImportToken setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultImportToken [id=" + id + ", targetObjectId=" + targetObjectId + ", token=" + token + ", filePath=" + filePath + "]";
    }
}
