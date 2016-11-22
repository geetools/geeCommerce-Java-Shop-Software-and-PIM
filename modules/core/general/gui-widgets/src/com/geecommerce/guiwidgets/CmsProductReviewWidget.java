package com.geecommerce.guiwidgets;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.customerReview.service.CustomerReviewService;
import com.google.inject.Inject;

@Widget(name = "cms_product_review", cms = true, css = true)
public class CmsProductReviewWidget extends AbstractWidgetController implements WidgetController {

    private static final String PARAM_PRODUCT = "product_id";
    private static final String PARAM_VIEW = "view";

    private static final String STARS_TEMPLATE = "stars";

    private final ProductService productService;
    private final WidgetParameters widgetParameters;
    private final CustomerReviewService customerReviewService;

    @Inject
    public CmsProductReviewWidget(ProductService productService, WidgetParameters widgetParameters, CustomerReviewService customerReviewService) {
        this.productService = productService;
        this.widgetParameters = widgetParameters;
        this.customerReviewService = customerReviewService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        String view = widgetCtx.getParam(PARAM_VIEW);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = productService.getProduct(id);
            widgetCtx.setParam("wProduct", product);

            if (STARS_TEMPLATE.equals(view)) {
                widgetCtx.setParam("average",
                    String.format(Locale.ENGLISH, "%.2f", customerReviewService.averageRating(id)));
                widgetCtx.setParam("total", customerReviewService.totalReviews(id));
                widgetCtx.setParam("stars", customerReviewService.ratingsForProductReviews(id));
            }
        }

        widgetCtx.render();
    }
}
