define(['jquery', 'bootstrap', 'gc/gc'], function ($, Bootstrap, gc) {

    $(".customer-account-container .forgot-password-reset-success-btn").on('click', function() {
        window.location.href='/customer/account/login';
    });

});