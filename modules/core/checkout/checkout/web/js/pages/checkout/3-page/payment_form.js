require(['jquery', 'bootstrap', 'gc/gc', 'checkout/api', 'checkout/utils/common'], function ($, Bootstrap, gc, checkoutAPI, checkoutUtil) {

    $("input[name='form.paymentMethodCode']").change(function () {
        var code = $(this).attr("value");
    });

    $(".btn-next").click(function(){
        if ($("input[name='form.paymentMethodCode']:checked").length) {
            $("form.checkout-form").submit();
        } else {
            var warn = $('input[name=paymentSelectedWarn]').val();
            alert(warn);
        }

        return false;
    });

});
