package com.geecommerce.core.batch.dataimport;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class DefaultImportField extends AbstractModel implements ImportField {
    private static final long serialVersionUID = 6105255191915577141L;

    protected ContextObject<String> label = null;

    protected String fieldExpression;

    protected String sourceFormat;

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ImportField setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getFieldExpression() {
        return fieldExpression;
    }

    @Override
    public ImportField setFieldExpression(String fieldExpression) {
        this.fieldExpression = fieldExpression;
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
    public void fromMap(Map<String, Object> map) {
        this.label = ctxObj_(map.get(Col.LABEL));
        this.fieldExpression = str_(map.get(Col.FIELD_EXPRESSION));
        this.sourceFormat = str_(map.get(Col.SOURCE_FORMAT));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put(Col.LABEL, getLabel());
        data.put(Col.FIELD_EXPRESSION, getFieldExpression());
        data.put(Col.SOURCE_FORMAT, getSourceFormat());

        return data;
    }

    @Override
    public String toString() {
        return "DefaultImportField [label=" + label + ", fieldExpression=" + fieldExpression + ", sourceFormat=" + sourceFormat + "]";
    }
}
