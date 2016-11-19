define(['durandal/app', 'knockout', 'gc/gc', 'plugins/router', 'gc-pictogram'], function (app, ko, gc, router, pictogramAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.pictogram.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.pictogram.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
				{'name' : 'url', 'label' : 'app:modules.pictogram.gridColUrl'},
				{'name' : 'extention', 'label' : 'app:modules.pictogram.gridColExtention'},
				{'name' : 'name', 'label' : 'app:modules.pictogram.gridColName'},
				{'name' : 'size', 'label' : 'app:modules.pictogram.gridColSize'}
			];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(pictogramAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	this.pager.load();
	    },
		// ---------------------------------------------
		// Durandal callback.
		// ---------------------------------------------
		attached : function() {
			var self = this;

			Dropzone.autoDiscover = false;


			/* Dropzone magic. Automatically uploads file and adds the saved entry to list of slides in slideShowVM. */
			$('.dropzone-files').each(function( index, el ) {
				$form = $(this).get(0);

				if(!self.dzInited){
					self.dzInited = true;
					var dz = new Dropzone($form, { url: '/api/v1/pictograms/'});
					dz.on("sending", function(file, xhr, formData) {
						var activeStore = gc.app.activeStore();

						if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
							xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
						}
					});

					dz.on("success", function(file, data) {
						// Remove file from preview.
						dz.removeFile(file);

						router.navigate('//pictograms/details/' + data.data.pictogram.id);

					});
				}
			});

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
							if(txt) {
								summaryText += '<img class="gridStoreImg" src="' + store.iconPathXS + '" title="' + store.name + '"/><span class="gridStoreStatusImg product-status-tick fa fa-check"></span><br/>';
							} else {
								summaryText += '<img class="gridStoreImg" src="' + store.iconPathXS + '" title="' + store.name + '"/><span class="gridStoreStatusImg product-status-cross fa fa-square-o"></span><br/>';
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
					statusDescTxt = '<span class="gridStoreStatusImg product-status-cross fa fa-square-o"></span>';
				}

			}

			return statusDescTxt;
		}
    }
});