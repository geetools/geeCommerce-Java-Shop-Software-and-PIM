package com.geecommerce.cart.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.cart.configuration.Key;
import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.model.BundleGroupItem;
import com.geecommerce.catalog.product.model.BundleProductItem;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.checkout.flow.helper.CheckoutFlowHelper;
import com.geecommerce.checkout.flow.model.CheckoutFlowStep;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.retail.service.RetailStoreInventoryService;
import com.geecommerce.retail.service.RetailStoreService;
import com.geecommerce.shipping.service.ShippingService;
import com.geemvc.HttpMethod;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/cart")
public class CartController extends BaseController {
    // protected Id productId = null;
    // protected Id variantId = null;

    protected final CartService cartService;
    protected final CartHelper cartHelper;
    protected final ProductService productService;
    protected final ProductHelper productHelper;
    protected final ShippingService shippingService;

    protected final RetailStoreInventoryService retailStoreInventoryService;
    protected final RetailStoreService retailStoreService;
    protected final CouponService couponService;

    protected final CatalogMediaHelper catalogMediaHelper;

    protected final CheckoutFlowHelper checkoutFlowHelper;

    // variant logic
    // protected Map<Id, Map<String, Object>> variantsAsMap = null;
    // protected String variantsAsJSON = null;
    //
    // protected List<ProductAvailability> productAvailabilities = null;
    // protected Boolean available = null;
    //
    // protected String couponCode = null;
    // protected Integer quantity = null;
    //
    // public CouponCode autoCoupon = null;
    // public List<CouponCode> autoCoupons = null;
    // public Boolean useAutoCoupon = null;
    // public Id selectedAutoCoupon = null;
    //
    // protected List<String> errors = new ArrayList<>();

    @Inject
    public CartController(CartService cartService, CartHelper cartHelper, ProductService productService,
        ProductHelper productHelper, ShippingService shippingService, CalculationService calculationService,
        CouponService couponService, CalculationHelper calculationHelper, RetailStoreService retailStoreService,
        RetailStoreInventoryService retailStoreInventoryService, CatalogMediaHelper catalogMediaHelper,
        CheckoutFlowHelper checkoutFlowHelper) {
        this.cartService = cartService;
        this.cartHelper = cartHelper;
        this.productService = productService;
        this.productHelper = productHelper;
        this.shippingService = shippingService;
        this.couponService = couponService;
        this.retailStoreService = retailStoreService;
        this.retailStoreInventoryService = retailStoreInventoryService;
        this.catalogMediaHelper = catalogMediaHelper;
        this.checkoutFlowHelper = checkoutFlowHelper;
    }

    @Request("view")
    public Result view() throws Exception {
        Cart cart = cartHelper.getCart();
        List<CartItem> cartItems = cart.getCartItems();

        Set<CartItem> cartItemsToRemove = null;
        Map<Id, Map<String, Object>> variantsAsMap = null;
        String variantsAsJSON = null;

        if (!cartItems.isEmpty()) {
            cartItemsToRemove = new HashSet<>();
            variantsAsMap = new HashMap<>();

            for (CartItem cartItem : cart.getCartItems()) {
                try {
                    Product p = cartItem.getProduct();

                    if (p == null || p.getId() == null || !p.isVisible() || p.isDeleted()) {
                        cartItemsToRemove.add(cartItem);
                        continue;
                    }

                    Id productId = p.getId();

                    if (p.isVariant())
                        p = p.getParent();

                    if (p == null || p.getId() == null /*
                                                        * || !p.isVisible() ||
                                                        * p.isDeleted()
                                                        */) {
                        cartItemsToRemove.add(cartItem);
                        continue;
                    }

                    if (p.isVariantMaster()) {
                        Map<String, Object> variants = productHelper.toVariantsMap(p);

                        if (variants != null && variants.size() > 0) {
                            variantsAsMap.put(productId, variants);
                        }
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                    // We do not want the whole cart to fail here, so we attempt
                    // just to remove the item that caused the error.
                    cartItemsToRemove.add(cartItem);
                    continue;
                }
            }

            if (!cartItemsToRemove.isEmpty()) {

                System.out.println(
                    "Removing " + cartItemsToRemove.size() + " out of " + cartItems.size() + " cart items. Items ("
                        + ") may either have caused an error, haven been deleted or are not visible.");
                cartItems.removeAll(cartItemsToRemove);
                cart.clearTotals();
                cartService.updateCart(cart);
            }

            variantsAsJSON = Json.toJson(variantsAsMap).replace("\\\"", "\\\\\"");
        }

        return view("cart/view").bind("cart", cart).bind("variantsAsJSON", variantsAsJSON)
            .bind("variantsAsMap", variantsAsMap).bind("checkoutAction", getCheckoutAction());
    }

    @Request(value = "add", method = HttpMethod.POST)
    public Result add(@Param("productId") Id productId, @Param("quantity") Integer quantity) {
        Cart cart = cartHelper.getCart(true);

        Product p = productService.getProduct(productId);

        if (quantity == null || quantity < 1)
            quantity = 1;

        for (int i = 0; i < quantity; i++)
            cart.addProduct(p);

        cartService.updateCart(cart);

        return redirect("http://" + app.servletRequest().getServerName() + ":" + app.servletRequest().getServerPort() + "/cart/view/");
    }

    @Request(value = "add2", method = HttpMethod.POST)
    public Result add(@Param("productId") Id bundleId, @Param("productIds") String productIds, @Param("quantities") String quantities) {
        Cart cart = cartHelper.getCart(true);

        cart.getCartItems().clear();

        Product bundle = productService.getProduct(bundleId);

        Iterator<String> iterProducts = new ArrayList<String>(Arrays.asList(productIds.split(","))).iterator(); // productIds.split(",")).iterator();
        Iterator<String> iterQuantities = new ArrayList<String>(Arrays.asList(quantities.split(","))).iterator(); // quantities.split(",")).iterator();

        while (iterProducts.hasNext() && iterQuantities.hasNext()) {
            Id productId = Id.parseId(iterProducts.next());
            Integer quantity = Integer.parseInt(iterQuantities.next());

            if (quantity == null || quantity < 1)
                quantity = 1;

            for (int i = 0; i < quantity; i++)
                cart.addProduct(productService.getProduct(productId), bundle);

        }

        for (BundleGroupItem bundleGroup : bundle.getBundleGroups()) {
            if (!bundleGroup.getShowInProductDetails()) {
                for (BundleProductItem productItem : bundleGroup.getValidBundleItemsForSelling()) {
                    if (productItem.isSelected()) {
                        Id productId = productItem.getProductId();
                        Integer quantity = productItem.getQuantity();

                        if (quantity == null || quantity < 1)
                            quantity = 1;

                        for (int i = 0; i < quantity; i++)
                            cart.addProduct(productItem.getProduct(), bundle);
                    }
                }

            }

        }

        cartService.updateCart(cart);

        return redirect("http://" + app.servletRequest().getServerName() + ":" + app.servletRequest().getServerPort() + "/cart/view/");
                
//        return redirect("/cart/view/");
    }

    @Request("remove")
    public Result remove(@Param("productId") Id productId) {
        Cart cart = cartHelper.getCart(true);
        Optional<CartItem> forDelete = cart.getCartItems().stream().filter(i -> i.getProductId().equals(productId))
            .findFirst();

        if (forDelete.isPresent()) {
            cart.getCartItems().remove(forDelete.get());
            cart.clearTotals();
            cartService.updateCart(cart);
        }

        return redirect("/cart/view/");
    }

    @Request("remove-bundle")
    public Result removeBundle(@Param("bundleId") Id bundleId) {
        Cart cart = cartHelper.getCart(true);
        List<CartItem> forDelete = cart.getCartItems().stream().filter(i -> i.getBundleId().equals(bundleId)).collect(Collectors.toList());

        if (forDelete != null && forDelete.size() > 0) {
            for (CartItem item : forDelete) {
                cart.getCartItems().remove(item);
            }
            cart.clearTotals();
            cartService.updateCart(cart);
        }

        return redirect("/cart/view/");
    }

    @Request("edit")
    public Result edit(@Param("productId") Id productId, @Param("variantId") Id variantId,
        @Param("quantity") Integer quantity) {
        Cart cart = cartHelper.getCart(true);
        Optional<CartItem> forEdit = cart.getCartItems().stream().filter(i -> i.getProductId().equals(productId))
            .findFirst();

        if (forEdit.isPresent()) {
            CartItem cartItem = forEdit.get();
            if (variantId == null) {
                if (quantity == null || quantity == 0) {
                    cart.getCartItems().remove(cartItem);
                } else {
                    if (quantity < 0) {
                        quantity = 1;
                    }
                    cartItem.setQuantity(quantity);
                }
            } else {
                cart.getCartItems().remove(cartItem);
                Product p = productService.getProduct(variantId);
                if (quantity < 0) {
                    quantity = 1;
                }
                for (int i = 1; i <= quantity; i++) {
                    cart.addProduct(p);
                }
            }
        }

        cart.clearTotals();
        cartService.updateCart(cart);

        return redirect("/cart/view/");

    }

    @Request("remove-coupon")
    public Result removeCoupon() {
        Cart cart = cartHelper.getCart(true);
        cart.setCouponCode(null);
        cart.setUseAutoCoupon(true);
        cartService.updateCart(cart);
        return redirect("/cart/view");
    }

    @Request("switch-auto-coupon")
    public Result switchAutoCoupon() {
        // if(useAutoCoupon != null){
        // Cart cart = getCart(true);
        // cart.setUseAutoCoupon(!useAutoCoupon);
        // }
        Cart cart = cartHelper.getCart(true);
        cart.setUseAutoCoupon(!cart.getUseAutoCoupon());
        cartService.updateCart(cart);
        return redirect("/cart/view");
    }

    @Request("set-auto-coupon")
    public Result setAutoCoupon(@Param("selectedAutoCoupon") String selectedAutoCoupon) {
        if (selectedAutoCoupon != null) {
            CouponCode couponCode = couponService.getCouponCode(selectedAutoCoupon);
            if (couponCode != null && couponCode.getCoupon() != null && couponCode.getCoupon().getAuto() != null
                && couponCode.getCoupon().getAuto()) {
                Cart cart = cartHelper.getCart(true);
                cart.setUseAutoCoupon(true);
                cart.setCouponCode(couponCode);
                cartService.updateCart(cart);
            }
        }

        return redirect("/cart/view");
    }

    @Request("add-coupon")
    public Result addCouponCode(@Param("couponCode") String couponCode) {
        CouponCode code = couponService.getCouponCode(couponCode);
        Map<String, String> result = new HashMap<>();
        // check that exists
        // if not say that coupon doesn't exists
        if (code == null) {
            result.put("result", "unsuccess");
            result.put("message", app.message("Coupon doesn't exist or not valid"));

        } else {
            Cart cart = cartHelper.getCart(true);
            CartAttributeCollection cartAttributeCollection = ((CouponData) cart).toCartAttributeCollection();

            if (couponService.isCouponApplicableToCart(code, cartAttributeCollection, true)) {
                cart.setCouponCode(code);
                cartService.updateCart(cart);
                result.put("result", "success");
            } else {
                result.put("result", "unsuccess");
                result.put("message", app.message("Can't use coupon for this cart"));
            }
        }

        return redirect("/cart/view");
    }

    @Request("mini-cart")
    public Result miniCart(@Param("cart-view") String view) throws Exception {
        return view("cart/" + view);
    }

    public String getCheckoutAction() {

        CheckoutFlowStep firstFlowStep = checkoutFlowHelper.getFirstActiveFlowStep();

        if (firstFlowStep != null) {
            System.out.println("CartController::getCheckoutAction() first active flow step = "
                + checkoutFlowHelper.getOriginalURI(firstFlowStep));
            return checkoutFlowHelper.getOriginalURI(firstFlowStep);
        } else {
            String checkoutFlowURL = app.cpStr_(Key.CHECKOUT_FLOW);

            if (checkoutFlowURL != null && !checkoutFlowURL.startsWith("http")) {
                checkoutFlowURL = new StringBuilder(app.getSecureBasePath()).append(checkoutFlowURL).toString();
            }

            return checkoutFlowURL;
        }
    }

    // public CouponCode getAutoCoupon() {
    // Cart cart = cartHelper.getCart();
    // if (autoCoupon == null) {
    // // check if cart contains autocoupon
    // if (cart.getCouponCode() != null &&
    // cart.getCouponCode().getCoupon().getAuto() == true)
    // autoCoupon = cart.getCouponCode();
    // }
    // if (autoCoupon == null) {
    // // get autocoupon
    // CartAttributeCollection cartAttributeCollection = ((CouponData)
    // cart).toCartAttributeCollection();
    // autoCoupon = couponService.getAutoCoupon(cart.getCouponCode(),
    // cartAttributeCollection);
    // }
    // return autoCoupon;
    // }
    //
    // public List<CouponCode> getAutoCoupons() {
    // if (autoCoupons == null) {
    // Cart cart = cartHelper.getCart();
    // CartAttributeCollection cartAttributeCollection = ((CouponData)
    // cart).toCartAttributeCollection();
    // autoCoupons = couponService.getAutoCoupons(cartAttributeCollection);
    // }
    //
    // return autoCoupons;
    // }
}
