package com.geecommerce.core.system.widget.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.widget.model.WidgetParameter;

public interface WidgetParameters extends Repository {

    public List<WidgetParameter> forWidget(String code);

}
