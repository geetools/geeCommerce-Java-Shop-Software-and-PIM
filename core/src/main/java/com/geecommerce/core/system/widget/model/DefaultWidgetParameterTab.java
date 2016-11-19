package com.geecommerce.core.system.widget.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model("widget_parameter_tabs")
public class DefaultWidgetParameterTab extends AbstractModel implements WidgetParameterTab {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(name = Col.ITEMS, autoPopulate = false)
    private List<WidgetParameterTabItem> items = new ArrayList<>();

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WidgetParameterTab setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public WidgetParameterTab setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public WidgetParameterTab setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public List<WidgetParameterTabItem> getItems() {
        return items;
    }

    @Override
    public WidgetParameterTab setItems(List<WidgetParameterTabItem> items) {
        this.items = items;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        List<Map<String, Object>> itemsList = list_(map.get(Col.ITEMS));
        if (itemsList != null) {
            this.items = new ArrayList<>();
            for (Map<String, Object> item : itemsList) {
                WidgetParameterTabItem tabItem = app.getInjectable(WidgetParameterTabItem.class);
                tabItem.fromMap(item);
                this.items.add(tabItem);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (getItems() != null) {
            for (WidgetParameterTabItem tabItem : getItems()) {
                itemsList.add(tabItem.toMap());
            }
            map.put(Col.ITEMS, itemsList);
        }

        return map;
    }
}
