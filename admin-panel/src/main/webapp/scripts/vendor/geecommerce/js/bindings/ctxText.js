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
            } else if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'any') {
                ctxVal = gc.ctxobj.any(valueUnwrapped);
            } else {
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext.scopeId);
            }
            
            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).text(ctxVal);
            } else {
                if(!_.isEmpty(ctxOptions.default)) {
                    $(element).text(ctxOptions.default);
                } else {
                    $(element).text('');                    
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
            } else if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'closest') {
                ctxVal = gc.ctxobj.closest(valueUnwrapped);
            } else if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'any') {
                ctxVal = gc.ctxobj.any(valueUnwrapped);
            } else {
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext.scopeId);
            }
            
            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).text(ctxVal);
            } else {
                if(!_.isEmpty(ctxOptions.default)) {
                    $(element).text(ctxOptions.default);
                } else {
                    $(element).text('');                    
                }
            }
        }
    };
});
