define([ 'knockout', 'i18next', 'gc/gc' ], function(ko, i18n, gc) {

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var $element = $(element), 
				value = valueAccessor(), 
				valueUnwrapped = _.isUndefined(value) ? [] : ko.unwrap(value),
				ctxVal = gc.ctxobj.val(valueUnwrapped, gc.app.currentLang()),
				allBindings = allBindingsAccessor(), 
				switchOptions = allBindings.switchOptions || {};

			var lang = gc.app.currentLang();
			var yesText = i18n.t('app:common.yes', { lng : lang });
			var noText = i18n.t('app:common.no', { lng : lang });
			
			// Default settings can be overridden in binding by passing "switchOptions: {}" parameter.
			var defaultOptions = { width: 30, height: 17, button_width: 17, on_label: yesText, off_label: noText, show_labels: false };
			
			var options = _.extend({}, defaultOptions, switchOptions);
			
			options.clear = false;
			
			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal) && ctxVal) {
				options.checked = true;
			} else {
				options.checked = false;
			}

			// Make sure that any edits are passed onto the ko-observable.
			$element.change(function() {
			
				gc.utils.triggerToolbar(element);
			
				valueUnwrapped = _.isUndefined(value) ? [] : ko.unwrap(value);

				gc.ctxobj.set(valueUnwrapped, gc.app.currentLang(), $element.is(':checked'));

				// Inform the ko-observable of the change.
				if (ko.isObservable(value)) {
					value(valueUnwrapped);
				}
			});

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
			var valueUnwrapped = _.isUndefined(value) ? [] : ko.unwrap(value)
			var ctxVal = gc.ctxobj.val(valueUnwrapped, gc.app.currentLang());

            $(element).click();

            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal) && ctxVal) {
				$(element).prop('checked', true);
				$(element).attr('checked', 'checked');
				$(element).change();
				$(element).switchButton('setChecked', true);

			} else {
				$(element).prop('checked', false);
				$(element).removeAttr('checked');
				$(element).change();
				$(element).switchButton('setChecked', false);
			}
		}
	};
});
