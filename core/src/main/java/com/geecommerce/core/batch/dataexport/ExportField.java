package com.geecommerce.core.batch.dataexport;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;

public interface ExportField extends Model {
    public ContextObject<String> getLabel();

    public ExportField setLabel(ContextObject<String> label);

    public String getFieldExpression();

    public ExportField setFieldExpression(String fieldExpression);

    public String getTargetFormat();

    public ExportField setTargetFormat(String targetFormat);

    public static class Col {
        public static String LABEL = "label";
        public static String FIELD_EXPRESSION = "expr";
        public static String TARGET_FORMAT = "tar_format";
    }
}
