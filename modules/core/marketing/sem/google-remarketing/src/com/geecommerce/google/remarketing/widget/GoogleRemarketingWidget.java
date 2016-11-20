package com.geecommerce.google.remarketing.widget;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.core.Char;
import com.geecommerce.core.RegistryKey;
import com.geecommerce.core.Str;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.price.pojo.PriceResult;
import com.google.inject.Inject;

@Widget(name = "google_remarketing")
public class GoogleRemarketingWidget extends AbstractWidgetController implements WidgetController {
    private static final String CONF_KEY_GOOGLE_REMARKETING_ID = "google/remarketing/conversion_id";
    private static final String CONF_KEY_GOOGLE_REMARKETING_LABEL = "google/remarketing/conversion_label";

    private static final String PARAM_CONVERSION_ID = "conversionId";
    private static final String PARAM_CONVERSION_LABEL = "conversionLabel";
    private static final String PARAM_PAGETYPE = "pageType";
    private static final String PARAM_PRODID = "prodId";
    private static final String PARAM_TOTALVALUE = "totalValue";

    private static final String PAGE_TYPE_HOME = "home";
    private static final String PAGE_TYPE_CATEGORY = "category";
    private static final String PAGE_TYPE_PRODUCT = "product";
    private static final String PAGE_TYPE_CART = "cart";
    private static final String PAGE_TYPE_PURCHASE = "purchase";
    private static final String PAGE_TYPE_SEARCHRESULTS = "searchresults";
    private static final String PAGE_TYPE_OTHER = "other";

    private static final String REQUEST_URI_EMPTY = "";
    private static final String REQUEST_URI_SLASH = "/";
    private static final String REQUEST_URI_HOME = "/home";

    private static final String REQUEST_URI_PRODUCT_LIST = "/catalog/product-list/view";
    private static final String REQUEST_URI_PRODUCT_VIEW = "/catalog/product/view";
    private static final String REQUEST_URI_CART = "/cart/view";

    private static final String TEMPLATE_HOME = "home";
    private static final String TEMPLATE_PRODUCT_VIEW = "catalog/product/view";
    private static final String TEMPLATE_PRODUCT_LIST = "catalog/product/list";
    private static final String TEMPLATE_CART_VIEW = "cart/view";
    private static final String TEMPLATE_SEARCH_RESULT = "catalog/search/result";
    private static final String TEMPLATE_CHECKOUT_SUCCESS = "checkout/success";

    private static final String VIEW = "google/remarketing/view";

    private final CheckoutService checkoutService;

    @Inject
    protected GoogleRemarketingWidget(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        try {
            String conversionId = app.cpStr_(CONF_KEY_GOOGLE_REMARKETING_ID);
            String conversionLabel = app.cpStr_(CONF_KEY_GOOGLE_REMARKETING_LABEL);

            if (!Str.isEmpty(conversionId) && !Str.isEmpty(conversionLabel)) {
                widgetCtx.setParam(PARAM_CONVERSION_ID, conversionId);
                widgetCtx.setParam(PARAM_CONVERSION_LABEL, conversionLabel);

                String pageType = null;
                String productId = null;
                Double totalPrice = 0D;

                // -----------------------------------------------------------------
                // Home.
                // -----------------------------------------------------------------
                if (isHome(request)) {
                    pageType = PAGE_TYPE_HOME;
                    productId = "''";
                }
                // -----------------------------------------------------------------
                // Product.
                // -----------------------------------------------------------------
                else if (isProduct(request)) {
                    pageType = PAGE_TYPE_PRODUCT;

                    Product product = (Product) request.getAttribute("product");

                    if (product != null) {
                        productId = new StringBuilder(Str.SINGLE_QUOTE).append(product.getId()).append(Str.SINGLE_QUOTE)
                            .toString();

                        PriceResult priceResult = product.getPrice();

                        if (priceResult != null) {
                            Double p = product.isVariantMaster() ? priceResult.getLowestFinalPrice()
                                : priceResult.getFinalPrice();
                            totalPrice = (p == null ? 0 : p);
                        }
                    }
                }
                // -----------------------------------------------------------------
                // Product-list / category.
                // -----------------------------------------------------------------
                else if (isProductList(request)) {
                    pageType = PAGE_TYPE_CATEGORY;

                    List<Product> products = (List<Product>) request.getAttribute("products");

                    StringBuilder productIds = new StringBuilder(Str.SQUARE_BRACKET_OPEN);

                    if (products != null && products.size() > 0) {
                        int x = 0;
                        for (Product product : products) {
                            if (x > 0)
                                productIds.append(Char.COMMA);

                            productIds.append(Char.SINGLE_QUOTE).append(product.getId()).append(Char.SINGLE_QUOTE);

                            PriceResult priceResult = product.getPrice();

                            if (priceResult != null) {
                                Double p = product.isVariantMaster() ? priceResult.getLowestFinalPrice()
                                    : priceResult.getFinalPrice();
                                totalPrice += (p == null ? 0 : p);
                            }

                            x++;
                        }
                    }

                    productIds.append(Char.SQUARE_BRACKET_CLOSE);

                    productId = productIds.toString();
                }
                // -----------------------------------------------------------------
                // Cart.
                // -----------------------------------------------------------------
                else if (isCart(request)) {
                    pageType = PAGE_TYPE_CART;

                    Cart cart = (Cart) request.getAttribute("cart");

                    StringBuilder productIds = new StringBuilder(Str.SQUARE_BRACKET_OPEN);

                    List<CartItem> cartItems = cart.getCartItems();

                    if (cartItems != null && cartItems.size() > 0) {
                        int x = 0;
                        for (CartItem cartItem : cartItems) {
                            if (x > 0)
                                productIds.append(Char.COMMA);

                            productIds.append(Char.SINGLE_QUOTE).append(cartItem.getProductId())
                                .append(Char.SINGLE_QUOTE);

                            Double p = cartItem.getProductPrice();
                            totalPrice += (p == null ? 0 : (cartItem.getProductPrice() * cartItem.getQuantity()));

                            x++;
                        }
                    }

                    productIds.append(Char.SQUARE_BRACKET_CLOSE);

                    productId = productIds.toString();
                }
                // -----------------------------------------------------------------
                // Checkout success / purchase.
                // -----------------------------------------------------------------
                else if (isPurchase()) {
                    pageType = PAGE_TYPE_PURCHASE;

                    Order order = getOrder(widgetCtx);

                    StringBuilder productIds = new StringBuilder(Str.SQUARE_BRACKET_OPEN);

                    if (order != null) {
                        List<OrderItem> orderItems = order.getOrderItems();

                        if (orderItems != null && orderItems.size() > 0) {
                            int x = 0;
                            for (OrderItem orderItem : orderItems) {
                                if (x > 0)
                                    productIds.append(Char.COMMA);

                                productIds.append(Char.SINGLE_QUOTE).append(orderItem.getProductId())
                                    .append(Char.SINGLE_QUOTE);

                                Double p = orderItem.getTotalRowPrice();
                                totalPrice += (p == null ? 0 : p);

                                x++;
                            }
                        }
                    }

                    productIds.append(Char.SQUARE_BRACKET_CLOSE);

                    productId = productIds.toString();
                }
                // -----------------------------------------------------------------
                // Search result.
                // -----------------------------------------------------------------
                else if (isSearchResult(request)) {
                    pageType = PAGE_TYPE_SEARCHRESULTS;

                    List<Product> products = (List<Product>) request.getAttribute("products");

                    StringBuilder productIds = new StringBuilder(Str.SQUARE_BRACKET_OPEN);

                    if (products != null && products.size() > 0) {
                        int x = 0;
                        for (Product product : products) {
                            if (x > 0)
                                productIds.append(Char.COMMA);

                            productIds.append(Char.SINGLE_QUOTE).append(product.getId()).append(Char.SINGLE_QUOTE);

                            PriceResult priceResult = product.getPrice();

                            if (priceResult != null) {
                                Double p = product.isVariantMaster() ? priceResult.getLowestFinalPrice()
                                    : priceResult.getFinalPrice();
                                totalPrice += (p == null ? 0 : p);
                            }

                            x++;
                        }
                    }

                    productIds.append(Char.SQUARE_BRACKET_CLOSE);

                    productId = productIds.toString();
                } else {
                    pageType = PAGE_TYPE_OTHER;
                    productId = "''";
                }

                widgetCtx.setParam(PARAM_PAGETYPE, pageType);
                widgetCtx.setParam(PARAM_PRODID, productId);
                widgetCtx.setParam(PARAM_TOTALVALUE, totalPrice);

                widgetCtx.render(VIEW);
            }
        }
        // We do not want tracking to cause a page not to be loaded.
        catch (Throwable t) {

        }
    }

    private Order getOrder(WidgetContext widgetCtx) {
        Id savedOrderId = widgetCtx.sessionGet(CheckoutConstant.SESSION_KEY_LAST_SAVED_ORDER);
        Order savedOrder = null;

        if (savedOrderId != null) {
            savedOrder = checkoutService.getOrder(savedOrderId);
        }

        return savedOrder;
    }

    private boolean isPurchase() {
        return TEMPLATE_CHECKOUT_SUCCESS.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }

    private boolean isSearchResult(HttpServletRequest request) {
        return TEMPLATE_SEARCH_RESULT.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }

    private boolean isCart(HttpServletRequest request) {
        return request.getRequestURI().startsWith(REQUEST_URI_CART) || app.getOriginalURI().startsWith(REQUEST_URI_CART)
            || TEMPLATE_CART_VIEW.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }

    private boolean isProductList(HttpServletRequest request) {
        return request.getRequestURI().startsWith(REQUEST_URI_PRODUCT_LIST)
            || app.getOriginalURI().startsWith(REQUEST_URI_PRODUCT_LIST)
            || TEMPLATE_PRODUCT_LIST.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }

    private boolean isProduct(HttpServletRequest request) {
        return request.getRequestURI().startsWith(REQUEST_URI_PRODUCT_VIEW)
            || app.getOriginalURI().startsWith(REQUEST_URI_PRODUCT_VIEW)
            || TEMPLATE_PRODUCT_VIEW.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }

    private boolean isHome(HttpServletRequest request) {
        return REQUEST_URI_SLASH.equals(app.getOriginalURI()) || REQUEST_URI_EMPTY.equals(app.getOriginalURI())
            || app.getOriginalURI().startsWith(REQUEST_URI_HOME)
            || TEMPLATE_HOME.equals(app.registryGet(RegistryKey.VIEW_PATH));
    }
}
