define([ 'knockout', 'i18next', 'gc/gc', 'numeral' ], function(ko, i18n, gc, numeral) {

    return {
        init : function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            var number = numeral(valueUnwrapped).format('0.00');
            
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
            
            var number = numeral(valueUnwrapped).format('0.00');
            
            $(element).val(number);
         }
    };
});
