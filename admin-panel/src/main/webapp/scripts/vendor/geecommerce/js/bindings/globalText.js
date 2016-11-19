define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _objUnwrapped = ko.unwrap(_obj);
			var _value = null;
			
			if(_.isObject(_objUnwrapped) && !_.isArray(_objUnwrapped)) {
				_value = _objUnwrapped.value;
			} else {
				_value = _objUnwrapped;
			}
			
			var ctxVal = gc.ctxobj.global(_value);

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).text(ctxVal);
			} else {
				$(element).text('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _objUnwrapped = ko.unwrap(_obj);

			var _value = null;
			
			if(_.isObject(_objUnwrapped) && !_.isArray(_objUnwrapped)) {
				_value = _objUnwrapped.value;
			} else {
				_value = _objUnwrapped;
			}
			
			var ctxVal = gc.ctxobj.global(_value);

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).text(ctxVal);
			} else {
				$(element).text('');
			}
		}
	};
});
