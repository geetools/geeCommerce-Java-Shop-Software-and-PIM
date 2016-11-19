package com.geecommerce.customerReview.widget;

import com.google.inject.Inject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.customerReview.service.CustomerReviewService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

@Widget(name = "customer_review_rating", js = true, css = true)
public class CustomerReviewRatingWidget extends AbstractWidgetController implements WidgetController {
    private final CustomerReviewService customerReviewService;

    private final String PARAM_PRODUCT_ID = "product_id";

    @Inject
    public CustomerReviewRatingWidget(CustomerReviewService customerReviewService) {
	this.customerReviewService = customerReviewService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	String productIdParam = widgetCtx.getParam(PARAM_PRODUCT_ID);
	Id productId = Id.toId(productIdParam);

	if (productIdParam != null && !productIdParam.isEmpty())
	    widgetCtx.setParam("productId", productIdParam);

	widgetCtx.setParam("average", String.format(Locale.ENGLISH, "%.2f", customerReviewService.averageRating(productId)));
	widgetCtx.setParam("total", customerReviewService.totalReviews(productId));
	widgetCtx.setParam("stars", customerReviewService.ratingsForProductReviews(productId));

	// widgetCtx.render("review/customer_review_rating");
	widgetCtx.render();

    }
}

/*
 * 
 * private WishListService wishListService = null;
 * 
 * @Inject public AddToWishListWidget(WishListService wishListService){ this.wishListService = wishListService; }
 * 
 * @Override public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext)
 * throws Exception { String productIdParam = widgetCtx.getParam(PARAM_PRODUCT_ID); if(productIdParam !=null && !productIdParam.isEmpty())
 * widgetCtx.setParam("productId", productIdParam);
 * 
 * if(app.isCustomerLoggedIn()){ widgetCtx.setParam("wishLists", wishListService.getWishLists(((Customer)app.getLoggedInCustomer()).getId())); }
 * widgetCtx.render("wishlist/add_to_wishlist"); }
 */