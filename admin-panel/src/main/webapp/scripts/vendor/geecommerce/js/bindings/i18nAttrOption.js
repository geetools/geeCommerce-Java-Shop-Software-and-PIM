define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _optionIds;
			var allBindings = allBindings() || {};
			
			if(!_.isUndefined(_obj)) {
				_optionIds = ko.gc.unwrap(_obj);
			}
			
			if(!_optionIds)
				return;

			var _attributes = gc.cache.get('attributes');
			var _attr = gc.attributes.find(_attributes, allBindings.code);

			if(_.isArray(_optionIds)) {
				var text = '';
				var len = _optionIds.length;
				if(len > 0) {
					for(var i=0; i<len; i++) {
						var attrOption = _.findWhere(_attr.options, {id: _optionIds[i]});
						
						if(attrOption && attrOption.label) {
							var ctxVal = gc.ctxobj.val(attrOption.label, gc.app.currentUserLang(), 'closest');
							text += ctxVal + '<br/>';
						}
					}
					
					$(element).html(text);
				}
			} else {
				var attrOption = _.findWhere(_attr.options, {id: _optionIds});
				
				if(attrOption && attrOption.label) {
					var ctxVal = gc.ctxobj.val(attrOption.label, gc.app.currentUserLang(), 'closest');
					$(element).text(ctxVal);
				}
			}
		}
	};
});
