package com.geecommerce.core.batch.dataimport.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;

public interface ImportField extends Model {
    String getSourceColumnHeader();

    ImportField setSourceColumnHeader(String sourceColumnHeader);

    ContextObject<String> getSourceColumnLabel();

    ImportField setSourceColumnLabel(ContextObject<String> sourceColumnLabel);

    String getSourceFormat();

    ImportField setSourceFormat(String sourceFormat);

    String getDestFieldExpression();

    ImportField setDestFieldExpression(String destFieldExpression);

    boolean isAttribute();

    ImportField setAttribute(boolean attribute);

    public static class Col {
        public static String SOURCE_COLUMN_HEADER = "src_header";
        public static String SOURCE_COLUMN_LABEL = "src_label";
        public static String SOURCE_FORMAT = "src_format";
        public static String DEST_FIELD_EXPRESSION = "dest_expr";
        public static String IS_ATTRIBUTE = "is_attr";
    }
}
