require(['jquery', 'bootstrap', 'gc/gc', 'customer-review/api', 'customer-review/utils/common', 'jquery-rateit'], function ($, Bootstrap, gc, reviewAPI, reviewUtil) {

    $("a[name='deleteReview']").click(function(){
        if(confirm("Are you sure you want to delete review?")){
            document.location.href=$(this).attr("href");
        }
        return false;
    })
});
