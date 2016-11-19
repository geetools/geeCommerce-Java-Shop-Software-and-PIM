require(['jquery', 'bootstrap', 'gc/gc', 'customer/api','customer-review/api', 'customer-review/utils/common', 'jquery-rateit'], function ($, Bootstrap, gc, customerAPI, reviewAPI, reviewUtil) {

    $("button[name='helpful']").click(function () {
        var reviewId = $(this).attr("reviewId");
        customerAPI.isCustomerLoggedIn().then(function (result) {
            var loggedIn = result.data.results;
            if (loggedIn) {
                reviewAPI.markAsHelpful(reviewId).then(function (result) {
                    $("span[reviewId='" + reviewId + "']").html('Thank you for your opinion');
                });
            } else {
                alert("You should be logged in to mark review");
            }
        });
    });

    $("button[name='unhelpful']").click(function () {
        var reviewId = $(this).attr("reviewId");
        customerAPI.isCustomerLoggedIn().then(function (result) {
            var loggedIn = result.data.results;
            if (loggedIn) {
                reviewAPI.markAsUnhelpful(reviewId).then(function (result) {
                    $("span[reviewId='" + reviewId + "']").html('Thank you for your opinion');
                });
            } else {
                alert("You should be logged in to mark review");
            }
        });
    });
});
