package com.geecommerce.google.analytics.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "ga_track_trans")
public class GoogleEcommerceTrackTransWidget extends AbstractWidgetController implements WidgetController {
    private static final String PARAM_ORDER = "order";
    private static final String VIEW = "google/track_trans/view";

    private final CheckoutService checkoutService;

    @Inject
    protected GoogleEcommerceTrackTransWidget(CheckoutService checkoutService) {
	this.checkoutService = checkoutService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	Order order = getOrder(widgetCtx);

	widgetCtx.setParam(PARAM_ORDER, order);

	widgetCtx.render(VIEW);
    }

    public Order getOrder(WidgetContext widgetCtx) {
	Id savedOrderId = widgetCtx.sessionGet(CheckoutConstant.SESSION_KEY_LAST_SAVED_ORDER);
	Order savedOrder = null;

	if (savedOrderId != null) {
	    savedOrder = checkoutService.getOrder(savedOrderId);
	}

	return savedOrder;
    }

}
