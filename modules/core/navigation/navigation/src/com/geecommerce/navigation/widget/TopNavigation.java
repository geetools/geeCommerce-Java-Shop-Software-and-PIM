package com.geecommerce.navigation.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.navigation.service.NavigationService;
import com.google.inject.Inject;

@Widget(name = "topnav")
public class TopNavigation extends AbstractWidgetController implements WidgetController {
    private final NavigationService navigationService;

    @Inject
    public TopNavigation(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        // // Get top-navigation items
        // List<NavigationItem> topNavigationItems =
        // navigationService.getTopNavigationItems(widgetCtx.getMerchant(),
        // widgetCtx.getStore());
        //
        // // Add top-navigation items to widget-context
        // widgetCtx.setParam("topNav", topNavigationItems);

        // Render with template topnav.ftl
        widgetCtx.render("navigation/topnav");
    }
}
