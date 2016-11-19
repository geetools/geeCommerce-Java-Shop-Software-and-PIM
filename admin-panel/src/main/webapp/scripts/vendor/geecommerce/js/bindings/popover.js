define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		init : function(element, valueAccessor, allBindings, viewModel, bindingContext) {

			var options = ko.utils.unwrapObservable(valueAccessor());

			// Target element to bind popover to.
			var targetBinding = $(document).find(options.target);
			
			if(_.isUndefined(targetBinding)) {
				targetBinding = element;
			}
						
			// Element containing the popover-content.
			var popoverContent = $(document).find(options.content);

			var randId = randomId();
			var popoverId = 'popover-' + randId;
			
			var popoverTemplate = "<div class='popoverWrapper' data-bound='false' id='" + popoverId + "'>" + $(popoverContent).html() + "</div>";
            
			// Create popover.
            $(targetBinding).popover({
            	title: options.title,
                placement: options.placement,
                content: popoverTemplate,
                html: true,
                trigger: 'manual'
            });

            
            $(element).click(function (e) {
                $(targetBinding).popover('toggle');

                // Make sure  that we do not do ko-binding twice or knockout will throw an error.
                var isBound = parseBoolean($('#' + popoverId).attr('data-bound'));
                
                if(!isBound) {
                    var childBindingContext = bindingContext.createChildContext(viewModel);
                    var docEl = document.getElementById(popoverId);
                    ko.applyBindingsToDescendants(childBindingContext, docEl);

                    // Remember that we have already done the ko-binding.
                    $('#' + popoverId).attr('data-bound', 'true');
                }
                
                return true;
            })			
		}
	};
});
