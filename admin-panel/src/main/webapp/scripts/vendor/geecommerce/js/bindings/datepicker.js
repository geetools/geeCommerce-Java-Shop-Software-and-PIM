define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
        init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            var observable = valueAccessor();
            var options = allBindings().datepickerOptions || {};

            // Create datepicker.
            $(element).datepicker({
                format: options.format || 'mm/dd/yyyy',
                weekStart: options.weekStart || 0,
                viewMode: options.viewMode || 0,
                minViewMode: options.minViewMode || 0,
                autoclose: true
            })/*.on('changeDate', function(ev) {
                observable(ev.date);
            });*/

            ko.utils.registerEventHandler(element, "change", function() {
                $el = $(element);
                var observable = valueAccessor();
                if($el.val() == ""){
                    observable(null);
                } else {
                    observable($el.datepicker("getDate"));
                }
            });

        },
        update: function(element, valueAccessor) {
            var value = ko.utils.unwrapObservable(valueAccessor()),
                $el = $(element),
                current = $el.datepicker("getDate");

            if (value - current !== 0) {
                if(!value) {
                    $el.datepicker("update", null);
                } else {
                    $el.datepicker("update", new Date(value));
                }

            }
        }
	};
});
