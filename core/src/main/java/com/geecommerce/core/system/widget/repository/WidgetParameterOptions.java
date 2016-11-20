package com.geecommerce.core.system.widget.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.type.Id;

public interface WidgetParameterOptions extends Repository {

    public List<WidgetParameterOption> thatBelongTo(WidgetParameter widgetParameter);

    public List<WidgetParameterOption> thatBelongTo(Id widgetParameterId);

}
