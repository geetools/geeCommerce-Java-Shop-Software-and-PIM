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

@Widget(name = "cms_spacer", cms = true, css = true)
public class CmsSpacerWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_COLOR = "css_background_color";
    private final String PARAM_HEIGHT = "css_height";

    private final ContentHelper contentHelper;

    @Inject
    public CmsSpacerWidget(ContentHelper contentHelper) {
        this.contentHelper = contentHelper;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {

        String style = contentHelper.generateStyle(widgetCtx, null);

        if (!StringUtils.isBlank(style)) {
            widgetCtx.setParam("wStyle", style);
        }

        widgetCtx.render();
    }

}
