package com.geecommerce.core.system.widget.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.widget.enums.WidgetParameterTabItemType;
import com.geecommerce.core.system.widget.enums.WidgetType;
import com.geecommerce.core.system.widget.repository.WidgetParameterTabs;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Model("widgets")
public class DefaultWidget extends AbstractModel implements Widget {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.TYPE)
    private WidgetType type = WidgetType.BACKEND;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.ICON)
    private String icon = null;

    @Column(Col.GROUP)
    private String group = null;

    @Column(Col.TABS)
    private List<Id> tabIds = null;

    @Column(Col.CONFIGURATION)
    private Map<String, Object> configuration = null;

    @Column(Col.CONTENT)
    private String content = null;

    private List<WidgetParameterTab> tabs = null;

    private List<WidgetParameter> parameters = null;

    private final WidgetParameterTabs widgetParameterTabs;

    @Inject
    public DefaultWidget(WidgetParameterTabs widgetParameterTabs) {
        this.widgetParameterTabs = widgetParameterTabs;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Widget setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public WidgetType getType() {
        return type;
    }

    @Override
    public Widget setType(WidgetType type) {
        this.type = type;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public Widget setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Widget setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public Widget setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Widget setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public List<WidgetParameterTab> getTabs() {
        if(tabs != null)
            return tabs;

        if(tabIds == null || tabIds.isEmpty())
            tabs = null;
        else {
            tabs = widgetParameterTabs.findByIds(WidgetParameterTab.class, tabIds.toArray(new Id[tabIds.size()]));
        }
        return tabs;
    }

    @Override
    public List<WidgetParameter> getParameters() {
        if(parameters != null)
            return parameters;
        if(getTabs() == null || getTabs().isEmpty())
            return null;

        parameters = gatherParameters(getTabs().get(0));

        return parameters;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    @Override
    public Widget setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Widget setContent(String content) {
        this.content = content;
        return this;
    }

    protected List<WidgetParameter> gatherParameters(WidgetParameterTab tab){
        List<WidgetParameter> params = new ArrayList<>();
        if(tab != null){
            for (WidgetParameterTabItem item: tab.getItems()){
                if(item.getType().equals(WidgetParameterTabItemType.PARAMETER)){
                    params.add((WidgetParameter) item.getItem());
                } else if(item.getType().equals(WidgetParameterTabItemType.TAB)){
                    params.addAll(gatherParameters((WidgetParameterTab) item.getItem()));
                }
            }
        }
        return params;
    }


}
