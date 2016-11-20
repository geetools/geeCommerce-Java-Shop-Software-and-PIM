package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.helper.ContentHelper;
import com.google.inject.Inject;

@Widget(name = "cms_link", cms = true, css = true)
public class CmsLinkWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_LINK = "link";
    private final String PARAM_TEXT = "text";

    private final ContentHelper contentHelper;

    @Inject
    public CmsLinkWidget(ContentHelper contentHelper) {
        this.contentHelper = contentHelper;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String link = widgetCtx.getParam(PARAM_LINK);
        String text = widgetCtx.getParam(PARAM_TEXT);

        if (!StringUtils.isBlank(link)) {
            widgetCtx.setParam("wLink", link);
        }

        if (!StringUtils.isBlank(text)) {
            widgetCtx.setParam("wText", text);
        }

        String style = contentHelper.generateStyle(widgetCtx, null);
        if (!StringUtils.isBlank(style)) {
            widgetCtx.setParam("wStyle", style);
        }

        widgetCtx.render();
    }

    protected String addToStyle(String style, String key, String value) {
        if (!StringUtils.isBlank(value)) {
            return style + key + ":" + value + ";";
        }
        return style;
    }
}
