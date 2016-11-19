package com.geecommerce.core.system.widget.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface WidgetParameterTab extends Model {

    public Id getId();

    public WidgetParameterTab setId(Id id);

    public String getCode();

    public WidgetParameterTab setCode(String code);

    ContextObject<String> getLabel();

    WidgetParameterTab setLabel(ContextObject<String> label);

    List<WidgetParameterTabItem> getItems();

    WidgetParameterTab setItems(List<WidgetParameterTabItem>  items);

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
        public static final String ITEMS = "items";
    }
}
