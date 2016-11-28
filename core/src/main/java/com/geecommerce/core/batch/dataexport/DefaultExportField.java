package com.geecommerce.core.batch.dataexport;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class DefaultExportField extends AbstractModel implements ExportField {
    private static final long serialVersionUID = 6105255191915577141L;

    protected ContextObject<String> label = null;

    protected String fieldExpression;

    protected String targetFormat;

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ExportField setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getFieldExpression() {
        return fieldExpression;
    }

    @Override
    public ExportField setFieldExpression(String fieldExpression) {
        this.fieldExpression = fieldExpression;
        return this;
    }

    @Override
    public String getTargetFormat() {
        return targetFormat;
    }

    @Override
    public ExportField setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.label = ctxObj_(map.get(Col.LABEL));
        this.fieldExpression = str_(map.get(Col.FIELD_EXPRESSION));
        this.targetFormat = str_(map.get(Col.TARGET_FORMAT));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put(Col.LABEL, getLabel());
        data.put(Col.FIELD_EXPRESSION, getFieldExpression());
        data.put(Col.TARGET_FORMAT, getTargetFormat());

        return data;
    }

    @Override
    public String toString() {
        return "DefaultExportField [label=" + label + ", fieldExpression=" + fieldExpression + ", targetFormat=" + targetFormat + "]";
    }
}
