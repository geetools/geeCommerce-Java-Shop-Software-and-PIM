define(['durandal/app', 'knockout', 'gc/gc', 'gc-magazine'], function (app, ko, gc, magazineAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.magazine.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.magazine.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'title', 'label' : 'app:modules.magazine.gridColTitle'},
              {'name' : 'showFrom', 'label' : 'app:modules.magazine.generalTabShowFrom', date:true},
              {'name' : 'showTo', 'label' : 'app:modules.magazine.generalTabShowTo', date:true},
              {'name' : 'enabled', 'label' : 'app:modules.magazine.gridColEnabled'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(magazineAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
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