package com.geecommerce.guiwidgets.helper;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.widget.model.Widget;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.guiwidgets.model.Content;

import java.util.List;

public interface ContentHelper extends Helper {

    public String generateStyle(WidgetContext widgetContext, List<String> styles);

}
