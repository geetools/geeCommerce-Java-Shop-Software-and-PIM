define([ 'knockout', 'gc/gc' ], function(ko, gc) {
    //jqAuto -- main binding (should contain additional options to pass to autocomplete)
    //jqAutoSource -- the array to populate with choices (needs to be an observableArray)
    //jqAutoQuery -- function to return choices
    //jqAutoValue -- where to write the selected value
    //jqAutoSourceLabel -- the property that should be displayed in the possible choices
    //jqAutoSourceInputValue -- the property that should be displayed in the input box
    //jqAutoSourceValue -- the property to use for the value
    return {
        init: function(element, valueAccessor, allBindingsAccessor, viewModel) {
            var options = valueAccessor() || {},
                allBindings = allBindingsAccessor(),
                unwrap = ko.utils.unwrapObservable,
                modelValue = allBindings.jqAutoValue,
                modelValueId = allBindings.jqAutoValueId,
                source = allBindings.jqAutoSource,
                query = allBindings.jqAutoQuery,
                valueProp = allBindings.jqAutoSourceValue,
                inputValueProp = allBindings.jqAutoSourceInputValue || valueProp,
                labelProp = allBindings.jqAutoSourceLabel || inputValueProp,
                prevVal = '',
                prevLang = '';

            //function that is shared by both select and change event handlers
            function writeValueToModel(valueToWrite) {
                valueUnwrapped = ko.unwrap(modelValue) || [];

                // Update the context-object to reflect the change.
                gc.ctxobj.set(valueUnwrapped, gc.app.currentLang(), valueToWrite);
                prevVal = valueToWrite;
                prevLang = gc.app.currentLang();

                // Inform the ko-observable of the change.
                if (ko.isObservable(modelValue)) {
                    modelValue(valueUnwrapped);
                }
               /* if (ko.isWriteableObservable(modelValue)) {
                    modelValue(valueToWrite );
                } else {  //write to non-observable
                    if (allBindings['_ko_property_writers'] && allBindings['_ko_property_writers']['jqAutoValue'])
                        allBindings['_ko_property_writers']['jqAutoValue'](valueToWrite );
                }*/
            }

            //on a selection write the proper value to the model
            options.select = function(event, ui) {
                //writeValueToModel(ui.item ? ui.item.actualValue : null);
                writeValueToModel(ui.item.label ? ui.item.label() : null);
                if (modelValueId) {
                    if(ui.item.actualValue){
                        modelValueId(ui.item.actualValue);
                    }
                }
            };

            //on a change, make sure that it is a valid value or clear out the model value
            options.change = function(event, ui) {
                var currentValue = $(element).val();

                if(prevVal !== currentValue || prevLang !== gc.app.currentLang()){
                    //custom
                    writeValueToModel(currentValue);
                    if (modelValueId) {
                        modelValueId(null);
                    }
                }
                //original
               /* var matchingItem =  ko.utils.arrayFirst(unwrap(source), function(item) {
                    return unwrap(item[inputValueProp]) === currentValue;
                });

                if (!matchingItem) {
                    writeValueToModel(null);
                }*/

            }

            //hold the autocomplete current response
            var currentResponse = null;

            //handle the choices being updated in a DO, to decouple value updates from source (options) updates
            var mappedSource = ko.computed({
                read: function() {
                    mapped = ko.utils.arrayMap(unwrap(source), function(item) {
                        var result = {};
                        result.label = labelProp ? unwrap(item[labelProp]) : unwrap(item).toString();  //show in pop-up choices
                        result.value = inputValueProp ? unwrap(item[inputValueProp]) : unwrap(item).toString();  //show in input box
                        result.actualValue = valueProp ? unwrap(item[valueProp]) : item;  //store in model
                        return result;
                    });
                    return mapped;
                },
                write: function(newValue) {
                    source(newValue);  //update the source observableArray, so our mapped value (above) is correct
                    if (currentResponse) {
                        currentResponse(mappedSource());
                    }
                }
            });

            if (query) {
                options.source = function(request, response) {
                    currentResponse = response;
                    query.call(this, request.term, mappedSource);
                }
            } else {
                //whenever the items that make up the source are updated, make sure that autocomplete knows it
                mappedSource.subscribe(function(newValue) {
                    $(element).autocomplete("option", "source", newValue);
                });

                options.source = mappedSource();
            }

            ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                $(element).autocomplete("destroy");
            });


            //initialize autocomplete
            $(element).autocomplete(options);
        },
        update: function(element, valueAccessor, allBindingsAccessor, viewModel) {
            //update value based on a model change
            var allBindings = allBindingsAccessor(),
                unwrap = ko.utils.unwrapObservable,
                modelValue = unwrap(allBindings.jqAutoValue) || '',
                valueProp = allBindings.jqAutoSourceValue,
                inputValueProp = allBindings.jqAutoSourceInputValue || valueProp;

            // custom part
            var ctxVal = gc.ctxobj.val(modelValue, gc.app.currentLang());

            if (!_.isUndefined(ctxVal) && !_.isNull(ctxVal)) {
                $(element).val(ctxVal);
            } else {
                $(element).val('');
            }
            //$(element).change();

            //original
            //if we are writing a different property to the input than we are writing to the model, then locate the object
          /*  if (valueProp && inputValueProp !== valueProp) {
                var source = unwrap(allBindings.jqAutoSource) || [];
                var modelValue = ko.utils.arrayFirst(source, function(item) {
                    return unwrap(item[valueProp]) === modelValue;
                }) || {};
            }   */

            //update the element with the value that should be shown in the input
           // $(element).val(modelValue && inputValueProp !== valueProp ? unwrap(modelValue[inputValueProp]) : modelValue.toString());
        }

    };
});

