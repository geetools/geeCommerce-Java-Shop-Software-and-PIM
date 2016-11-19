define([ 'knockout' ], function(ko) {

	return {
		update: function (element, valueAccessor) {
	        if (valueAccessor()) {
	            $(element).attr("readonly", "readonly");
	            $(element).addClass("readonly");
	        } else {
	            $(element).removeAttr("readonly");
	            $(element).removeClass("readonly");
	        }
	    }
    };
});
