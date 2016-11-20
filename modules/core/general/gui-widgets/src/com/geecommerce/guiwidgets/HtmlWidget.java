package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "html")
public class HtmlWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_HTML = "html";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String html = widgetCtx.getParam(PARAM_HTML);

        widgetCtx.renderContent(html);

    }

}