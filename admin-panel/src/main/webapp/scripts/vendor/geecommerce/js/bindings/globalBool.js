define([ 'knockout', 'i18next', 'gc/gc' ], function(ko, i18n, gc) {

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var $element = $(element), 
				value = valueAccessor(), 
				allBindings = allBindingsAccessor(), 
				switchOptions = allBindings.switchOptions || {};

			var lang = gc.app.currentLang();
			var yesText = i18n.t('app:common.yes', { lng : lang });
			var noText = i18n.t('app:common.no', { lng : lang });
				
			// Default settings can be overriden in binding by passing "switchOptions: {}" parameter.
			var defaultOptions = { width: 30, height: 17, button_width: 17, on_label: yesText, off_label: noText, show_labels: false };
			
			var options = _.extend({}, defaultOptions, switchOptions);
				
			var unwrappedValue = ko.unwrap(value) || [];
			var booleanVal = gc.ctxobj.plain(unwrappedValue);
			
			if(booleanVal) {
				options.checked = true;
			} else {
				options.checked = false;
			}

			// Change observable value if checkbox has been clicked.
			if(ko.isObservable(value)) {
				$element.change(function() {
					gc.utils.triggerToolbar(element);

					gc.ctxobj.set(unwrappedValue, undefined, $element.is(':checked'));
					value(unwrappedValue);
			    });
			}
			
			$element.switchButton(options);
			
            if(options.show_labels === true) {
                // In case the use makes a language change, we set the text again here in a computed function.
                ko.computed(function() {
                    var lang = gc.app.currentLang();
                    
                    var noText = i18n.t('app:common.no', { lng : lang });
                    var yesText = i18n.t('app:common.yes', { lng : lang });

                    var labels = $element.parent('.switch-wrapper').children('.switch-button-label');
                    
                    $(labels[0]).text(noText);
                    $(labels[1]).text(yesText);
                });
            }
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var value = valueAccessor();
			var valueUnwrapped = ko.unwrap(value) || [];
			var booleanVal = gc.ctxobj.plain(valueUnwrapped);

			if (booleanVal) {
				$(element).prop('checked', true);
				$(element).attr('checked', 'checked');
				$(element).switchButton('setChecked', true);

			} else {
				$(element).prop('checked', false);
				$(element).removeAttr('checked');
				$(element).switchButton('setChecked', false);
			}
		}
	};
});
