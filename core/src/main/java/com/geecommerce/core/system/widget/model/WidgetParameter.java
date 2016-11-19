package com.geecommerce.core.system.widget.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.widget.enums.WidgetParameterInputType;
import com.geecommerce.core.system.widget.enums.WidgetParameterType;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface WidgetParameter extends MultiContextModel {

    public Id getId();

    public WidgetParameter setId(Id id);

    public String getWidgetCode();

    public WidgetParameter setWidgetCode(String widgetCode);

    public String getCode();

    public WidgetParameter setCode(String code);

    public ContextObject<String> getLabel();

    public WidgetParameter setLabel(ContextObject<String> label);

    public Boolean isAutocomplete();

    public WidgetParameter setAutocomplete(Boolean autocomplete);

    public boolean isOption();

    public WidgetParameter setOption(boolean option);

    public WidgetParameterType getType();

    public WidgetParameter setType(WidgetParameterType type);

    public WidgetParameterInputType getInputType();

    public WidgetParameter setType(WidgetParameterInputType inputType);

    public String getDefaultValue();

    public WidgetParameter setDefaultValue(String defaultValue);

    public Double getMinValue();

    public WidgetParameter setMinValue(Double minValue);

    public Double getMaxValue();

    public WidgetParameter setMaxValue(Double maxValue);

    public Double getStep();

    public WidgetParameter setStep(Double step);

    static final class Col {
        public static final String ID = "_id";
        public static final String WIDGET_CODE = "w_code";
        public static final String CODE = "code";
        public static final String OPTION = "option";
        public static final String MANDATORY = "mandatory";
        public static final String LABEL = "label";
        public static final String AUTOCOMPLETE = "autocomplete";
        public static final String TYPE = "type";

        public static final String DEFAULT_VALUE = "default_value";
        public static final String MAX_VALUE = "max_value";
        public static final String MIN_VALUE = "min_value";
        public static final String STEP = "step_value";
    }
}
