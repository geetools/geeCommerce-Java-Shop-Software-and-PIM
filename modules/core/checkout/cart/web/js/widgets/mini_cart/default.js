define(['jquery', 'bootstrap', 'gc/gc', 'cart/api', 'cart/utils/common', 'price/utils/common'], function ($, Bootstrap, gc, cartAPI, cartUtil, priceUtil) {
    return {
        init: function (widgetParams) {
            cartAPI.getCart(1).then(function (data) {
                console.log(data);
                var cart = {};
                cart.items = [];

                cart.empty = true;

                _.each(data.cartItems, function (cartItem) {
                    var item = {};
                    item.id = cartItem.productId;
                    item.url = cartItem.productURI;
                    item.quantity = cartItem.quantity;
                    item.image = cartItem.product.mainImage != null ? cartItem.product.mainImage.webThumbnailPath : "";
                    item.name = cartUtil.attributeValue(cartItem.product.attributes, "name");
                    item.name2 = cartUtil.attributeValue(cartItem.product.attributes, "name2");
                    item.price = priceUtil.formatPrice(cartItem.productPrice);
                    item.subtotal = priceUtil.formatPrice(data.totals.itemResults[item.id].gross_subtotal);
                    cart.items.push(item);
                    cart.empty = false;
                });

                cart.total = priceUtil.formatPrice(data.totals.gross_grand_total);

                gc.app.render({
                    slice: 'cart/mini_cart/default',
                    data: {cart: cart},
                    process: true,
                    target: '#mini-cart-content'
                }, function (targetEL) {

                });
            });

            $('#popoverMiniCart').popover({
                html: true,
                content: function () {
                    var content = $(this).attr("data-popover-content");
                    return $(content).children("#mini-cart-content").html();
                }
            });
        }
    }

});