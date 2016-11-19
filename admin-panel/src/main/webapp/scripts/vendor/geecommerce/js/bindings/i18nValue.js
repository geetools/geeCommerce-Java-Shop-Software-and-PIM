define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = _.isUndefined(value) ? [] : ko.unwrap(value);
			
			var ctxVal = gc.ctxobj.val(valueUnwrapped, gc.app.currentLang());

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).val(ctxVal);
			} else {
				$(element).val('');
			}

			// Make sure that any edits are passed onto the ko-observable.
			$(element).change(function() {
				valueUnwrapped = ko.unwrap(value) || [];
				
				// Update the context-object to reflect the change.
				gc.ctxobj.set(valueUnwrapped, gc.app.currentLang(), $(this).val());

				// Inform the ko-observable of the change.
				if (ko.isObservable(value)) {
					value(valueUnwrapped);
				}
			});
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = ko.unwrap(value);
			
			var ctxVal = gc.ctxobj.val(valueUnwrapped, gc.app.currentLang());

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).val(ctxVal);
			} else {
				$(element).val('');
			}
		}
	};
});
