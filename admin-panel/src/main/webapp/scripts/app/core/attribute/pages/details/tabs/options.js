define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute', 'jquery.spectrum' ], function(app, ko, gc, attrAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeOptionsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeOptionsController)) {
			throw new TypeError("AttributeOptionsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeId = undefined;
		this.attributeVM = {};
		this.attributeOptionTags = ko.observableArray();
		this.newAttributeOptions = ko.observableArray();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'updatePositions', 'addNewOptionRow', 'removeNewOptionRow', 'removeOption', 'activate', 'attached');
	}

	AttributeOptionsController.prototype = {
		constructor : AttributeOptionsController,
    	// Fields used for filtering and sorting.
		gridHeaders: [
          {'field' : 'null', 'label' : 'Default Language Label'},
          {'field' : 'label', 'label' : 'Label'},
          {'field' : 'thumbnailColor', 'label' : 'Thumbnail (Farbe)'},
          {'field' : 'thumbnailStyle', 'label' : 'Thumbnail (Style)'},
          {'field' : 'tags', 'label' : 'Tags'}, 
          {'field' : 'position', 'label' : 'Position'},
          {'field' : '', 'label' : ''}
        ],
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
    	saveData : function(context) {
			var self = this;

			var createPromise = undefined;
			var updatePromise = undefined;
			
			//-----------------------------------------
			// New Options
			//-----------------------------------------
			var newAttributeOptions = ko.toJS(self.newAttributeOptions);
			
			if(_.isArray(newAttributeOptions) && !_.isEmpty(newAttributeOptions)) {
				createPromise = attrAPI.createOptions(self.attributeId, newAttributeOptions);
			}

			// Make sure that we always have a promise, so that the next step is executed,
			if(_.isUndefined(createPromise)) {
				createPromise = $.when({});
			}
			
			//-----------------------------------------
			// Updates
			//-----------------------------------------
			
			var rows = self.pager.getData();
			var updates = [];
			
			if(_.isArray(rows) && !_.isEmpty(rows)) {
				createPromise.then(function(createData) {
					_.each(rows, function(data) {
						var updateModel = gc.app.newUpdateModel()
							.id(data.id)
							.field('attributeId', data.attributeId)
							.field('label', data.label, true)
							.field('thumbnailColor', data.thumbnailColor)
							.field('thumbnailStyle', data.thumbnailStyle)
							.field('position', data.position || 0)
							.field('tags', data.tags || '');
						
						updates.push(updateModel.data());
					});
					
					updatePromise = attrAPI.updateOptions(self.attributeId, updates);
				});
			}
			
			// Make sure that we always have a promise, so that the next step is executed,
			if(_.isUndefined(updatePromise)) {
				updatePromise = $.when({});
			}
			
			$.when(createPromise, updatePromise).done(function(data1, data2) {
        		self.pager.refresh().then(function() {
        		    context.saved();
        			
            		self.newAttributeOptions.removeAll();
        		});
        		
        		attrAPI.getOptionGroupingTags(self.attributeId).then(function(data) {
            		self.attributeOptionTags(data);
        		});
			});
		},
		updatePositions : function(domTableRows) {
			var self = this;
			
			var optionPositions = {};
			
			domTableRows.each(function(index, elem) {
                var row = $(elem),
	                pos = row.index()+1,
	                optionId = $(row).attr('data-id');

                optionPositions[optionId] = pos;
			});
			
			attrAPI.updateOptionPositions(self.attributeId, optionPositions).then(function(data) {
				self.pager.refresh();
			});
		},
		addNewOptionRow : function(data, element) {
			var self = this;
			
			self.newAttributeOptions.push({
				attributeId: self.attributeId,
				label: ko.observableArray([]),
				thumbnailStyle: ko.observable(),
				thumbnailColor: ko.observable(),
				tags: ko.observableArray([]),
				position: ko.observable()
			});
		},
		removeNewOptionRow : function(data, element) {
			var self = this;
			self.newAttributeOptions.remove(data);
		},
		removeOption : function(data, element) {
			var self = this;
			var _data = ko.toJS(data);

			attrAPI.removeOption(_data.attributeId, _data.id).then(function() {
				self.pager.refresh();
			});
		},
		activate : function(attributeId) {
			var self = this;
			
			self.attributeVM = gc.app.sessionGet('attributeVM');
			self.attributeId = attributeId;
			
	    	// Init the pager.
        	this.pager = new gc.Pager(attrAPI.getAttributeOptionsPagingOptions(attributeId));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load().then(function() {
        		attrAPI.getOptionGroupingTags(attributeId).then(function(data) {
            		self.attributeOptionTags(data);
        		});
        	});
		},
		attached : function() {
			var self = this;
			
			$('.sortableAttributeOptions').sortable({
	            update: function() {
	            	self.updatePositions($(this).children('tr'));
	            }
	        });
			
            $('#attributeOptionsForm').addClass('save-button-listen-area');
            
            gc.app.onSaveEvent(function(context) {
                var id = $('.tab-content>.active').attr('id');
               
                if(id == 'attr_options') {
                    self.saveData(context);
                }
            });
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-attribute-details-options', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
			
			var spectrumOptions = {
				preferredFormat: "hex",
				allowEmpty: true,
				showInitial: true,
				showButtons: true,
			    showPaletteOnly: true,
			    showAlpha: true,
			    togglePaletteOnly: true,
			    togglePaletteMoreText: 'mehr',
			    togglePaletteLessText: 'weniger',
				cancelText: 'abbrechen',
    			chooseText: 'ok',
			    palette: [
			        ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
			        ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
			        ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"],
			        ["#ea9999","#f9cb9c","#ffe599","#b6d7a8","#a2c4c9","#9fc5e8","#b4a7d6","#d5a6bd"],
			        ["#e06666","#f6b26b","#ffd966","#93c47d","#76a5af","#6fa8dc","#8e7cc3","#c27ba0"],
			        ["#c00","#e69138","#f1c232","#6aa84f","#45818e","#3d85c6","#674ea7","#a64d79"],
			        ["#900","#b45f06","#bf9000","#38761d","#134f5c","#0b5394","#351c75","#741b47"],
			        ["#600","#783f04","#7f6000","#274e13","#0c343d","#073763","#20124d","#4c1130"]
			    ],
			    change: function(color) {
			    	gc.utils.triggerToolbar($(this));
			    }
			};

			$(".option-thumbnail-color").spectrum(spectrumOptions);
			
			self.pager.onload(function() {
				$(".option-thumbnail-color").spectrum(spectrumOptions);
			});
		}
	};

	return AttributeOptionsController;
});