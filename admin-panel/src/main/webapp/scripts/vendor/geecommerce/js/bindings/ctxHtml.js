define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var activeContext = gc.app.sessionGet('activeContext');
            var ctxOptions = allBindings.ctxOptions || {};

            var ctxVal = null;

            if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'global') {
                ctxVal = gc.ctxobj.plain(valueUnwrapped);
            } else if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'closest') {
                ctxVal = gc.ctxobj.closest(valueUnwrapped);
            } else {
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext.scopeId);
            }
            
            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).html(ctxVal);
            } else {
                if(!_.isEmpty(ctxOptions.default)) {
                    $(element).html(ctxOptions.default);
                } else {
                    $(element).html('');                    
                }
            }
        },
        update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var activeContext = gc.app.sessionGet('activeContext');
            var ctxOptions = allBindings.ctxOptions || {};

            var ctxVal = null;

            if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'global') {
                ctxVal = gc.ctxobj.plain(valueUnwrapped);
            } else {
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext.scopeId);
            }
            
            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).html(ctxVal);
            } else {
                if(!_.isEmpty(ctxOptions.default)) {
                    $(element).html(ctxOptions.default);
                } else {
                    $(element).html('');                    
                }
            }
        }
    };
});
