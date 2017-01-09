define([ 'gc/gc' ], function(gc) {

	return {
        formatPrice : function (price) {
            return price + " €";
        },
        formatPriceWithSign : function (price) {
            if(price > 0){
                return "+" + price + " €";
            } else {
                return price + " €";
            }

        }
	};
});