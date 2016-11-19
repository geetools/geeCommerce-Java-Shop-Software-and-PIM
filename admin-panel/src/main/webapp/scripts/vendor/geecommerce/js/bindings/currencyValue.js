define([ 'knockout', 'i18next', 'gc/gc', 'numeral' ], function(ko, i18n, gc, numeral) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);

            console.log('CURRENCY VALUE INIT1::: ', valueUnwrapped);
            
            var number = numeral(valueUnwrapped).format('0.00');
            
            console.log('CURRENCY VALUE INIT2::: ', valueUnwrapped, number);
            
            $(element).val(number);
            
            $(element).change(function() {
                var val = $(this).val();
                
                if (ko.isObservable(value)) {
                    value(numeral(val).format('0.00'));
                }
            });
        },
        update : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);

            console.log('CURRENCY VALUE UPDATE1::: ', valueUnwrapped);
            
            var number = numeral(valueUnwrapped).format('0.00');
            
            console.log('CURRENCY VALUE UPDATE2::: ', valueUnwrapped, number);
            
            $(element).val(number);
         }
    };
});
