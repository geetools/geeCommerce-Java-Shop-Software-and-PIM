package com.geecommerce.retail.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.retail.model.RetailStoreInventory;
import com.geecommerce.retail.service.AvailabilityTextService;
import com.geecommerce.retail.service.RetailStoreInventoryService;
import com.geecommerce.retail.service.RetailStoreService;
import com.google.inject.Inject;

@Widget(name = "retail_store_inventory")
public class RetailStoreInventoryWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_PRODUCT_ID = "product_id";
    List<RetailStoreInventoryView> retailStoreInventoryViews = null;
    private final RetailStoreInventoryService retailStoreInventoryService;
    private final RetailStoreService retailStoreService;
    private final AvailabilityTextService availabilityTextService;

    @Inject
    public RetailStoreInventoryWidget(RetailStoreInventoryService retailStoreInventoryService,
        AvailabilityTextService availabilityTextService, RetailStoreService retailStoreService) {
        this.retailStoreInventoryService = retailStoreInventoryService;
        this.availabilityTextService = availabilityTextService;
        this.retailStoreService = retailStoreService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        retailStoreInventoryViews = new ArrayList<>();
        String productIdParam = widgetCtx.getParam(PARAM_PRODUCT_ID);
        List<RetailStoreInventory> retailStoreInventories = new ArrayList<>();

        if (productIdParam != null && !productIdParam.isEmpty()) {
            Id productId = new Id(productIdParam);
            retailStoreInventories = retailStoreInventoryService.getRetailStoreInventoriesByProductId(productId);
        }

        if (retailStoreInventories != null && !retailStoreInventories.isEmpty()) {
            for (RetailStoreInventory retailStoreInventory : retailStoreInventories) {
                String retailStoreTitle = retailStoreService.getRetailStore(retailStoreInventory.getRetailStoreId())
                    .getTitle();
                // String availibilityText =
                // availabilityTextService.getAvailabilityText(retailStoreInventory.getAvailabilityTextId()).getText();
                RetailStoreInventoryView retailStoreInventoryView = new RetailStoreInventoryView();
                retailStoreInventoryView.setRetailStoreTitle(retailStoreTitle)
                    .setQuantity(retailStoreInventory.getQuantity());
                retailStoreInventoryViews.add(retailStoreInventoryView);
            }
        }

        if (retailStoreInventoryViews != null && !retailStoreInventoryViews.isEmpty())
            widgetCtx.setParam("retailStoreInventoryViews", retailStoreInventoryViews);

        widgetCtx.render("retail_store_inventory/retail_store_inventory");
    }

    public class RetailStoreInventoryView {
        private String retailStoreTitle;
        private String availibilityText;
        private Integer quantity;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getRetailStoreTitle() {
            return retailStoreTitle;
        }

        public RetailStoreInventoryView setRetailStoreTitle(String retailStoreTitle) {
            this.retailStoreTitle = retailStoreTitle;
            return this;
        }

        public String getAvailibilityText() {
            return availibilityText;
        }

        public RetailStoreInventoryView setAvailibilityText(String availibilityText) {
            this.availibilityText = availibilityText;
            return this;
        }
    }
}
