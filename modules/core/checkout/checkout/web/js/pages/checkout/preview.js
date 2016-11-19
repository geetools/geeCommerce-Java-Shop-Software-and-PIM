require(['jquery', 'bootstrap', 'gc/gc', 'checkout/api', 'checkout/utils/common'], function ($, Bootstrap, gc, checkoutAPI, checkoutUtil) {

    var mobile = {
        Android: function () {
            return navigator.userAgent.match(/Android/i);
        },
        BlackBerry: function () {
            return navigator.userAgent.match(/BlackBerry/i);
        },
        iOS: function () {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i);
        },
        Opera: function () {
            return navigator.userAgent.match(/Opera Mini/i);
        },
        Windows: function () {
            return navigator.userAgent.match(/IEMobile/i);
        },
        any: function () {
            return (mobile.Android() || mobile.BlackBerry() || mobile.iOS() || mobile.Opera() || mobile.Windows());
        }
    };

    var isMobile = false;
    if (mobile.any()) {
        isMobile = true;
    } else {

    }

    $('#popoverOption').popover({
        content: $('#popoverContent').html(),
        html: true,
        trigger: isMobile ? "click" : "hover"
    });
    //

    $('#popoverOption2').popover({
        content: $('#popoverContent').html(),
        html: true,
        trigger: isMobile ? "click" : "hover"
    });

    $(".checkout-action-btn").click(function() {
        console.log("agreeToTerms() checked...");

        if ($("input[name='form.agreeToTerms']:checked").length) {
            $(".checkout-form").submit();
        } else {
            var warn = $('input[name=agreeToTermsWarn]').val();
            alert(warn);
            return false;
        }
    });

});
