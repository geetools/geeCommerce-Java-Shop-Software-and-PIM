package com.geecommerce.guiwidgets.helper;

import java.util.List;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.web.api.WidgetContext;

public interface ContentHelper extends Helper {

    public String generateStyle(WidgetContext widgetContext, List<String> styles);

}
