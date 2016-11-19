define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var _obj = valueAccessor();
			var _attr = {};
			var _val;
			
			if(!_.isUndefined(_obj)) {
				_attr = ko.gc.unwrap(_obj);
			}
			
			if(!_.isUndefined(_attr)) {
				_val = ko.gc.unwrap(_attr.value);
			}

			if(!_val)
				return;
			
			if(_attr.isOption === true) {
				if(_.isArray(_val)) {
					var text = '';
					var len = _val.length;
					if(len > 0) {
						for(var i=0; i<len; i++) {
							var attrOption = _.findWhere(_attr.options, {id: _val[i]});
							
							if(attrOption && attrOption.label) {
								var ctxVal = gc.ctxobj.val(attrOption.label, gc.app.currentUserLang(), 'closest');
								
								if(i > 0)
									text += ', ';
								
								text += ctxVal;
							}
						}
						
						$(element).html(text);
					}
				} else {
					var attrOption = _.findWhere(_attr.options, {id: _val});
					
					if(attrOption && attrOption.label) {
						var ctxVal = gc.ctxobj.val(attrOption.label, gc.app.currentUserLang(), 'closest');
						$(element).text(ctxVal);
					}
				}
				
			} else {
				var ctxVal = '';
				
				ko.computed(function() {
					var activeStore = gc.app.sessionGet('activeStore');
					var ctxVal = gc.ctxobj.val(_val, gc.app.currentUserLang(), 'closest', activeStore.id);

					if (ctxVal) {
						$(element).text(ctxVal);
					} else {
						$(element).text('');
					}
				});
			}
		}
	};
});
