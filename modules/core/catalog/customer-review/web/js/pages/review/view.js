require(['jquery', 'bootstrap', 'gc/gc', 'customer/api', 'customer-review/api', 'customer-review/utils/common', 'jquery-rateit'], function ($, Bootstrap, gc, customerAPI, reviewAPI, reviewUtil) {

    $('#createReview').click(function () {
        var productId = $(this).attr("productId");
        customerAPI.isCustomerLoggedIn().then(function (result) {
            var loggedIn = result.data.results;
            if (loggedIn) {
                document.location.href = "/review/new/" + productId;
            } else {
                var text = $("#review-view-create").val();
                alert(text);
            }
        });
    });

    $("button[name='helpful']").click(function () {
        var reviewId = $(this).attr("reviewId");
        customerAPI.isCustomerLoggedIn().then(function (result) {
            var loggedIn = result.data.results;
            if (loggedIn) {
                reviewAPI.markAsHelpful(reviewId).then(function (result) {
                    $("span[reviewId='" + reviewId + "']").html($("#review-view-mark-thanks").val());
                });
            } else {
                var text = $("#review-view-mark").val();
                alert(text);
            }
        });
    });

    $("button[name='unhelpful']").click(function () {
        var reviewId = $(this).attr("reviewId");
        customerAPI.isCustomerLoggedIn().then(function (result) {
            var loggedIn = result.data.results;
            if (loggedIn) {
                reviewAPI.markAsUnhelpful(reviewId).then(function (result) {
                    $("span[reviewId='" + reviewId + "']").html($("#review-view-mark-thanks").val());
                });
            } else {
                var text = $("#review-view-mark").val();
                alert(text);
            }
        });
    });
});
