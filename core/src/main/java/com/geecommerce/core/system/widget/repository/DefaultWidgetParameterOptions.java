package com.geecommerce.core.system.widget.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.type.Id;

public class DefaultWidgetParameterOptions extends AbstractRepository implements WidgetParameterOptions {
    @Override
    public List<WidgetParameterOption> thatBelongTo(WidgetParameter widgetParameter) {
        return thatBelongTo(widgetParameter.getId());
    }

    @Override
    public List<WidgetParameterOption> thatBelongTo(Id widgetParameterId) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(WidgetParameterOption.Col.WIDGET_PARAMETER_ID, widgetParameterId);

        return find(WidgetParameterOption.class, filter);
    }
}
