package com.geecommerce.core.system.widget.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.widget.model.WidgetParameter;

import java.util.List;

public interface WidgetParameters extends Repository {

    public List<WidgetParameter> forWidget(String code);

}
