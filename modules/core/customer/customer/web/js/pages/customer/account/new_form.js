define(['jquery', 'bootstrap', 'gc/gc', 'customer/api'], function ($, Bootstrap, gc, customerAPI) {

    customerAPI.toggleShowPassword({
        field1: '#ac-input-password-1',
        field2: '#ac-input-password-2',
        control: '#ac-show-password'
    });

});