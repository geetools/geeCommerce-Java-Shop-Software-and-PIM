define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductHistoryController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductHistoryController)) {
			throw new TypeError("ProductHistoryController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.productVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate');
	}

	ProductHistoryController.prototype = {
		constructor : ProductHistoryController,
		activate : function(productId) {
			var self = this;

			console.log('ACTIVATING HISTORY!');

			self.productVM = gc.app.sessionKGet('productVM');			
			
			var vm = self.productVM();

	    	// Pager columns
			var pagerColumns = [
              {'name' : 'version', 'label' : 'Version'},
              {'name' : 'historyDate', 'label' : 'Snapshot Datum'},
              {'name' : 'modifiedOn', 'label' : 'Geändert am'},
              {'name' : 'modifiedBy', 'label' : 'Geändert von'}
            ];
			
			var pagingOptions = productAPI.historyPagingOptions( productId, { columns : pagerColumns } )

			console.log('ACTIVATING HISTORY PO!', pagingOptions);

			
	    	// Init the pager.
        	self.historyPager = new gc.Pager(pagingOptions);


        	
        	return self.historyPager.load();
 		},
		attached : function(view, parent) {
		
			$(document).on('click.bs.collapse.data-api', '[data-toggle=collapse]', function (e) {
				e.preventDefault();
  				var $this   = $(this);
				var target  = $this.attr('data-target');
				var $target = $(target);
			    var data    = $target.data('bs.collapse');
			    var option  = data ? 'toggle' : $this.data();

				console.log('click.bs.collapse.data-api ////////// ', $(this), e, $target.hasClass('in'), data, option);

				
			});
		}
	};

	return ProductHistoryController;
});