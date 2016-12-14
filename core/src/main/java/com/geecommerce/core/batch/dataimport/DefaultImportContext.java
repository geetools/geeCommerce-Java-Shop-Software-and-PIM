package com.geecommerce.core.batch.dataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public class DefaultImportContext implements ImportContext {
    protected static final long serialVersionUID = 3655079124559178984L;
    protected String token = null;
    protected Map<String, String> data = null;
    protected Model model = null;
    protected ImportProfile importProfile = null;
    protected List<ImportMessage> importMessages = null;
    protected Map<String, String> properties = null;
    protected String fileName = null;
    protected long lineNumber = 0;
    private int buildCount = 0;

    @Override
    public ImportContext build(String token, Map<String, String> data, Model model, ImportProfile importProfile, List<ImportMessage> importMessages, String fileName, long lineNumber) {

        if (buildCount > 0)
            throw new IllegalStateException("ImportContext.buiild() should only be called once");

        this.token = token;
        this.data = data;
        this.model = model;
        this.importProfile = importProfile;
        this.importMessages = importMessages;
        this.fileName = fileName;
        this.lineNumber = lineNumber;

        buildCount++;

        return this;
    }

    @Override
    public String token() {
        return token;
    }

    @Override
    public Map<String, String> data() {
        return data;
    }

    @Override
    public String field(String key) {
        return data.get(key);
    }

    @Override
    public Id idField(String key) {
        String id = field(key);
        return id == null ? null : Id.valueOf(id);
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public ImportProfile importProfile() {
        return importProfile;
    }

    @Override
    public List<ImportMessage> importMessages() {
        return importMessages;
    }

    @Override
    public ImportContext add(ImportMessage importMessage) {
        if (importMessages == null)
            importMessages = new ArrayList<>();

        importMessages.add(importMessage);
        return this;
    }

    @Override
    public ImportContext property(String key, String value) {
        if (properties == null)
            properties = new HashMap<>();

        properties.put(key, value);
        return this;
    }

    @Override
    public String property(String key) {
        return properties.get(key);
    }

    @Override
    public String fileName() {
        return fileName;
    }

    @Override
    public long lineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "DefaultImportContext [token=" + token + ", data=" + data + ", model=" + model + ", importProfile=" + importProfile + ", importMessages=" + importMessages + ", fileName=" + fileName
            + ", lineNumber=" + lineNumber + "]";
    }
}
