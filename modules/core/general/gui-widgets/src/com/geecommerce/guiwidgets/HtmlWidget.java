package com.geecommerce.guiwidgets;

import com.google.inject.Inject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.repository.Contents;
import com.geecommerce.guiwidgets.service.ContentService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Widget(name = "html")
public class HtmlWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_HTML = "html";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	String html = widgetCtx.getParam(PARAM_HTML);

	widgetCtx.renderContent(html);

    }

}