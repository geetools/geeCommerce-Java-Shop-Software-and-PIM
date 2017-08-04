define(['jquery', 'bootstrap', 'gc/gc', 'customer/api'], function ($, Bootstrap, gc, customerAPI) {

    $(".customer-account-container .registr-btn").on('click', function () {
        window.location.href = '/customer/account/new';
    });

    $(".customer-account-container .logout-btn").on('click', function () {
        window.location.href = '/customer/account/logout';
    });

    customerAPI.toggleShowPassword({
        field1: '#ac-password',
        control: '#ac-show-password'
    });

});