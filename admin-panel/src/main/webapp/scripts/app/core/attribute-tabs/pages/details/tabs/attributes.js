define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute', 'gc-attribute-tabs' ], function(app, ko, gc, attrAPI, attrTabsAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeTabMappingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeTabMappingController)) {
			throw new TypeError("AttributeTabMappingController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeTabId = undefined;
		this.attributeTabVM = {};
		this.query = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'setupSearchListener', 'dropFromSource', 'removeAttributeFromTab', 'updatePositions', 'activate', 'attached');
	}

	AttributeTabMappingController.prototype = {
		constructor : AttributeTabMappingController,
    	// Fields used for filtering and sorting.
		gridHeaders: [
          {'field' : 'label', 'label' : 'Label'},
          {'field' : 'tags', 'label' : 'Tags'}, 
          {'field' : 'position', 'label' : 'Position'},
          {'field' : '', 'label' : ''}
        ],
        // The pager takes care of filtering, sorting and paging functionality.
        sourceAttributesPager: {},
        dropFromSource : function(data) {
        	var self = this;

        	// Only add attribute to tab if it does not exist yet.
    		var foundAttribute = _.findWhere(ko.unwrap(self.attributeTabVM.attributes), { attributeId : data.id });
        	
    		if(_.isUndefined(foundAttribute)) {
            	attrTabsAPI.addAttributeToTab(self.attributeTabId, data.id).then(function( response ) {
                    var attributeTM = response.data.attributeTabMapping;

                	self.attributeTabVM.attributes.push( { tabId: self.attributeTabId, attributeId: data.id, backendLabel: data.backendLabel, id: attributeTM.id, code: data.code, code2: data.code2 } );
                	self.sourceAttributesPager.data.remove(data);
            	});
    		}
        },
        removeAttributeFromTab : function(data) {
        	var self = this;
        	
        	attrTabsAPI.removeAttributeFromTab(self.attributeTabId, data.attributeId).then(function() {
        		// See if the attribute is already in the source container.
        		var foundAttribute = _.findWhere(ko.unwrap(self.sourceAttributesPager.data), { id : data.attributeId });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundAttribute)) {
                	self.sourceAttributesPager.data.push( { id: data.attributeId, backendLabel: data.backendLabel, code: data.code, code2: data.code2 } );
        		}
        		
        		// Remove from target-container in view.
            	self.attributeTabVM.attributes.remove(data);
        	});
        },
		updatePositions : function(domTableRows) {
			var self = this;
			
			var attrPositions = {};

            domTableRows.each(function(index, elem) {
                var row = $(elem),
                    pos = row.index()+1,
                    attrId = $(row).attr('data-id');

                attrPositions[attrId] = pos;
            });

            attrTabsAPI.updateAttributePositions(self.attributeTabId, attrPositions).then(function(data) {
//                self.pager.refresh();
            });
		},
		activate : function(attributeTabId) {
			var self = this;

            self.setupSearchListener();

			if(!_.isEmpty(attributeTabId) && attributeTabId != 'new') {
			
				self.attributeTabVM = gc.app.sessionGet('attributeTabVM');
				self.attributeTabId = attributeTabId;


                var pagerColumns = [
                    {'name' : 'code', 'label' : 'Code'},
                    {'name' : 'backendLabel', 'label' : 'Label', 'type': 'ContextObject', 'useRegexp' : true}
                ];

		    	// Init the pager.
	        	this.sourceAttributesPager = new gc.Pager(attrTabsAPI.getPagingOptions(attributeTabId, {columns: pagerColumns, fields : [ 'code', 'code2', 'backendLabel' ], filter: { group : 'PRODUCT' }, sort : [ 'code' ] }));
	        	
	        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
	        	return attrTabsAPI.getAttributeTabMapping(attributeTabId).then(function(data) {
	            	
	            	var attributeTabMappings = data.data.attributeTabMappings;
	            	
	            	var attributeIds = _.pluck(attributeTabMappings, 'attributeId');
	            	
	            	if(!_.isEmpty(attributeIds)) {
	                	return attrAPI.getAttributes('product', { fields : [ 'code', 'code2', 'backendLabel' ], filter: { id : attributeIds.join() } } ).then(function( response ) {
                            
	                		var attributes = response.data.attributes;

	                    	_.each(attributeTabMappings, function(attributeTabMapping) {
	                    		var foundAttribute = _.findWhere( attributes, { id : attributeTabMapping.attributeId } );
	                    		
	                    		if(!_.isUndefined(foundAttribute)) {
	                        		attributeTabMapping.backendLabel = foundAttribute.backendLabel;
	                        		attributeTabMapping.code = foundAttribute.code;
	                        		attributeTabMapping.code2 = foundAttribute.code2;
	                    		} else {
	                        		attributeTabMapping.backendLabel = [ { val: gc.app.i18n('app:common.noName') } ];
	                        		attributeTabMapping.code = gc.app.i18n('app:common.valueNotFound', { value: attributeTabMapping.attributeId });
	                    		}
	                    	})
	                    	
	                		self.attributeTabVM.attributes(attributeTabMappings);
	                	});
	            	}
	        	}).then(function() {
	        		if (!_.isEmpty(self.attributeTabId)) {
	        			return self.sourceAttributesPager.load().then(function(data) {
	            		});
	        		}
	        	});
        	}
		},
		setupSearchListener : function() {
			var self = this;

			self.query.subscribe(function(value) {
				console.log('SEARCHING FOR: ', value);

				self.sourceAttributesPager.columnValue('code', undefined);
				self.sourceAttributesPager.columnValue('backendLabel', '.*' + value + '.*');
				self.sourceAttributesPager.load().then(function(data) {
                    console.log(data)
					if(_.isEmpty(data.data)) {
						self.sourceAttributesPager.columnValue('backendLabel', undefined);
						self.sourceAttributesPager.columnValue('code', value);
						self.sourceAttributesPager.load().then(function(data2) {
                            console.log(data2)
						});
					}
				});
			});
		},
		attached : function() {
			var self = this;
			
			$('.sortableAttributeOptions').sortable({
	            update: function() {
	            	self.updatePositions($(this).children('li'));
	            }
	        });
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-attribute-tabs-details-mapping', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};

	return AttributeTabMappingController;
});