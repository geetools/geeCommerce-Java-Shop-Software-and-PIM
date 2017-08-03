define(['jquery', 'bootstrap', 'gc/gc', 'customer/api'], function ($, Bootstrap, gc, customerAPI) {

    $('input[type="password"]').val('');

    $("button[name=ac-password-change-btn]").click(function () {
        $('input[type="password"]').val('');
        if ($(".ac-change-password-group").is(":hidden"))
            $(".ac-change-password-group").removeClass('hidden');
        else
            $(".ac-change-password-group").addClass('hidden');

    });

    customerAPI.toggleShowPassword({
        field1: '#ac-input-password-1',
        field2: '#ac-input-password-2',
        control: '#ac-show-password'
    });

});