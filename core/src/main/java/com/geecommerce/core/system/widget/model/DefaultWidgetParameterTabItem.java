package com.geecommerce.core.system.widget.model;

import java.util.Map;

import com.geecommerce.core.service.annotation.Injectable;
import com.geecommerce.core.system.widget.enums.WidgetParameterTabItemType;
import com.geecommerce.core.system.widget.repository.WidgetParameterTabs;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Injectable
public class DefaultWidgetParameterTabItem implements WidgetParameterTabItem {
    private static final long serialVersionUID = -756836049491579572L;

    private Id id = null;
    private WidgetParameterTabItemType type = null;
    private Object item = null;

    private final WidgetParameterTabs widgetParameterTabs;
    private final WidgetParameters widgetParameters;

    @Inject
    public DefaultWidgetParameterTabItem(WidgetParameterTabs widgetParameterTabs, WidgetParameters widgetParameters) {
        this.widgetParameterTabs = widgetParameterTabs;
        this.widgetParameters = widgetParameters;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WidgetParameterTabItem setId(Id id) {
        this.id = id;
        this.item = null;
        return this;
    }

    @Override
    public WidgetParameterTabItemType getType() {
        return type;
    }

    @Override
    public WidgetParameterTabItem setType(WidgetParameterTabItemType type) {
        this.type = type;
        this.item = null;
        return this;
    }

    @Override
    public Object getItem() {
        if (item != null)
            return item;

        if (id != null && type != null) {
            if (type == WidgetParameterTabItemType.PARAMETER) {
                item = widgetParameters.findById(WidgetParameter.class, id);
            } else if (type == WidgetParameterTabItemType.TAB) {
                item = widgetParameterTabs.findById(WidgetParameterTab.class, id);
            }
            return item;
        }
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        id = TypeConverter.asId(map.get(Col.ID));
        type = TypeConverter.asEnum(WidgetParameterTabItemType.class, map.get(Col.TYPE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put(Col.ID, getId());
        map.put(Col.TYPE, getType());
        return map;
    }
}
