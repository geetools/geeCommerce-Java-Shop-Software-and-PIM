package com.geecommerce.core.batch.dataimport.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class DefaultImportField extends AbstractModel implements ImportField {
    private static final long serialVersionUID = 6105255191915577141L;

    protected String sourceColumnHeader = null;

    protected ContextObject<String> sourceColumnLabel = null;

    protected String sourceFormat;

    protected String destFieldExpression;

    protected boolean attribute;

    @Override
    public Id getId() {
        return null;
    }

    public String getSourceColumnHeader() {
        return sourceColumnHeader;
    }

    public ImportField setSourceColumnHeader(String sourceColumnHeader) {
        this.sourceColumnHeader = sourceColumnHeader;
        return this;
    }

    public ContextObject<String> getSourceColumnLabel() {
        return sourceColumnLabel;
    }

    public ImportField setSourceColumnLabel(ContextObject<String> sourceColumnLabel) {
        this.sourceColumnLabel = sourceColumnLabel;
        return this;
    }

    @Override
    public String getSourceFormat() {
        return sourceFormat;
    }

    @Override
    public ImportField setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
        return this;
    }

    @Override
    public String getDestFieldExpression() {
        return destFieldExpression;
    }

    @Override
    public ImportField setDestFieldExpression(String destFieldExpression) {
        this.destFieldExpression = destFieldExpression;
        return this;
    }

    @Override
    public boolean isAttribute() {
        return attribute;
    }

    @Override
    public ImportField setAttribute(boolean attribute) {
        this.attribute = attribute;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.sourceColumnHeader = str_(map.get(Col.SOURCE_COLUMN_HEADER));
        this.sourceColumnLabel = ctxObj_(map.get(Col.SOURCE_COLUMN_LABEL));
        this.destFieldExpression = str_(map.get(Col.DEST_FIELD_EXPRESSION));
        this.sourceFormat = str_(map.get(Col.SOURCE_FORMAT));
        this.attribute = bool_(map.get(Col.IS_ATTRIBUTE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put(Col.SOURCE_COLUMN_HEADER, getSourceColumnHeader());
        data.put(Col.SOURCE_COLUMN_LABEL, getSourceColumnLabel());
        data.put(Col.SOURCE_FORMAT, getSourceFormat());
        data.put(Col.DEST_FIELD_EXPRESSION, getDestFieldExpression());
        data.put(Col.IS_ATTRIBUTE, isAttribute());

        return data;
    }

    @Override
    public String toString() {
        return "DefaultImportField [sourceColumnHeader=" + sourceColumnHeader + ", sourceColumnLabel=" + sourceColumnLabel + ", sourceFormat=" + sourceFormat + ", destFieldExpression="
            + destFieldExpression + ", attribute=" + attribute + "]";
    }
}
