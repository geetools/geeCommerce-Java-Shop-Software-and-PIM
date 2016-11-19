define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        init: function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            $.fn.editable.defaults.mode = 'inline';

            var $element = $(element),
                value = valueAccessor(),
                allBindings = allBindingsAccessor(),
                editableOptions = allBindings.editableOptions || {};
                
            editableOptions.value = ko.utils.unwrapObservable(value);

            if (!editableOptions.name) {
                $.each(bindingContext.$data, function (k, v) {
                    if (v == value) {
                        editableOptions.name = k;
                        return false;
                    }
                });
            }

            //wrap calls to knockout.validation
            if (!editableOptions.validate && value && value.isValid) {
                editableOptions.validate = function (testValue) {
                    //have to set to new value, then call validate, then reset to original value
                    //not pretty, but works
                    var initalValue = value();
                    
                    value(testValue);
                    var res = value.isValid() ? null : ko.utils.unwrapObservable(value.error);
                    value(initalValue);
                    return res;
                }
            }
            
            if ((editableOptions.type === 'select' || editableOptions.type === 'select2' || editableOptions.type === 'checklist'|| editableOptions.type === 'typeahead') && !editableOptions.source && editableOptions.options) {

            	if (editableOptions.optionsCaption)
                    editableOptions.prepend = editableOptions.optionsCaption;

                //taken directly from ko.bindingHandlers['options']
                function applyToObject(object, predicate, defaultValue) {
                    var predicateType = typeof predicate;
                    if (predicateType == "function")    // Given a function; run it against the data value
                        return predicate(object);
                    else if (predicateType == "string") // Given a string; treat it as a property name on the data value
                        return object[predicate];
                    else                                // Given no optionsText arg; use the data value itself
                        return defaultValue;
                }
                
                editableOptions.source = function() {
                    return ko.utils.arrayMap(editableOptions.options(), function (item) {
                        var optionValue = applyToObject(item, editableOptions.optionsValue, item);
                        var optionText = applyToObject(item, editableOptions.optionsText, optionText);

                        if(ko.isObservable(optionValue) || _.isFunction(optionValue)) {
                        	optionValue = optionValue();
                        }

                        if(ko.isObservable(optionText) || _.isFunction(optionText)) {
                        	optionText = optionText();
                        }
                        
                        return {
                            value: ko.utils.unwrapObservable(optionValue),
                            text: ko.utils.unwrapObservable(optionText)
                        };
                    });
                };
            }

            if (editableOptions.visible && ko.isObservable(editableOptions.visible)) {
                editableOptions.toggle = 'manual';
            }

            //create editable
            var origHtml = $element.html(); // saves the original contents of the button
            var $editable = $element.editable(editableOptions);

            //update observable on save
            if (ko.isObservable(value)) {
              $editable.on('save.ko', function(e, params) {
                value(params.newValue);
              })
            };

            if (editableOptions.save) {
              $editable.on('save', editableOptions.save);
            }
            
            if (editableOptions.hidden) {
                $editable.on('hidden', editableOptions.hidden);
            }

            //setup observable to fire only when editable changes, not when options change
            //http://www.knockmeout.net/2012/06/knockoutjs-performance-gotcha-3-all-bindings.html
            ko.computed({
              read: function() {
                var val = ko.utils.unwrapObservable(valueAccessor());
                // Restores the original contents of the button.
                if (editableOptions.noHTML) {
                	setTimeout(function() {
                		$editable.html(origHtml);
                	}, 0);
                } else {
                	if (val === null) val = '';
                    $editable.editable('setValue', val, true)
                }
              },
              owner: this,
              disposeWhenNodeIsRemoved: element
            });
            
            //create editable
//            var $editable = $element.editable(editableOptions);
            
            //update observable on save
//            if (ko.isObservable(value)) {
//                $editable.on('save.ko', function (e, params) {
//                    value(params.newValue);
//                })
//            };

//            if (editableOptions.save) {
//                $editable.on('save', editableOptions.save);
//            }

            

            //setup observable to fire only when editable changes, not when options change
            //http://www.knockmeout.net/2012/06/knockoutjs-performance-gotcha-3-all-bindings.html
//            ko.computed({
//                read: function () {
//                    var val = ko.utils.unwrapObservable(valueAccessor());
//                    
//                    if (val === null) val = '';
//                    $editable.editable('setValue', val, true)
//                },
//                owner: this,
//                disposeWhenNodeIsRemoved: element
//            });

            if (editableOptions.visible && ko.isObservable(editableOptions.visible)) {
                ko.computed({
                    read: function () {
                        var val = ko.utils.unwrapObservable(editableOptions.visible());
                        if (val)
                            $editable.editable('show');
                    },
                    owner: this,
                    disposeWhenNodeIsRemoved: element
                });

                $editable.on('hidden.ko', function (e, params) {
                    editableOptions.visible(false);
                });
            }
        }
    };
});


