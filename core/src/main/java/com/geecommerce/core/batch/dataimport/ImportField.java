package com.geecommerce.core.batch.dataimport;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;

public interface ImportField extends Model {
    public ContextObject<String> getLabel();

    public ImportField setLabel(ContextObject<String> label);

    public String getFieldExpression();

    public ImportField setFieldExpression(String fieldExpression);

    public String getSourceFormat();

    public ImportField setSourceFormat(String sourceFormat);

    public static class Col {
        public static String LABEL = "label";
        public static String FIELD_EXPRESSION = "expr";
        public static String SOURCE_FORMAT = "src_format";
    }
}
