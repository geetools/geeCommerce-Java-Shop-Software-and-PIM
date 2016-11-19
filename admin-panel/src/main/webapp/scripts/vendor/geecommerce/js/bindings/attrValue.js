define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			
			var code = allBindings.get('code');
			var attribute = gc.attributes.find(srcObjUnwrapped.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				$(element).val(gc.ctxobj.val(attribute.value, gc.app.currentLang()));
			} else {
				$(element).val('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			
			var code = allBindings.get('code');
			var attribute = gc.attributes.find(srcObjUnwrapped.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				$(element).val(gc.ctxobj.val(attribute.value, gc.app.currentLang()));
			} else {
				$(element).val('');
			}
		}
	};
});
