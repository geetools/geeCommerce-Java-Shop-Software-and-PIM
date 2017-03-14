package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

import java.util.List;

@Widget(name = "cms_product_list_promotion", cms = true, js=true, css = true)
public class CmsProductListPromotionWidget extends AbstractWidgetController implements WidgetController {

    private final ProductListService productListService;

    private final String PARAM_PRODUCT_LIST = "product_list_id";
    private final String PARAM_TITLE = "title";
    private final String PARAM_SLIDES_TOTAL = "slides_total";
    private final String PARAM_SLIDES_TO_SHOW = "slides_to_show";
    private final String PARAM_SLIDES_TO_SCROLL = "slides_to_scroll";

    @Inject
    public CmsProductListPromotionWidget(ProductListService productListService) {
        this.productListService = productListService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String productListId = widgetCtx.getParam(PARAM_PRODUCT_LIST);

        String slidesToShow = widgetCtx.getParam(PARAM_SLIDES_TO_SHOW);
        String slidesToScroll = widgetCtx.getParam(PARAM_SLIDES_TO_SCROLL);
        String slidesTotal = widgetCtx.getParam(PARAM_SLIDES_TOTAL);
        String title = widgetCtx.getParam(PARAM_TITLE);

        if (productListId != null && !productListId.isEmpty()) {
            ProductList productList = productListService.getProductList(Id.parseId(productListId));
            if(productList != null) {
                List<Product> products = productListService.getProducts(productList, true, Integer.parseInt(slidesTotal));

                if (title != null && !title.isEmpty()) {
                    widgetCtx.setParam("wTitle", title);
                } else {
                    widgetCtx.setParam("wTitle", productList.getLabel().str());
                }

                if (products != null && products.size() != 0) {
                    widgetCtx.setParam("wProducts", products);
                }
                widgetCtx.setJsParam("slidesToShow", slidesToShow);
                widgetCtx.setJsParam("slidesToScroll", slidesToScroll);
            }
        }

        widgetCtx.render();

    }
}
