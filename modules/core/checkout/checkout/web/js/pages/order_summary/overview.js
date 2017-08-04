define(['jquery', 'bootstrap', 'gc/gc', 'checkout/api', 'checkout/utils/common'], function ($, Bootstrap, gc, checkoutAPI, checkoutUtil) {

    $('#order-filter-date').on('change', function() {
        var redirectURL = "?orderFilterDate=" + $('#order-filter-date').val();
        var url = document.location.toString();
        if (url.match('#')) {
            redirectURL += '#' + url.split('#')[1];
        }

        window.location.href = redirectURL;
    });

    // to open appropriate tab panel with uri link fragment
    var url = document.location.toString();
    if (url.match('#')) {
        $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
    }

    // Change hash for page-reload
    $('.nav-tabs a').on('shown.bs.tab', function (e) {
        window.location.hash = e.target.hash;
    });

});
