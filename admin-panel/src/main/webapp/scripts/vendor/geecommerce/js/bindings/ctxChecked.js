define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var value = valueAccessor();
			
			var valueUnwrapped = ko.unwrap(value);
			var allBindings = allBindingsAccessor();
			var activeContext = gc.app.sessionGet('activeContext');
			var ctxOptions = allBindings.ctxOptions || {};

			var ctxVal = null;

			if(!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'global') {
				ctxVal = gc.ctxobj.plain(valueUnwrapped);
			} else {
				ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext.id);
			}

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal) && ctxVal === true) {
				element.checked = true;
			} else {
				element.checked = false;
			}

			// Make sure that any edits are passed onto the ko-observable.
			$(element).change(function() {
				valueUnwrapped = ko.unwrap(value);
				activeContext = gc.app.sessionGet('activeContext');

				// Update the context-object to reflect the change.
				if(!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'global') {
					gc.ctxobj.set(valueUnwrapped, undefined, $(this).is(":checked"));
				} else {
					gc.ctxobj.set(valueUnwrapped, undefined, $(this).is(":checked"), activeContext);
				}

				// Inform the ko-observable of the change.
				if (ko.isObservable(value)) {
					value(valueUnwrapped);
				}
			});
		}
	};
});
