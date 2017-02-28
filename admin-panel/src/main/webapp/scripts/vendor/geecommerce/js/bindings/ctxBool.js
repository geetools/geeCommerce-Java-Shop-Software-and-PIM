define([ 'knockout', 'i18next', 'gc/gc' ], function(ko, i18n, gc) {

	/**
     * This binding converts a checkbox to a graphical slider switch. The resulting boolean values are set in a context-sensitive manner using the context-object. Current available scopes are global
     * and store. The slider works as follows: - The switch is initialized context-sensitively, depending on whether the store-buttons are active or not. - if the global-button is active, the switch
     * is initialized with an existing global value, if one exists, otherwise with a default value or false if no default is available. - If a store-button is active, then an attempt is made to
     * initialize the switch with an existing value belonging to the store. If no value is available, an attempt is made to find one in the following order: global, default or simply false. - When a
     * user is in global mode and clicks on the switch, all store-specific entries are removed and replaced with the new global value. - When a user is in store-mode, the new value is simply added to
     * the the context-object or replaced if one already exists for that store.
     */

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var activeContext = gc.app.sessionGet('activeContext');
			
			var $element = $(element), 
				value = valueAccessor(), 
				valueUnwrapped = ko.unwrap(value) || [],
				ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext),
				allBindings = allBindingsAccessor(), 
				switchOptions = allBindings.switchOptions || {};

			var defaultVal = switchOptions.default;
			var globalVal = gc.ctxobj.global(valueUnwrapped);

			// Attempt to set a default value for the current context (global or store) if none exists.
			if(_.isUndefined(ctxVal)) {
				// Store has been selected.
				if(!_.isEmpty(activeContext) && !_.isEmpty(activeContext.id)) {
					// If there is already a global value, we use that as default for now.
					if(!_.isUndefined(globalVal)) {
						ctxVal = globalVal;
					// Otherwise we use the configured default if one exists.
					} else if(!_.isUndefined(defaultVal)) {
						ctxVal = defaultVal;
					// If no value has been assigned at all yet, we just set false as the default.
					} else {
						ctxVal = false;
					}
				} else {
					// When setting the global value, we just fallback to default if it exists.
					if(!_.isUndefined(defaultVal)) {
						ctxVal = defaultVal;
					} else {
						ctxVal = false;
					}
				}
			}
			
			var lang = gc.app.currentLang();
			var yesText = i18n.t('app:common.yes', { lng : lang });
			var noText = i18n.t('app:common.no', { lng : lang });
			
			// Default settings can be overridden in binding by passing "switchOptions: {}" parameter.
			var defaultOptions = { width: 30, height: 17, button_width: 17, on_label: yesText, off_label: noText, show_labels: false };
			
			var options = _.extend({}, defaultOptions, switchOptions);
			options.checked = ctxVal;
			
			// Initialize the actual switch button (this converts the checkbox to the boolean slider).
			$element.switchButton(options);

			// Find the actual slider-button that the user will click on.
			$switchElement = $element.closest('.switch-wrapper').find('.switch-button-button').first();

			// Now we can listen to clicks, so that we only really change something if the user has actively triggered the event.
			$switchElement.on({
			    click: function () {
					gc.utils.triggerToolbar(element);
			    
					var activeContext = gc.app.sessionGet('activeContext');
					valueUnwrapped = ko.unwrap(value) || [];

					// If we are in global-mode, we remove all the store entries and simply replace them with just the new global value.
			    	if(_.isEmpty(activeContext) || _.isEmpty(activeContext.id)) {
// valueUnwrapped = [];
				    	gc.ctxobj.set(valueUnwrapped, undefined, $element.is(':checked'));
					// Otherwise just add/replace the store-specific value to the context-object.
			    	} else {
			    		gc.ctxobj.set(valueUnwrapped, undefined, $element.is(':checked'), activeContext);
			    	}
			    	
					// Inform the ko-observable of the change.
					if (ko.isObservable(value)) {
						value(valueUnwrapped);
					}
				}
			});

			$element.on({
			    change: function () {
					gc.utils.triggerToolbar(element);
					
					var activeContext = gc.app.sessionGet('activeContext');
					valueUnwrapped = ko.unwrap(value) || [];

	                var valueHasChanged = false;	                   
	                   
					// If we are in global-mode, we remove all the store entries and simply replace them with just the new global value.
			    	if(_.isEmpty(activeContext) || _.isEmpty(activeContext.id)) {
				    	// valueUnwrapped = [];
			    	    
                        var currentVal = gc.ctxobj.val(valueUnwrapped);
			    	    
                        if(currentVal !== $element.is(':checked')) {
                            gc.ctxobj.set(valueUnwrapped, undefined, $element.is(':checked'));
                            valueHasChanged = true;
                        }
                        
					// Otherwise just add/replace the store-specific value to the context-object.
			    	} else {
			    	    var currentVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext);
			    	    
                        if(currentVal !== $element.is(':checked')) {
                            gc.ctxobj.set(valueUnwrapped, undefined, $element.is(':checked'), activeContext);
                            valueHasChanged = true;
                        }
			    	}
			    	
					// Inform the ko-observable of the change.
					if (valueHasChanged === true && ko.isObservable(value)) {
						value(valueUnwrapped);
					}
			    }
			});

			if(options.show_labels === true) {
	            // In case the user makes a language change, we set the labels again here in a computed function.
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
		update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var activeContext = gc.app.sessionGet('activeContext');
			
			var $element = $(element),
				value = valueAccessor(),
				valueUnwrapped = ko.unwrap(value) || [],
				ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext),
				allBindings = allBindingsAccessor(), 
				switchOptions = allBindings.switchOptions || {};

			var defaultVal = switchOptions.default;
			var globalVal = gc.ctxobj.global(valueUnwrapped);

			
			// Attempt to set a default value for the current context (global or store) if none exists.
			if(_.isUndefined(ctxVal)) {
				// Store has been selected.
				if(!_.isEmpty(activeContext) && !_.isEmpty(activeContext.id)) {
					// If there is already a global value, we use that as default for now.
					if(!_.isUndefined(globalVal)) {
						ctxVal = globalVal;
					// Otherwise we use the configured default if one exists.
					} else if(!_.isUndefined(defaultVal)) {
						ctxVal = defaultVal;
					// If no value has been assigned at all yet, we just set false as the default.
					} else {
						ctxVal = false;
					}
				} else {
					// When setting the global value, we just fallback to default if it exists.
					if(!_.isUndefined(defaultVal)) {
						ctxVal = defaultVal;
					} else {
						ctxVal = false;
					}
				}
			}

            if($element.is(':checked') !== ctxVal) {
//                $element.switchButton('setChecked', ctxVal);
            }
		}
	};
});
