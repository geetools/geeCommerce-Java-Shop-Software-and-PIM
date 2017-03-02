define(['jquery', 'bootstrap', 'gc/gc'], function ($, Bootstrap, gc) {
    return {
        init: function (widgetParams) {
            if(false) {
                var endTime = Date.parse("${promoEndTimeForCounter}");
                if (!endTime)
                    endTime = Date.fromISO("${promoEndTimeForCounter}");

                setInterval(function () {
                    var now = new Date();
                    var delta = Math.abs(endTime - now) / 1000;
                    //  console.log(delta)
                    // calculate (and subtract) whole days
                    var days = Math.floor(delta / 86400);
                    delta -= days * 86400;

                    // calculate (and subtract) whole hours
                    var hours = Math.floor(delta / 3600) /*% 24*/;
                    delta -= hours * 3600;

                    // calculate (and subtract) whole minutes
                    var minutes = Math.floor(delta / 60) /*% 60*/;
                    delta -= minutes * 60;

                    // what's left is seconds
                    var seconds = Math.floor(delta % 60);

                    var dayText = " days ";
                    if (days == 1) {
                        dayText = " day "
                    } else if (days >= 2 && days <= 4) {
                        dayText = " days "
                    }

                    if (minutes < 10)
                        minutes = "0" + minutes;

                    if (seconds < 10)
                        seconds = "0" + seconds;

                    $(".time-left").html(days + dayText + hours + ":" + minutes + ":" + seconds);
                }, 1000);
            }
        }
    }

});