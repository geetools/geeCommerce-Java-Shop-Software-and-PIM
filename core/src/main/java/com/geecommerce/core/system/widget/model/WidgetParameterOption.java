package com.geecommerce.core.system.widget.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface WidgetParameterOption extends MultiContextModel {

    public Id getId();

    public WidgetParameterOption setId(Id id);

    public Id getWidgetParameterId();

    public WidgetParameterOption setWidgetParameterId(Id widgetParameterId);

    public ContextObject<String> getLabel();

    public WidgetParameterOption setLabel(ContextObject<String> label);

    public String getValue();

    public WidgetParameterOption setValue(String value);

    public WidgetParameterOption belongsTo(WidgetParameter widgetParameter);

    @JsonIgnore
    public WidgetParameter getWidgetParameter();

    static final class Col {
        public static final String ID = "_id";
        public static final String WIDGET_PARAMETER_ID = "wp_id";
        public static final String LABEL = "label";
        public static final String VALUE = "val";
    }
}
