package com.geecommerce.calculation.model;

import com.geecommerce.calculation.CalculationType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CalculationScriptlet extends Model {
    public Id getId();

    public CalculationScriptlet setId(Id id);

    public CalculationType getType();

    public CalculationScriptlet setType(CalculationType type);

    public String getCode();

    public CalculationScriptlet setCode(String code);

    public ContextObject<String> getLabel();

    public CalculationScriptlet setLabel(ContextObject<String> label);

    public String getBody();

    public CalculationScriptlet setBody(String body);

    public boolean isValid();

    static final class Column {
        public static final String ID = "_id";
        public static final String TYPE = "type";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String BODY = "body";
    }
}
