package com.geecommerce.core.system.widget.model;

import java.util.List;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("widget_groups")
public class DefaultWidgetGroup extends AbstractModel implements WidgetGroup {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.WIDGETS)
    private List<Id> widgetIds = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WidgetGroup setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public WidgetGroup setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public WidgetGroup setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public List<Id> getWidgetIds() {
        return widgetIds;
    }

    @Override
    public WidgetGroup setWidgetIds(List<Id> widgetIds) {
        this.widgetIds = widgetIds;
        return this;
    }

}
