define(['durandal/app', 'plugins/router', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute'], function (app, router, ko, gc, productAPI, attrAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductGridIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductGridIndexController)) {
			throw new TypeError("ProductGridIndexController constructor cannot be called as a function.");
		}

		this.gc = gc;
		this.app = gc.app;
		this.pager = {};
		this.gridTableMenuItems = [];
        this.groupOptions = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'computeStatus');
	}

	ProductGridIndexController.prototype = {
		constructor : ProductGridIndexController,
		statusDescription : function(data) {
				var statusDescTxt = '0_unbearbeitet';
		
				var attr = gc.attributes.find(data.attributes, 'status_description');
				var activeStore = gc.app.sessionGet('activeStore');
				var availableStores = gc.app.confGet('availableStores');

				// If no store is currently selected, just show the current values as text.
				if(_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
					statusDescTxt = '';
					var summaryText = '';
					
					var x = 0;
					_.each(availableStores, function(store) {
					
						if(store.id && store.id != '') {
						
							var txt = '0_unbearbeitet';
							
							if(!_.isUndefined(attr)) {
							
								var xOptionIds = attr.xOptionIds;
								var attributeOptions = attr.attributeOptions;
	
								if(!_.isEmpty(attributeOptions) && !_.isEmpty(xOptionIds)) {
									var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, store.id);
								
									if(optionId) {
										var option = _.findWhere(attributeOptions, {id : optionId[0]});
										txt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
									}
								}
							} else {
								txt = '0_unbearbeitet';
							}
							
							if(!_.isUndefined(txt)) {
								if(txt.length > 20) {
									summaryText += '<span class="gridStoreTxt"> ' + txt.substring(0,20) + '...</span><br/>';
								} else {
									summaryText += '<span class="gridStoreTxt"> ' + txt + '</span><br/>';
								}
								
								x++;
							}
						}
					});
					
					statusDescTxt = summaryText;
				} else {
				
					if(!_.isUndefined(attr)) {
					
						var xOptionIds = attr.xOptionIds;
						var attributeOptions = attr.attributeOptions;
				
						var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, activeStore.id);
						
						if(optionId) {
							var option = _.findWhere(attributeOptions, {id : optionId[0]});
							statusDescTxt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
						}
					}
				}
			
			return statusDescTxt;
		},
		statusImages : function(data) {
			var txt = '0_unbearbeitet';

			var attr = gc.attributes.find(data.attributes, 'status_image');
			
			if(!_.isUndefined(attr)) {
				var attributeOptions = attr.attributeOptions;

				if(!_.isEmpty(attributeOptions)) {
					txt = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
				}
			}
			
			return txt;
		},
		statusArticle : function(data) {
				var statusDescTxt = '0_nicht für online freigegeben';
		
				var attr = gc.attributes.find(data.attributes, 'status_article');
				var activeStore = gc.app.sessionGet('activeStore');
				var availableStores = gc.app.confGet('availableStores');

				// If no store is currently selected, just show the current values as text.
				if(_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
					statusDescTxt = '';
					var summaryText = '';
					
					_.each(availableStores, function(store) {
					
						if(store.id && store.id != '') {
						
							var txt = '0_nicht für online freigegeben';
							
							if(!_.isUndefined(attr)) {
							
								var xOptionIds = attr.xOptionIds;
								var attributeOptions = attr.attributeOptions;
	
								if(!_.isEmpty(attributeOptions) && !_.isEmpty(xOptionIds)) {
									var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, store.id);
								
									if(optionId) {
										var option = _.findWhere(attributeOptions, {id : optionId[0]});
										txt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
									}
								}
							} else {
								txt = '0_unbearbeitet';
							}
							
							if(!_.isUndefined(txt)) {
								if(txt.length > 20) {
									summaryText += '<span class="gridStoreTxt"> ' + txt.substring(0,20) + '...</span><br/>';
								} else {
									summaryText += '<span class="gridStoreTxt"> ' + txt + '</span><br/>';
								}
							}
						}
					});
					
					statusDescTxt = summaryText;
				} else {
				
					if(!_.isUndefined(attr)) {
					
						var xOptionIds = attr.xOptionIds;
						var attributeOptions = attr.attributeOptions;
				
						var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, activeStore.id);
						
						if(optionId) {
							var option = _.findWhere(attributeOptions, {id : optionId[0]});
							statusDescTxt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
						}
					}
				}
			
			return statusDescTxt;
		
		
		
//			var txt = '0_nicht für online freigegeben';

//			var attr = gc.attributes.find(data.attributes, 'status_article');
			
//			if(!_.isUndefined(attr)) {
//				var attributeOptions = attr.attributeOptions;

//				if(!_.isEmpty(attributeOptions)) {
//					txt = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
//				}
//			}
			
//			return txt;
		},
        group : function(data) {
            var group = this.optionsText(data, "product_group");
            if(group == ''){
                group = this.optionsText(data, "programme");
            }
            return group;

        },
		optionsText : function(data, code) {
			var txt = '';

			var attr = gc.attributes.find(data.attributes, code);
			
			if(!_.isUndefined(attr)) {
				var attributeOptions = attr.attributeOptions;

				if(!_.isEmpty(attributeOptions)) {
					txt = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
				}
			}
			
			return txt;
		},
		computeStatus : function(data) {
			var statusText = '';
			
			var descStatus = gc.attributes.find(data.attributes, 'status_description');
			var descStatusText = '0';

			if(!_.isUndefined(descStatus)) {
				var attributeOptions = descStatus.attributeOptions;

				if(!_.isEmpty(attributeOptions)) {
					descStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
				}
			}
			
			
			var imageStatus = gc.attributes.find(data.attributes, 'status_image');
			var imageStatusText = '0';
			
			if(!_.isUndefined(imageStatus)) {
				var attributeOptions = imageStatus.attributeOptions;

				if(!_.isEmpty(attributeOptions)) {
					imageStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
				}
			}
			
			
			var articleStatus = gc.attributes.find(data.attributes, 'status_article');
			var articleStatusText = '0';
			
			if(!_.isUndefined(articleStatus)) {
				var attributeOptions = articleStatus.attributeOptions;

				if(!_.isEmpty(attributeOptions)) {
					articleStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
				}
			}

			
			return (_.isUndefined(descStatusText) ? '0' : descStatusText.substring(0,1)) + '-' 
				+ (_.isUndefined(imageStatusText) ? '0' : imageStatusText.substring(0,1)) + '-' 
				+ (_.isUndefined(articleStatusText) ? '0' : articleStatusText.substring(0,1));
		},
	    activate: function(data) {
	    	gc.app.pageTitle(gc.app.i18n('app:modules.product.title'));
	    	gc.app.pageDescription(gc.app.i18n('app:modules.product.subtitle'));
	    	
			var attrStatusDesc = gc.app.dataGet('attr:status_description');
			var attrStatusImage = gc.app.dataGet('attr:status_image');
			var attrStatusArticle = gc.app.dataGet('attr:status_article');

			
			var statusDescSelectOptions = [];
			var statusImageSelectOptions = [];
			var statusArticleSelectOptions = [];
			
			if(!_.isEmpty(attrStatusDesc) && !_.isEmpty(attrStatusDesc.options)) {
				statusDescSelectOptions.push( { value : '', label : gc.app.i18n('app:common.choose') } );
				_.forEach(attrStatusDesc.options, function(option) {
					statusDescSelectOptions.push( { label : option.label.i18n(), value : option.id } );
				});
			}

			if(!_.isEmpty(attrStatusImage) && !_.isEmpty(attrStatusImage.options)) {
				statusImageSelectOptions.push( { value : '', label : gc.app.i18n('app:common.choose') } );
				_.forEach(attrStatusImage.options, function(option) {
					statusImageSelectOptions.push( { label : option.label.i18n(), value : option.id } );
				});
			}

			if(!_.isEmpty(attrStatusArticle) && !_.isEmpty(attrStatusArticle.options)) {
				statusArticleSelectOptions.push( { value : '', label : gc.app.i18n('app:common.choose') } );
				_.forEach(attrStatusArticle.options, function(option) {
					statusArticleSelectOptions.push( { label : option.label.i18n(), value : option.id } );
				});
			}

            var self = this;
            var computedOptions = [];
            computedOptions.push( { id : '', text : gc.app.i18n('app:common.choose') } );

            attrAPI.getAttributes( { fields : [ 'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label' ], filter: { code : "product_group, programme" } } ).then(function( response ) {
                var attributes = response.data.attributes;
                
                
                _.each(attributes, function(attr) {
                    var attrOptions = attr.options;
                    gc.ctxobj.enhance(attrOptions, [ 'label' ],  'any');
                    _.forEach(attrOptions, function(option) {
                    	if(option.label && option.label.i18n) {
	                        computedOptions.push( { text : option.label.i18n, id : "$opt." + attr.code + "__" + option.id } );
                    	}
                    });
                });
                
				self.groupOptions(computedOptions);
            });

	    	// Pager columns
			var pagerColumns = [
              { 'name' : '$attr.manufacturer', 'label' : 'app:modules.product.gridColManufacturer', cookieKey : 'mf' },
              { 'name' : 'type', 'label' : 'app:modules.product.gridColType', cookieKey : 't', 'selectOptions' :
            	  [
            	   { label: gc.app.i18n('app:common.choose'), value: '' },
            	   { label: gc.app.i18n('app:modules.product.typePHYSICAL'), value: 1 },
            	   { label: gc.app.i18n('app:modules.product.typeVARIANT_MASTER'), value: 2 },
            	   { label: gc.app.i18n('app:modules.product.typePROGRAMME'), value: 4 } 
            	  ] },
              { 'name' : 'group', combined: true, 'label' : 'app:modules.product.gridColGroup', cookieKey : 'g' },
              { 'name' : '$attr.ean', 'label' : 'app:modules.product.gridColEan', cookieKey : 'ean' },
              { 'name' : '$attr.article_number', 'label' : 'app:modules.product.gridColArticleNo', cookieKey : 'an' },
              { 'name' : '$attr.brand', 'label' : 'app:modules.product.gridColBrand', cookieKey : 'b' },
              { 'name' : '$attr.name', 'label' : 'app:modules.product.gridColName', cookieKey : 'n' },
              { 'name' : '$attr.supplier', 'label' : 'app:modules.product.gridColSupplier', cookieKey : 'sp' },
              { 'name' : 'deleted', 'label' : 'app:modules.product.gridColDeleted', cookieKey : 'd', 'selectOptions' :
            	  [
            	   { label: gc.app.i18n('app:common.no'), value: false },
            	   { label: gc.app.i18n('app:common.yes'), value: true },
            	   { label: gc.app.i18n('app:common.all'), value: '' },
            	  ] }
            ];
	    	
			this.gridTableMenuItems = [
			      {key:'menu-item-settings', icon: 'fa fa-cog', label: 'app:gridTable.menuItemSettings', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-columns" aria-hidden="true"></i>Edit columns', action: function() {}}, {text: '<i class="fa fa-filter" aria-hidden="true"></i>Apply filter', action: function() {gc.app.showQueryBuilderModal(true)}}]},
	              {key:'menu-item-new', icon: 'fa fa-plus-square-o', label: 'app:gridTable.menuItemNew', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-cube" aria-hidden="true"></i>' + gc.app.i18n('app:modules.product.newProductButton'), url: '#/products/details/new:product'}, {text: '<i class="fa fa-cubes" aria-hidden="true"></i>' + gc.app.i18n('app:modules.product.newVariantMasterButton'), url: '#/products/details/new:variant-master'}, {text: '<i class="fa fa-cubes" aria-hidden="true"></i>' + gc.app.i18n('app:modules.product.newProgrammeButton'), url: '#/products/details/new:programme'}, {text: '<i class="fa fa-cubes" aria-hidden="true"></i>' + gc.app.i18n('app:modules.product.newBundleButton'), url: '#/products/details/new:bundle'}]},
	              {key:'menu-item-select', icon: 'fa fa-check-square-o', label: 'app:gridTable.menuItemSelect', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-check-square-o" aria-hidden="true"></i>Select all rows on this page', action: function() {}}, {text: '<i class="fa fa-check-square-o" aria-hidden="true"></i>Select rows on all pages', action: function() {}}, {text: '<i class="fa fa-square-o" aria-hidden="true"></i>Deselect all rows', action: function() {}}]},
	              {key:'menu-item-actions', icon: 'fa fa-tasks', label: 'app:gridTable.menuItemActions', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-puzzle-piece" aria-hidden="true"></i>Batch update attributes', action: function() {}}]},
	              {key:'menu-item-import', icon: 'fa fa-download', label: 'app:gridTable.menuItemImport', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-download" aria-hidden="true"></i>Import data', action: function() {}}]},
	              {key:'menu-item-export', icon: 'fa fa-upload', label: 'app:gridTable.menuItemExport', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-upload" aria-hidden="true"></i>Export selected rows', action: function() {}}, {text: '<i class="fa fa-upload" aria-hidden="true"></i>Export wizard', action: function() {}}]},
	              {key:'menu-item-bookmark', icon: 'fa fa-star-o', label: 'app:gridTable.menuItemFavorites', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-star-o" aria-hidden="true"></i>Save as favorite', action: function() {}}, {text: '<i class="fa fa-star" aria-hidden="true"></i>Favorites', action: function() {}}]},
                  {key:'menu-item-refresh', icon: 'fa fa-refresh', label: 'app:gridTable.menuItemRefresh', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-refresh" aria-hidden="true"></i>Refresh data', action: function() {}}]},
                  {key:'menu-item-delete', icon: 'fa fa-trash', label: 'app:gridTable.menuItemDelete', target: '', targetType: '', contextMenu: [{text: '<i class="fa fa-trash" aria-hidden="true"></i>Delete selected rows', action: function() {}}, {text: '<i class="fa fa-trash" aria-hidden="true"></i>Delete wizard', action: function() {}}]}
			];
			
	    	// Init the pager.
        	this.pager = new gc.Pager(productAPI.pagingOptions({ columns : pagerColumns, multiContext : true }));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
	
	return ProductGridIndexController;
});