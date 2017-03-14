 define(['jquery', 'bootstrap', 'gc/gc', 'jquery-slick'], function ($, Bootstrap, gc, slick) {
     return {
         // -----------------------------------------------------------------------------
         // Find variant product by selected options.
         // -----------------------------------------------------------------------------
         init: function (widgetPatams) {

             var $widget = $('body').find('#' + widgetPatams.widgetId);

             if(!$widget.hasClass('slick-initialized')){
                 $widget.slick({
                     slidesToShow: widgetPatams.slidesToShow || 1,
                     slidesToScroll: widgetPatams.slidesToShow || 1,
                     dots: true,
                     arrows: true,
                 });
             }

         }
     }
 });

