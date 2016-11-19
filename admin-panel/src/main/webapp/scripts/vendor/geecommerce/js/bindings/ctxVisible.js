define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var activeContext = gc.app.sessionGet('activeContext');
            var ctxOptions = allBindings.ctxOptions || {};
            var keepSelf = ctxOptions.keepSelf || false;

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
            
            if ((!_.isUndefined(ctxVal) && !_.isString(ctxVal)) || (!_.isEmpty(ctxVal) && _.isString(ctxVal))) {
                if(keepSelf === true) {
                    $(element).children().show();
                } else {
                    $(element).show();
                }
            } else {
                if(keepSelf === true) {
                    $(element).children().hide();
                } else {
                    $(element).hide();
                }
            }
        },
        update : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var allBindings = allBindingsAccessor();
            var activeContext = gc.app.sessionGet('activeContext');
            var ctxOptions = allBindings.ctxOptions || {};
            var keepSelf = ctxOptions.keepSelf || false;

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
            
            if ((!_.isUndefined(ctxVal) && !_.isString(ctxVal)) || (!_.isEmpty(ctxVal) && _.isString(ctxVal))) {
                if(keepSelf === true) {
                    $(element).children().show();
                } else {
                    $(element).show();
                }
            } else {
                if(keepSelf === true) {
                    $(element).children().hide();
                } else {
                    $(element).hide();
                }
            }
        }
    };
});
