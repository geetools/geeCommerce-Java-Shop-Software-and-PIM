define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute', 'gc-attribute-group' ], function(app, ko, gc, attrAPI, attrGroupsAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeTabMappingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeTabMappingController)) {
			throw new TypeError("AttributeTabMappingController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeGroupId = undefined;
		this.attributeGroupVM = {};
		this.query = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'setupSearchListener', 'dropFromSource', 'removeItemFromGroup', 'updatePositions', 'activate', 'attached');
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
		sourceAttributeGroupsPager: {},
		dropFromSource : function(data) {
        	var self = this;

			// Only add attribute to tab if it does not exist yet.
			var foundItem = _.findWhere(ko.unwrap(self.attributeGroupVM.items), { id : data.id });

			if(_.isUndefined(foundItem)) {

				if (data.code2 == "GROUP") {

					if(_.isUndefined(foundItem)) {
						attrGroupsAPI.addGroupToGroup(self.attributeGroupId, data.id).then(function( response ) {
							//var attributeGM = response.data.attributeTabMapping;

							self.attributeGroupVM.items.push( {id: data.id, label: data.label, code: data.code, code2: data.code2 } );
							self.sourceAttributeGroupsPager.data.remove(data);
						});
					}
				} else {
					if(_.isUndefined(foundItem)) {
						attrGroupsAPI.addAttributeToGroup(self.attributeGroupId, data.id).then(function( response ) {
							//var attributeGM = response.data.attributeGroupMapping;
							self.attributeGroupVM.items.push( { id: self.id, label: data.backendLabel, code: data.code, code2: data.code2 } );
							self.sourceAttributesPager.data.remove(data);
						});
					}
				}
			}

        },
        removeItemFromGroup : function(data) {
        	var self = this;
			attrGroupsAPI.removeItemFromGroup(self.attributeGroupId, data.id).then(function() {

				if(data.type == "ATTRIBUTE"){
					var foundAttribute = _.findWhere(ko.unwrap(self.sourceAttributesPager.data), { id : data.id });

					// Only add to drag&drop source container if it does not exist yet.
					if(_.isUndefined(foundAttribute)) {
						self.sourceAttributesPager.data.push( { id: data.id, backendLabel: data.label, code: data.code, code2: data.code2 } );
					}
				} else if (data.type == "ATTRIBUTE_GROUP") {
					var foundAttributeGroup = _.findWhere(ko.unwrap(self.sourceAttributeGroupsPager.data), { id : data.id });
					
					if(_.isUndefined(foundAttributeGroup)) {
						self.sourceAttributeGroupsPager.data.push( { id: data.id, label: data.label, code: data.code, code2: data.code2  } );
					}
				}
				
        		// Remove from target-container in view.
            	self.attributeGroupVM.items.remove(data);
        	});
        },
		updatePositions : function(domTableRows) {
			var self = this;
			
			var itemPositions = {};

            domTableRows.each(function(index, elem) {
                var row = $(elem),
                    pos = row.index()+1,
                    attrId = $(row).attr('data-id');

				itemPositions[attrId] = pos;
            });

			attrGroupsAPI.updateItemPositions(self.attributeGroupId, itemPositions).then(function(data) {
//                self.pager.refresh();
            });
		},
		activate : function(attributeGroupId) {
			var self = this;

            self.setupSearchListener();

			if(!_.isEmpty(attributeGroupId) && attributeGroupId != 'new') {
			
				self.attributeGroupVM = gc.app.sessionGet('attributeGroupVM');
				self.attributeGroupId = attributeGroupId;

                var pagerColumns = [
                    {'name' : 'code', 'label' : 'Code'},
                    {'name' : 'backendLabel', 'label' : 'Label', 'type': 'ContextObject', 'useRegexp' : true}
                ];

				var pagerGroupColumns = [
					{'name' : 'code', 'label' : 'Code'},
					{'name' : 'label', 'label' : 'Label', 'type': 'ContextObject', 'useRegexp' : true}
				];

		    	// Init the pagers.
	        	this.sourceAttributesPager = new gc.Pager(attrGroupsAPI.getAttrPagingOptions(attributeGroupId, {columns: pagerColumns, fields : [ 'code', 'code2', 'backendLabel' ], filter: { group : 'PRODUCT' }, sort : [ 'code' ] }));
				this.sourceAttributeGroupsPager = new gc.Pager(attrGroupsAPI.getAttrGroupPagingOptions(attributeGroupId, {columns: pagerGroupColumns, fields : [ 'code', 'label' ], sort : [ 'code' ] }));


//				this.sourceAttributesPager.load();
//				this.sourceAttributeGroupsPager.load();

				return attrAPI.getAttributes( 'product', { fields : [ 'code', 'code2', 'backendLabel' ],/* filter: { id : attributeIds.join() }*/ } ).then(function( response ) {
					return attrGroupsAPI.getAttributeGroups().then(function(response2){
						var itemsGroupMappings = self.attributeGroupVM.items();

						var attributes = response.data.attributes;
						var attributeGroups = response2.data.attributeGroups;

						if(!_.isEmpty(itemsGroupMappings)) {
							_.each(itemsGroupMappings, function(itemMapping) {

								if(itemMapping.type == "ATTRIBUTE"){
									var foundAttribute = _.findWhere( attributes, { id : itemMapping.id } );

									if(!_.isUndefined(foundAttribute)) {
										itemMapping.label = foundAttribute.backendLabel;
										itemMapping.code = foundAttribute.code;
										itemMapping.code2 = foundAttribute.code2;
									} else {
										itemMapping.label = [ { val: gc.app.i18n('app:common.noName') } ];
										itemMapping.code = gc.app.i18n('app:common.valueNotFound', { value: itemMapping.id });
									}
								} else if(itemMapping.type == "ATTRIBUTE_GROUP"){
									var foundGroup = _.findWhere( attributeGroups, { id : itemMapping.id } );

									if(!_.isUndefined(foundGroup)) {
										itemMapping.label = foundGroup.label;
										itemMapping.code = foundGroup.code;
										itemMapping.code2 = "GROUP";
									} else {
										itemMapping.label = [ { val: gc.app.i18n('app:common.noName') } ];
										itemMapping.code = gc.app.i18n('app:common.valueNotFound', { value: itemMapping.id });
									}
								}
							})
						}

					});
				}).then(function() {
					if (!_.isEmpty(self.attributeGroupId)) {
						return self.sourceAttributesPager.load().then(function(data) {
							return self.sourceAttributeGroupsPager.load().then(function(data) {

							});
						});
					}
				});
				// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
	       /* 	return attrTabsAPI.getAttributeTabMapping(attributeTabId).then(function(data) {
	            	
	            	var attributeTabMappings = data.data.attributeTabMappings;
	            	
	            	var attributeIds = _.pluck(attributeTabMappings, 'attributeId');
	            	
	            	if(!_.isEmpty(attributeIds)) {
	                	return attrAPI.getAttributes( { fields : [ 'code', 'code2', 'backendLabel' ], filter: { id : attributeIds.join() } } ).then(function( response ) {
                            
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
	        		if (!_.isEmpty(self.attributeGroupId)) {
	        			return self.sourceAttributesPager.load().then(function(data) {
							return self.sourceAttributeGroupsPager.load().then(function(data) {

							});
	            		});
	        		}
	        	});*/
        	}
		},
		setupSearchListener : function() {
			var self = this;

			self.query.subscribe(function(value) {
				self.sourceAttributesPager.columnValue('code', undefined);
				self.sourceAttributesPager.columnValue('backendLabel', '.*' + value + '.*');
				self.sourceAttributesPager.load().then(function(data) {
					if(_.isEmpty(data.data)) {
						self.sourceAttributesPager.columnValue('backendLabel', undefined);
						self.sourceAttributesPager.columnValue('code', value);
						self.sourceAttributesPager.load().then(function(data2) {
						});
					}
				});

				self.sourceAttributeGroupsPager.columnValue('code', undefined);
				self.sourceAttributeGroupsPager.columnValue('label', '.*' + value + '.*');
				self.sourceAttributeGroupsPager.load().then(function(data) {
					if(_.isEmpty(data.data)) {
						self.sourceAttributeGroupsPager.columnValue('label', undefined);
						self.sourceAttributeGroupsPager.columnValue('code', value);
						self.sourceAttributeGroupsPager.load().then(function(data2) {
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