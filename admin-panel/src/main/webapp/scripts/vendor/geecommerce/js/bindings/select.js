define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $element = $(element), value = valueAccessor(), allBindings = allBindingsAccessor(), data = allBindings.data || [], sort = allBindings.sort || false, selectOptions = allBindings.selectOptions || {};

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

                if (sort) {

                    var firstItem;
                    if (unwrappedData[0] && unwrappedData[0].id == '') {
                        firstItem = unwrappedData.shift();
                    }

                    unwrappedData = unwrappedData.sort(function(a, b) {
                        var _a = ko.isObservable(a.text) || _.isFunction(a.text) ? a.text() : a.text;
                        var _b = ko.isObservable(b.text) || _.isFunction(b.text) ? b.text() : b.text;

                        return _a.localeCompare(_b);
                    });

                    if (firstItem) {
                        unwrappedData.unshift(firstItem);
                    }
                }

                $element.empty();

                // Rebuild list in case language changes.
                _.each(unwrappedData, function(option) {
                    if (ko.isObservable(option.text) || _.isFunction(option.text)) {
                        if (option.text() && option.text() != '' && !_.isUndefined(option.id))
                            $("<option />", {
                                value : option.id,
                                text : option.text()
                            }).appendTo($element);
                    } else {
                        if (option.text && option.text != '' && !_.isUndefined(option.id))
                            $element.append('<option value="' + option.id + '">' + option.text + '</option>');
                    }
                });

                $element.selectpicker('refresh');
            });

            ko.computed(function() {
                var lang = gc.app.currentLang();
                var unwrappedValue = ko.unwrap(value);
                var unwrappedData = ko.unwrap(data);

                if (!_.isEmpty(unwrappedData)) {
                    // Preselect the options.
                    if (!_.isEmpty(unwrappedValue) && $element.prop('multiple') && _.isArray(unwrappedValue)) {
                        $element.selectpicker('val', unwrappedValue);
                    } else if (!_.isUndefined(unwrappedValue)) {
                        $element.selectpicker('val', unwrappedValue);
                    } else {
                        $element.selectpicker('val', '');
                    }

                    $element.selectpicker('refresh');
                }
            });

            // -----------------------------------------------------
            // Listen for any changes to select box.
            // -----------------------------------------------------
            $element.change(function() {
                var newValue = $element.val();

                // If no value has been selected, just give us an empty array to work with.
                if ($element.prop('multiple') && (_.isNull(newValue) || _.isUndefined(newValue)))
                    newValue = [];

                // Observable value in form.
                var unwrappedValue = ko.unwrap(value);

                // Make sure that we have an array to add the value to.
                if ($element.prop('multiple') && _.isUndefined(unwrappedValue)) {
                    unwrappedValue = [];
                }

                // First we remove any unselected items.
                if (unwrappedValue && _.isArray(unwrappedValue)) {
                    for (var i = 0; i < unwrappedValue.length; i++) {
                        if (unwrappedValue[i] && newValue.indexOf(unwrappedValue[i]) == -1) {
                            unwrappedValue.splice(i, 1);
                        }
                    }
                }

                // Then we add the new values.
                if ($element.prop('multiple') && _.isArray(unwrappedValue)) {
                    if (_.isArray(newValue)) {
                        for (var i = 0; i < newValue.length; i++) {
                            if (newValue[i] && newValue[i] != '' && unwrappedValue.indexOf(newValue[i]) == -1) {
                                value.push(newValue[i]);
                            }
                        }
                    } else if (newValue && newValue != '' && unwrappedValue.indexOf(newValue) == -1) {
                        value.push(newValue);
                    }
                } else if (!$element.prop('multiple')) {
                    if (_.isArray(unwrappedValue)) {
                        unwrappedValue.length = 0;
                        unwrappedValue.push(newValue);
                        value(unwrappedValue);
                    } else {
                        value(newValue);
                    }
                }
            });
        }
    };
});
