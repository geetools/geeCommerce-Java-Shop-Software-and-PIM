define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $element = $(element), value = valueAccessor(), allBindings = allBindingsAccessor(), data = allBindings.data || [], ctxOptions = allBindings.ctxOptions || {}, selectOptions = allBindings.selectOptions || {};

            var unwrappedData = ko.unwrap(data);
            var unwrappedValue = ko.unwrap(value);

            var asArray = selectOptions.asArray || false;
            var contextMap = gc.app.confGet('contextMap');
            var title = selectOptions.title;

            if (title) {
                $element.append('<option value="" data-hidden="true">' + title + '</option>');
                delete selectOptions.title;
            }

            // -----------------------------------------------------------
            // Create the select-picker
            // -----------------------------------------------------------

            $select = $element.selectpicker(selectOptions);

            // -----------------------------------------------------------
            // We need to populate the data in a ko.computed function
            // in case the data or the language changes at some point.
            // -----------------------------------------------------------

            ko.computed(function() {
                var lang = gc.app.currentLang();
                var unwrappedData = ko.unwrap(data);
                var unwrappedValue = ko.unwrap(value);

                var activeContext = gc.app.sessionGet('activeContext');
                var ctxVal;

                // Make sure we get the new value if active context changes.
                if (!_.isUndefined(ctxOptions.scopes) && ctxOptions.scopes.length == 1 && ctxOptions.scopes[0] == 'global') {
                    ctxVal = gc.ctxobj.plain(unwrappedValue);
                } else {
                    ctxVal = gc.ctxobj.val(unwrappedValue, undefined, undefined, activeContext);
                }

                $element.empty();

                // Rebuild list in case language changes.
                _.each(unwrappedData, function(option) {
                    if (ko.isObservable(option.text) || _.isFunction(option.text)) {
                        $("<option />", {
                            value : option.id,
                            text : option.text()
                        }).appendTo($element);
                    } else {
                        $element.append('<option value="' + option.id + '">' + option.text + '</option>');
                    }
                });

                // Preselect the options.
                if (!_.isUndefined(ctxVal) && $element.prop('multiple') && _.isArray(ctxVal)) {
                    $element.selectpicker('val', ctxVal);
                } else if (!_.isUndefined(ctxVal)) {
                    $element.selectpicker('val', ctxVal);
                } else {
                    $element.selectpicker('val', '');
                }

                $element.selectpicker('refresh');
            });

            // -----------------------------------------------------
            // Listen for any changes to select box.
            // -----------------------------------------------------
            $element.change(function() {
                var newValue = $element.val();

                // If no value has been selected just return.
                if (_.isNull(newValue) || _.isUndefined(newValue)) {
                    return;
                }

                // Observable value in form.
                var unwrappedValue = ko.unwrap(value);
                // Currently selected store.
                var activeContext = gc.app.sessionGet('activeContext');

                // Make sure that we have an array to add the value to.
                if (_.isUndefined(unwrappedValue)) {
                    unwrappedValue = [];
                }

                // Attempt to get the current value for the selected store.
                var ctxVal = gc.ctxobj.val(unwrappedValue, undefined, undefined, activeContext);

                // Make sure that we have an array to add the value to.
                if (_.isUndefined(ctxVal) && $element.prop('multiple'))
                    ctxVal = [];

                if (ctxVal !== undefined && _.isArray(ctxVal)) {
                    if ($element.prop('multiple')) {
                        // If multiple selection is allowed, add the new value.
                        if (!_.contains(ctxVal, newValue)) {
                            ctxVal.push(newValue);
                        }
                    } else {
                        // Otherwise reset array before adding value.
                        ctxVal.length = 0;
                        ctxVal.push(newValue);
                    }
                } else {
                    ctxVal = newValue;
                }

                // Now add the new value to the xOpions (contextObject).
                gc.ctxobj.set(unwrappedValue, undefined, ctxVal, activeContext);

                // Tell observers that value has changed.
                value(unwrappedValue);
            });
        },
        update : function(element, valueAccessor, allBindingsAccessor) {
        }
    };
});
