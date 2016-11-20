package com.geecommerce.core.system.widget.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface WidgetGroup extends Model {

    public Id getId();

    public WidgetGroup setId(Id id);

    ContextObject<String> getLabel();

    WidgetGroup setLabel(ContextObject<String> label);

    public String getCode();

    public WidgetGroup setCode(String code);

    public List<Id> getWidgetIds();

    public WidgetGroup setWidgetIds(List<Id> ids);

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String WIDGETS = "widgets";
    }
}

// tabs