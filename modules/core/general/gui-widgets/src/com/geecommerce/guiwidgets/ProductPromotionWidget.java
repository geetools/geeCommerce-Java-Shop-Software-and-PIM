package com.geecommerce.guiwidgets;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.model.ProductPromotion;
import com.geecommerce.guiwidgets.service.ProductPromotionService;
import com.google.inject.Inject;

@Widget(name = "product_promotion", cms = false, css = true, js=true)
public class ProductPromotionWidget extends AbstractWidgetController implements WidgetController {
    private final ProductPromotionService productPromotionService;
    private final ProductListService productListService;
    private final String PARAM_KEY = "key";
    private final String PARAM_TEMPLATE = "template";

    @Inject
    public ProductPromotionWidget(ProductPromotionService productPromotionService, ProductListService productListService) {
        this.productPromotionService = productPromotionService;
        this.productListService = productListService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String promotionKey = widgetCtx.getParam(PARAM_KEY);
        widgetCtx.setParam("productPromotionKey", promotionKey);
        String promotionTemplate = widgetCtx.getParam(PARAM_TEMPLATE);
        if (promotionKey != null && !promotionKey.isEmpty() /*&& promotionTemplate != null && !promotionTemplate.isEmpty()*/) {
            List<ProductPromotion> productPromotions = productPromotionService.getProductPromotionByKey(promotionKey);
            if (productPromotions != null && productPromotions.size() != 0) {
                ProductPromotion productPromotion = productPromotions.get(0);
                if(productPromotion.getTargetObjectId() != null){
                    List<Product> products = productListService.getProducts(productListService.getProductList(productPromotion.getTargetObjectId()), true, productPromotion.getLimit());
                    if (products != null && products.size() != 0) {
                        widgetCtx.setParam("wProducts", products);

                    }
                }
                widgetCtx.setParam("wProductPromotion", productPromotion);
                widgetCtx.setJsParam("slidesToShow", productPromotion.getSlidesToShow());
            }
        }
        widgetCtx.render(); // "product_promotion/" + promotionTemplate);
    }
}