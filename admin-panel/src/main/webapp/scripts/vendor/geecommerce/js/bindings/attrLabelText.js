define([ 'knockout', 'gc/gc', 'gc-attribute' ], function(ko, gc, attrAPI) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var code = valueAccessor();
			var forType = allBindings.get('forType');
            var mode = allBindings.get('mode') || 'closest';
            var attribute;
            
			attrAPI.getAttributes(forType, {filter: {code: code}}).then(function(data) {
			    if(data && !_.isEmpty(data.data.attributes) && data.data.attributes.length === 1) {
	                attribute = data.data.attributes[0];
			    }
			});
			
			if (!_.isUndefined(attribute)) {
				$(element).text(gc.ctxobj.val(attribute.backendLabel, gc.app.currentUserLang(), mode));
			} else {
				$(element).text('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
		}
	};
});
