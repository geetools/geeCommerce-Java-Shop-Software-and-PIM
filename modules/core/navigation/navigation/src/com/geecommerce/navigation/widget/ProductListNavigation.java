package com.geecommerce.navigation.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.service.NavigationService;

@Widget(name = "product_list_navigation")
public class ProductListNavigation extends AbstractWidgetController implements WidgetController {
    private final NavigationService navigationService;

    @Inject
    public ProductListNavigation(NavigationService navigationService) {
	this.navigationService = navigationService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	// Fetch options
	String key = widgetCtx.getParam("key");
	Id productListId = widgetCtx.getParam("product_list_id", Id.class);
	String customTemplate = widgetCtx.getParam("template");

	NavigationItem rootNavItem = null;

	if (key != null && !key.isEmpty())
	    rootNavItem = navigationService.getNavigationItemByKey(key);

	if (rootNavItem == null) {
	    rootNavItem = navigationService.findRootNavigationItem();
	}

	if (rootNavItem != null) {
	    NavigationItem navItem = navigationService.getNavigationItemByTargetObject(ObjectType.PRODUCT_LIST, productListId, rootNavItem.getId());
	    widgetCtx.setParam("navItem", navItem);
	}

	if (customTemplate != null) {
	    widgetCtx.render("navigation/" + customTemplate);
	} else {
	    widgetCtx.render("navigation/navigation_product_list");
	}
    }

}
