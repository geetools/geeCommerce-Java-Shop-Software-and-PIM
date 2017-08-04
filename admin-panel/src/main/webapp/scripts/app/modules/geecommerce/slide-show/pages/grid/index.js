define(['durandal/app', 'knockout', 'gc/gc', 'gc-slide-show'], function (app, ko, gc, slideShowAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function SlideshowGridIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof SlideshowGridIndexController)) {
			throw new TypeError("SlideshowGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeSlideshow', 'statusEnabled', 'statusEnabledTitle');
	}
	
	SlideshowGridIndexController.prototype = {
		constructor : SlideshowGridIndexController,
	    activate: function(data) {
	    	var self = this;
            gc.app.pageTitle(gc.app.i18n('app:modules.slide-show.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.slide-show.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'name', 'label' : 'app:modules.slide-show.gridColName'},
              {'name' : 'dateFrom', 'label' : 'app:modules.slide-show.gridColDateFrom', date: true},
              {'name' : 'dateTo', 'label' : 'app:modules.slide-show.gridColDateTo', date: true},
			  {'name' : 'enabled', 'label' : 'app:modules.slide-show.gridColEnabled'},
			  {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	self.pager = new gc.Pager(slideShowAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    removeSlideshow: function(slideshow) {
    		var self = this;
    		var yes = gc.app.i18n('app:common.yes');
    		var no = gc.app.i18n('app:common.no');
    		
			app.showMessage(gc.app.i18n('app:modules.slide-show.confirmDelete'), gc.ctxobj.val(slideshow.name, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
					slideShowAPI.removeSlideshow(slideshow.id).then(function() {
		    			self.pager.removeData(slideshow);
		    		});
				}
			});
    	},
    	statusEnabledTitle : function() {
    		var contextMap = gc.app.confGet('contextMap');
    		if (contextMap.length > 2) { 
    			return gc.app.i18n('app:modules.slide-show.gridColEnabledMulti');
			} else {
				return gc.app.i18n('app:modules.slide-show.gridColEnabled');
			}
    	},
		statusEnabled : function(data) {
			var statusDescTxt = '';

			var value = data.enabled;
			var activeStore = gc.app.sessionGet('activeStore');
			var availableStores = gc.app.confGet('availableStores');

			// If no store is currently selected, just show the current values as text.
			if(_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
				var summaryText = '';
				_.each(availableStores, function(store) {
					if(store.id && store.id != '') {

						var txt = false;

						if(!_.isUndefined(value)) {
							txt = gc.ctxobj.val(value, undefined, 'closest', store.id);
						}

						if(!_.isUndefined(txt)) {
							if (availableStores.length > 2) { 
								if(txt) {
									summaryText += '<img class="gridStoreImg" src="' + store.iconPathXS + '" title="' + store.name + '"/><br/>';
								} else {
									summaryText += '';
								}
							} else {
								if(txt) {
									summaryText += '<span class="gridStoreStatusImg product-status-tick fa fa-check"></span>';
								} else {
									summaryText += '<span class="gridStoreStatusImg product-status-tick fa fa-times"></span>';
								}
							}
						}
					}
				});

				statusDescTxt = summaryText;
			} else {
				var txt = false;
				if(!_.isUndefined(value)) {
					txt = gc.ctxobj.val(value, undefined, 'closest', activeStore.id);

				}
				if(txt) {
					statusDescTxt = '<span class="gridStoreStatusImg product-status-tick fa fa-check"></span>';
				} else {
					statusDescTxt = '<span class="gridStoreStatusImg product-status-cross fa fa-circle-o"></span>';
				}

			}

			return statusDescTxt;
		}
    }
	return SlideshowGridIndexController;
});