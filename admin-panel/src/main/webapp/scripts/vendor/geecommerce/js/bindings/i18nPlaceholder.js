define([ 'knockout', 'i18next', 'gc/gc' ], function(ko, i18n, gc) {

	return {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var $element = $(element), 
				value = valueAccessor(),
				allBindings = allBindingsAccessor(), 
				options = allBindings.args || {};
			
			var key = ko.unwrap(value);
			var lang = gc.app.currentUserLang();
        	var i18nOptions = _.extend({}, { lng : lang }, options);
			
			var text = i18n.t(key, i18nOptions);
			
			if (!_.isUndefined(text) && !_.isNull(text)) {
				$(element).attr('placeholder', text);
			} else {
				$(element).attr('placeholder', '');
			}
		},
		update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			var $element = $(element), 
			value = valueAccessor(),
			allBindings = allBindingsAccessor(), 
			options = allBindings.args || {};
		
			var key = ko.unwrap(value);
			var lang = gc.app.currentUserLang();
	    	var i18nOptions = _.extend({}, { lng : lang }, options);
			
			var text = i18n.t(key, i18nOptions);
			
			if (!_.isUndefined(text) && !_.isNull(text)) {
				$(element).attr('placeholder', text);
			} else {
				$(element).attr('placeholder', '');
			}
		}
	}
});
