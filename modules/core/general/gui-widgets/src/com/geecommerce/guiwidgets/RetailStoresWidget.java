package com.geecommerce.guiwidgets;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.RetailStoreService;
import com.google.inject.Inject;

@Widget(name = "retail_stores")
public class RetailStoresWidget extends AbstractWidgetController implements WidgetController {
    private final RetailStoreService retailStoreService;

    @Inject
    public RetailStoresWidget(RetailStoreService retailStoreService) {
        this.retailStoreService = retailStoreService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        List<RetailStore> retailStores = retailStoreService.getEnabledRetailStores();

        widgetCtx.setParam("retailStores", retailStores);
        widgetCtx.render("retail_stores/retail_stores");
    }
}
