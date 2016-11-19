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
            } else {
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext);
            }

            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).val(ctxVal);
            } else {
                $(element).val('');
            }

            // Make sure that any edits are passed onto the ko-observable.
            $(element).change(function() {
                var activeCtx = gc.app.sessionGet('activeContext');

                valueUnwrapped = ko.unwrap(value);

                // Update the context-object to reflect the change.
                gc.ctxobj.set(valueUnwrapped, undefined, gc.utils.typedValue($(this)), activeCtx);

                // Inform the ko-observable of the change.
                if (ko.isObservable(value)) {
                    value(valueUnwrapped);
                }
            });
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
                ctxVal = gc.ctxobj.val(valueUnwrapped, undefined, undefined, activeContext);
            }

            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).val(ctxVal);
            } else {
                $(element).val('');
            }
        }
    };
});
