package com.geecommerce.core.system.widget.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.widget.enums.WidgetParameterInputType;
import com.geecommerce.core.system.widget.enums.WidgetParameterType;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geemvc.converter.adapter.DoubleArrayConverterAdapter;

@Model(collection = "widget_parameters")
public class DefaultWidgetParameter extends AbstractMultiContextModel implements WidgetParameter {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.WIDGET_CODE)
    private String widgetCode = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.AUTOCOMPLETE)
    private Boolean autocomplete = null;

    @Column(Col.OPTION)
    private boolean option = false;

    @Column(Col.TYPE)
    private WidgetParameterType type = null;

    @Column(Col.DEFAULT_VALUE)
    private String defaultValue = null;

    @Column(Col.MAX_VALUE)
    private Double maxValue = null;

    @Column(Col.MIN_VALUE)
    private Double minValue = null;

    @Column(Col.STEP)
    private Double step = null;

    @Override
    public WidgetParameter setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getWidgetCode() {
        return widgetCode;
    }

    @Override
    public WidgetParameter setWidgetCode(String widgetCode) {
        this.widgetCode = widgetCode;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public WidgetParameter setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public WidgetParameter setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public Boolean isAutocomplete() {
        return autocomplete;
    }

    @Override
    public WidgetParameter setAutocomplete(Boolean autocomplete) {
        this.autocomplete = autocomplete;
        return this;
    }

    @Override
    public boolean isOption() {
        return option;
    }

    @Override
    public WidgetParameter setOption(boolean option) {
        this.option = option;
        return this;
    }

    @Override
    public WidgetParameterType getType() {
        return type;
    }

    @Override
    public WidgetParameter setType(WidgetParameterType type) {
        this.type = type;
        return this;
    }

    @Override
    public WidgetParameterInputType getInputType() {
        return null;
    }

    @Override
    public WidgetParameter setType(WidgetParameterInputType inputType) {
        return null;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public WidgetParameter setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public Double getMinValue() {
        return minValue;
    }

    @Override
    public WidgetParameter setMinValue(Double minValue) {
        this.minValue = minValue;
        return this;
    }

    @Override
    public Double getMaxValue() {
        return maxValue;
    }

    @Override
    public WidgetParameter setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    @Override
    public Double getStep() {
        return step;
    }

    @Override
    public WidgetParameter setStep(Double step) {
        this.step = step;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }
}
