define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {

            var $element = $(element);
            var value = valueAccessor();
            var allBindings = allBindings();
            var pickerOptions = allBindings.colorpickerOptions || {};

            $element.colorpicker(pickerOptions)

            if(value){
                $element.colorpicker('setValue', value)
            }

            $element.colorpicker().on('changeColor', function(e) { value(e.color.toHex()); });
		},
        update: function (element, valueAccessor, allBindingsAccessor) {
            var value = ko.utils.unwrapObservable(valueAccessor());
            $(element).colorpicker('setValue', value)
            $(element).change();
        }
	};
});

