package com.geecommerce.cart.helper;

import com.geecommerce.cart.CartConstant;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;
import com.google.inject.Inject;

@Helper
public class DefaultCartHelper implements CartHelper {
    @Inject
    protected App app;

    protected final CartService cartService;

    @Inject
    public DefaultCartHelper(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public Cart getCart() {
        return getCart(true);
    }

    @Override
    public Cart getCart(boolean createIfNotExists) {
        // Attempt to get cart from session.
        Cart cart = app.sessionGet(CartConstant.SESSION_KEY_CART);

        if (cart == null) {
            // If no cart exists in session yet, see if a cartId has previously
            // been stored in a cookie.
            String cartId = app.cookieGet(CartConstant.COOKIE_KEY_CART_ID);

            if (cartId != null) {
                // Get the previously created cart from the database
                cart = cartService.getCart(Id.parseId(cartId));
            }

            // If we still have no cart, create a new one and store its id in a
            // cookie.
            if (cart == null && createIfNotExists) {
                cart = createNewCart();
                app.cookieSet(CartConstant.COOKIE_KEY_CART_ID, cart.getId(), (60 * 60 * 24 * 30));
            }

            if (cart != null) {
                // Add the cart to the session
                app.sessionSet(CartConstant.SESSION_KEY_CART, cart);
            }
        }

        if (app.isCustomerLoggedIn()) {
            Customer customer = (Customer) app.getLoggedInCustomer();

            if (cart == null) {
                // we should try find cart for customer
                Cart customerCart = cartService.getCartForCustomer(customer.getId());

                if (customerCart != null) {
                    cart = customerCart;
                    setCart(cart);
                }

                // else if cart is null than we have createIfNotExists = false
            } else if (cart.getCustomerId() == null) {
                // we have a cart be it doesn't belong to customer
                unsetCart();
                Cart customerCart = cartService.getCartForCustomer(customer.getId());

                if (cart.getCartItems() != null && cart.getCartItems().size() != 0) {
                    // need to copy
                    if (customerCart == null) {
                        customerCart = createNewCart();
                    }

                    for (CartItem item : cart.getCartItems()) {
                        customerCart.addProduct(item.getProduct());
                    }
                    cartService.updateCart(customerCart);
                } else {
                    // nothing to copy
                    if (customerCart == null & createIfNotExists) {
                        customerCart = createNewCart();
                    }

                }
                cart = customerCart;
                setCart(cart);

            } else if (!cart.getCustomerId().equals(customer.getId())) {
                // we have cart from other customer
                unsetCart();
                Cart customerCart = cartService.getCartForCustomer(customer.getId());
                if (customerCart == null & createIfNotExists) {
                    customerCart = createNewCart();
                }
                cart = customerCart;
                setCart(cart);
            }
        } else {
            // customer unknown, we should check if he see other cart
            if (cart != null && cart.getCustomerId() != null) {
                // need to remove cart from cookies and session
                unsetCart();
                cart = null;
                if (createIfNotExists) {
                    cart = createNewCart();
                    setCart(cart);
                }
            }
        }

        return cart;
    }

    protected void unsetCart() {
        app.cookieUnset(CartConstant.COOKIE_KEY_CART_ID);
        app.sessionRemove(CartConstant.SESSION_KEY_CART);
    }

    public void setCart(Cart cart) {
        if (cart != null) {
            app.cookieSet(CartConstant.COOKIE_KEY_CART_ID, cart.getId(), (60 * 60 * 24 * 30));
            app.sessionSet(CartConstant.SESSION_KEY_CART, cart);
        }
    }

    protected Cart createNewCart() {
        Cart cart = app.getModel(Cart.class);

        if (app.isCustomerLoggedIn()) {
            cart.belongsTo((Customer) app.getLoggedInCustomer());
        }
        cart.setEnabled(true);
        return cartService.createCart(cart);
    }
}
