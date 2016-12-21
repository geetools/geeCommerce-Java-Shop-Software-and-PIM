define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = _.isUndefined(value) ? [] : ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var lang = allBindings.lang || gc.app.currentLang();
	
            console.log('INIT allBindings.lang=', allBindings.lang, ' gc.app.currentLang()=', gc.app.currentLang());
            
            
			var ctxVal = gc.ctxobj.val(valueUnwrapped, lang);

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).val(ctxVal);
			} else {
				$(element).val('');
			}

			// Make sure that any edits are passed onto the ko-observable.
			$(element).change(function() {
				valueUnwrapped = ko.unwrap(value) || [];
				
	            var lang = allBindings.lang || gc.app.currentLang();
				
				console.log('USING LANG ________________ ', lang);
				
				// Update the context-object to reflect the change.
				gc.ctxobj.set(valueUnwrapped, lang, $(this).val());

				// Inform the ko-observable of the change.
				if (ko.isObservable(value)) {
					value(valueUnwrapped);
				}
			});
		},
		update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var lang = allBindings.lang || gc.app.currentLang();
			
            console.log('UPDATE allBindings.lang=', allBindings.lang, ' gc.app.currentLang()=', gc.app.currentLang());
            
			var ctxVal = gc.ctxobj.val(valueUnwrapped, lang);

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).val(ctxVal);
			} else {
				$(element).val('');
			}
		}
	};
});
