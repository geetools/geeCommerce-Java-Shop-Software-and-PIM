require(['jquery', 'bootstrap', 'gc/gc', 'cart/api', 'cart/utils/common'], function ($, Bootstrap, gc, cartAPI, cartUtil) {

    $(".recalc-btn").change(function () {
        var productId = $(this).attr("productId");
        var quantity = $(this).val();
        var url = "/cart/edit?productId=" + productId + "&quantity=" + quantity;

        window.location.href = url;
    });
	
});
