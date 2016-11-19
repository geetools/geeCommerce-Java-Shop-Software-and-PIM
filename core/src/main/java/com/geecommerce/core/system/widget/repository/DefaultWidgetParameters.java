package com.geecommerce.core.system.widget.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.system.widget.model.WidgetParameter;

public class DefaultWidgetParameters extends AbstractRepository implements WidgetParameters {
    @Override
    public List<WidgetParameter> forWidget(String code) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(WidgetParameter.Col.WIDGET_CODE, code);

        return find(WidgetParameter.class, filter);
    }
}
