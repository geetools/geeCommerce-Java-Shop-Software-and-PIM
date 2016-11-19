define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			
			var code = allBindings.get('code');
			var attribute = gc.attributes.find(srcObj.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				$(element).html(gc.ctxobj.val(attribute.value, gc.app.currentUserLang()));
			} else {
				$(element).html('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			
			var code = allBindings.get('code');
			var attribute = gc.attributes.find(srcObj.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				$(element).html(gc.ctxobj.val(attribute.value, gc.app.currentUserLang()));
			} else {
				$(element).html('');
			}
		}
	};
});
