
 define(['jquery', 'bootstrap', 'gc/gc', 'jquery-slick'], function ($, Bootstrap, gc, slick) {

     return {
         // -----------------------------------------------------------------------------
         // Find variant product by selected options.
         // -----------------------------------------------------------------------------
         init: function (widgetPatams) {


             $('#' + widgetPatams.widgetId + "-for").not('.slick-initialized').slick({
                 slidesToShow: 1,
                 slidesToScroll: 1,
                 dots: true,
                 arrows: true,
                 asNavFor: '#' + widgetPatams.widgetId + "-nav"
             });
             $('#' + widgetPatams.widgetId + "-nav").not('.slick-initialized').slick({
                 slidesToShow: 5,
                 slidesToScroll: 1,
                 asNavFor: '#' + widgetPatams.widgetId + "-for",
                 centerMode: true,
                 focusOnSelect: true
             });
         }
     }
 });
