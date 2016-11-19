package com.geecommerce.core.system.widget.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model(collection = "widget_parameter_options")
public class DefaultWidgetParameterOption extends AbstractMultiContextModel implements WidgetParameterOption {

    @Column(Col.ID)
    private Id id = null;
    @Column(Col.WIDGET_PARAMETER_ID)
    private Id widgetParameterId = null;
    @Column(Col.LABEL)
    private ContextObject<String> label = null;
    @Column(Col.VALUE)
    private String value = null;

    @Override
    public WidgetParameterOption setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getWidgetParameterId() {
	return widgetParameterId;
    }

    @Override
    public WidgetParameterOption setWidgetParameterId(Id widgetParameterId) {
	this.widgetParameterId = widgetParameterId;
	return this;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public WidgetParameterOption setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public String getValue() {
	return value;
    }

    @Override
    public WidgetParameterOption setValue(String value) {
	this.value = value;
	return this;
    }

    @Override
    public WidgetParameterOption belongsTo(WidgetParameter widgetParameter) {
	if (widgetParameter != null)
	    widgetParameterId = widgetParameter.getId();
	else
	    widgetParameterId = null;

	return this;
    }

    @Override
    public WidgetParameter getWidgetParameter() {
	return null;
    }

    @Override
    public Id getId() {
	return id;
    }
}
