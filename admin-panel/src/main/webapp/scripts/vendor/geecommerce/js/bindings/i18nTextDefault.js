define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _objUnwrapped = [];
			
			if(!_.isUndefined(_obj)) {
				_objUnwrapped = ko.unwrap(_obj);
			}

			var _value = null;
			var _mode = 'strict';
			
			if(_.isObject(_objUnwrapped) && !_.isArray(_objUnwrapped)) {
				_value = ko.unwrap(_objUnwrapped.value);
				
				if(!_.isUndefined(_objUnwrapped.mode)) {
					_mode = _objUnwrapped.mode;
				}
			} else {
				_value = _objUnwrapped;
			}
			
//			_value = ko.mapping.toJS(_value);
			
			var ctxVal = gc.ctxobj.val(_value, gc.app.defaultLanguage(), _mode);

			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).text(ctxVal);
			} else {
				$(element).text('');
			}
		},
		update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _objUnwrapped = [];
			
			if(!_.isUndefined(_obj)) {
				_objUnwrapped = ko.unwrap(_obj);
			}

			var _value = null;
			var _mode = 'strict';
			
			if(_.isObject(_objUnwrapped) && !_.isArray(_objUnwrapped)) {
				_value = ko.unwrap(_objUnwrapped.value);
				if(!_.isUndefined(_objUnwrapped.mode)) {
					_mode = _objUnwrapped.mode;
				}
			} else {
				_value = _objUnwrapped;
			}
			
//			_value = ko.mapping.toJS(_value);
			
			var ctxVal = gc.ctxobj.val(_value, gc.app.defaultLanguage(), _mode);
			
			if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
				$(element).text(ctxVal);
			} else {
				$(element).text('');
			}
		}
	};
});
