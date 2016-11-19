require(['jquery', 'bootstrap', 'gc/gc', 'customer-review/api', 'customer-review/utils/common', 'jquery-rateit'], function ($, Bootstrap, gc, reviewAPI, reviewUtil) {

    var star1 = $("#review-rating-star-1").val();
    var star2 = $("#review-rating-star-2").val();
    var star3 = $("#review-rating-star-3").val();
    var star4 = $("#review-rating-star-4").val();
    var star5 = $("#review-rating-star-5").val();

    var tooltipvalues = [ star1, star2, star3, star4, star5 ];

/*    
    <#if actionBean.rating??>
        var rating = ${actionBean.rating};
    <#else>
*/
    reviewUtil.initSummary();

    var rating = 0;

    var savedStatus = "";
    if(rating > 0){
        savedStatus = tooltipvalues[rating-1];
        $('.rateit-label').text(savedStatus);
    }

    $(".rateit").bind('over', function (event, value) {
        $('.rateit-label').text(tooltipvalues[value-1]);
    });

    $(".rateit").bind('rated', function (event, value) {
        $('input[name="rating"]').val(value);
        savedStatus = tooltipvalues[value-1];
    });

    $('.rateit').hover(function(){
    }, function(){
        $('.rateit-label').text(savedStatus);
    });


});
