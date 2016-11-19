require(['jquery', 'bootstrap', 'gc/gc', 'checkout/api', 'checkout/utils/common'], function ($, Bootstrap, gc, checkoutAPI, checkoutUtil) {

	// JS-Controller for the cart.

    $(".delivery-address").hide();

    if($("input.delivery-box-switcher").is(':checked')){
        $(".delivery-address").show();
    }

    $("label.delivery-box-switcher").click(function () {
        if ($("input.delivery-box-switcher").is(':checked')) {
            $("input.delivery-box-switcher").prop('checked', false);
            $(".delivery-address").hide();
        } else {
            $("input.delivery-box-switcher").prop('checked', true);
            $(".delivery-address").show();
        }
    });

    $("input.delivery-box-switcher").change(function () {
        if ($(this).is(':checked')) {
            $(".delivery-address").show();
        } else {
            $(".delivery-address").hide();
        }
    });
    
    jQuery(document).ready(function($) {
        $('.capitalize').on("change", function(event) {
            var textBox = event.target;
            var start = textBox.selectionStart;
            var end = textBox.selectionEnd;
            textBox.value = textBox.value.charAt(0).toUpperCase() + textBox.value.slice(1);
            textBox.setSelectionRange(start, end);
        });
    });


});
