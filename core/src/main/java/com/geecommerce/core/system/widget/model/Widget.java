package com.geecommerce.core.system.widget.model;


import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.widget.enums.WidgetType;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.List;
import java.util.Map;

public interface Widget extends Model {

    public Id getId();

    public Widget setId(Id id);

    public WidgetType getType();

    public Widget setType(WidgetType type);

    ContextObject<String> getLabel();

    Widget setLabel(ContextObject<String> label);

    public String getCode();

    public Widget setCode(String code);

    public String getIcon();

    public Widget setIcon(String icon);

    public String getGroup();

    public Widget setGroup(String group);

    public List<WidgetParameterTab> getTabs();

    public List<WidgetParameter> getParameters();

    public Map<String, Object> getConfiguration();

    public Widget setConfiguration(Map<String, Object> configuration);

    public String getContent();

    public Widget setContent(String content);

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String ICON = "icon";
        public static final String GROUP = "group";
        public static final String TABS = "tabs";
        public static final String TYPE = "type";
        public static final String CONFIGURATION = "config";
        public static final String CONTENT ="content";
    }
}


// tabs