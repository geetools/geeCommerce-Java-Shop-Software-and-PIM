define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			var activeStore = gc.app.sessionGet('activeStore');

			var code = allBindings.get('code');
			var scope = allBindings.get('scope');
			var mode = allBindings.get('mode');
			
			var attribute = gc.attributes.find(srcObjUnwrapped.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				var val = [];
				
				if(!_.isUndefined(scope) && scope == 'global') {
					$(element).text(gc.ctxobj.plain(attribute.value));
				} else if(!_.isUndefined(scope) && scope == 'store') {
					$(element).text(gc.ctxobj.val(attribute.value, undefined, mode, activeStore.id));
				} else {
					$(element).text(gc.ctxobj.val(attribute.value, gc.app.currentUserLang(), mode));
				}
			} else {
				$(element).text('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var srcObj = valueAccessor();
			var srcObjUnwrapped = ko.unwrap(srcObj);
			
			var code = allBindings.get('code');
			var scope = allBindings.get('scope');
			var mode = allBindings.get('mode');
			var attribute = gc.attributes.find(srcObj.attributes, code);

			if (!_.isUndefined(attribute) && !_.isNull(attribute)) {
				if(!_.isUndefined(scope) && scope == 'global') {
					$(element).text(gc.ctxobj.val(attribute.value, undefined, mode));
				} else {
					$(element).text(gc.ctxobj.val(attribute.value, gc.app.currentUserLang(), mode));
				}
			} else {
				$(element).text('');
			}
		}
	};
});
