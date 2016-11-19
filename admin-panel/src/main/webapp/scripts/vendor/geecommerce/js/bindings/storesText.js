define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = ko.unwrap(value) || [];

			if(!_.isArray(valueUnwrapped)) {
				valueUnwrapped = [valueUnwrapped];
			}

			var availableStores = gc.app.confGet('availableStores');
			var storesText = '';

			var x = 0;
			_.each(valueUnwrapped, function(storeId) {
				if(x > 0)
				  storesText += ', ';
				
				var store = _.findWhere(availableStores, {id: storeId});
				storesText += store.name;
				
				x++;
			});
			
			if (!_.isUndefined(storesText) && !_.isNull(storesText)) {
				$(element).text(storesText);
			} else {
				$(element).text('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
		}
	};
});
