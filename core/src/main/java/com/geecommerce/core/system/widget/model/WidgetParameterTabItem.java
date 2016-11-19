package com.geecommerce.core.system.widget.model;

import java.util.Map;

import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.system.widget.enums.WidgetParameterTabItemType;
import com.geecommerce.core.type.Id;

public interface WidgetParameterTabItem extends Injectable {

    public Id getId();

    public WidgetParameterTabItem setId(Id id);

    public WidgetParameterTabItemType getType();

    public WidgetParameterTabItem setType(WidgetParameterTabItemType type);

    public Object getItem();

    public void fromMap(Map<String, Object> map);

    public Map<String, Object> toMap();

    static final class Col {
        public static final String ID = "id";
        public static final String TYPE = "type";
    }
}
