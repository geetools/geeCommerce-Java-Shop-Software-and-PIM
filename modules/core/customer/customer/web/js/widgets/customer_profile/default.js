define(['jquery', 'bootstrap', 'gc/gc', 'customer/api'], function ($, Bootstrap, gc, customerAPI) {
    return {
        init: function (widgetParams) {
            $('a.customer-profile-link#popoverCustomerProfile').popover({
                html: true,
                placement: "bottom",
                content: $('#popoverCustomerProfile_content').html(),
                trigger: "manual",
                title: $('#popoverCustomerProfileHeader_content').html()

            }).on('shown.bs.popover', function (e) {

                var popover = $(this);
                $(this).parent().find('div.popover .close').on('click', function (e) {
                    popover.popover('hide');
                });

                var loggedIn = false;
                customerAPI.isCustomerLoggedIn().then(function (result) {
                    loggedIn = result.data.results;
                    gc.app.render({
                            slice: 'customer/customer_profile/header_layer', data: {loggedIn: loggedIn}, process: true,
                            target: "#customer-profile-content"
                        },
                        function (data) {
                            $(".cp-action-btn .cp-login-btn").on('click', function () {
                                window.location.href = '/customer/account/login';
                            });

                            $(".cp-action-btn .cp-logout-btn").on('click', function () {
                                window.location.href = '/customer/account/logout';
                            });
                        });
                });

                customerAPI.getLoggedInCustomer().then(function (result) {
                    if(result) {
                        gc.app.render({
                            slice: 'customer/customer_profile/title_layer',
                            data: {customer: result},
                            process: true,
                            target: '#customer-profile-title'
                        }, function () {
                            var lable = result.salutation + ".&nbsp;" + result.forename + "&nbsp;" + result.surname;
                            if (lable.length > 25) {
                                $('#cp-title').tooltip({
                                    trigger: "hover",
                                    placement: "bottom",
                                    html: true,
                                    title: "<div>" + lable + "</div>"
                                });
                            }
                        });
                    } else {
                        gc.app.render({
                            slice: 'customer/customer_profile/title_layer',
                            data: {customer: null},
                            process: true,
                            target: '#customer-profile-title'
                        });
                    }
                }, function (fail) {
                    gc.app.render({
                        slice: 'customer/customer_profile/title_layer',
                        data: {customer: null},
                        process: true,
                        target: '#customer-profile-title'
                    });
                });

            }).click(function () {
                $(this).popover("show");
            });
        }
    }
});