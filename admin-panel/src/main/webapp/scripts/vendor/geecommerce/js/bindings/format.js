define([ 'knockout', 'gc/gc', 'moment' ], function(ko, gc, moment) {

	return {
        init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            var value = ko.unwrap(valueAccessor());
            var options = allBindings().formatOptions || {};
			var type = options.type || 'text'; // Could support text and currency in the future. Now we just need date.
			var fromFormat = options.from || 'YYYY-MM-DDTHH:mm:ss.SSSZZ';
			var toFormat = options.to || 'DD.MM.YYYY';
			var locale = options.locale;

			var formatted = value;

			if( type == 'date' ) {
				if(value instanceof Date) {
					formatted = moment(value).format(toFormat);
				} else {
					formatted = moment(value, fromFormat).format(toFormat);
				}
			}

            $(element).text(formatted);
        }
	};
});
