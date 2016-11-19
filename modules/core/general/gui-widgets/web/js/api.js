define(['jquery', 'gc/gc', 'postal'], function ($, gc, postal) {

    return {
        getChannel: function (channel, topic) {

            // logging all the message data (log/debug purposes only)
            var tap = postal.addWireTap( function( d, e ) {
                console.log( JSON.stringify( e ) );
            });

            return postal.channel(channel, topic);
        },
        getProductChannel: function (topic) {
            return postal.channel('product', topic);
        }
    };

});