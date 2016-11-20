package com.geecommerce.catalog.product.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "addthis")
public class AddThisWidget extends AbstractWidgetController implements WidgetController {

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String enabledPlugins = app.cpStr_("product/sharebuttons");
        if (enabledPlugins != null && !enabledPlugins.equals("false") && !enabledPlugins.isEmpty()) {
            String[] enabledPluginsArray = getEnabledPluginsAsArray(enabledPlugins);
            widgetCtx.setParam("enabledPlugins", enabledPluginsArray);
        } else {
            widgetCtx.setParam("enabledPlugins", null);
        }

        widgetCtx.render("addthis/addthis");
    }

    private String[] getEnabledPluginsAsArray(String enabledPlugins) {
        enabledPlugins = enabledPlugins.trim();
        enabledPlugins = enabledPlugins.replaceAll(" ", "");
        enabledPlugins = enabledPlugins.toLowerCase();
        String[] enabledPluginsArray = enabledPlugins.split(",");
        return enabledPluginsArray;
    }
}
