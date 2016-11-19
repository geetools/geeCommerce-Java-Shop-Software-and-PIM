define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	//-----------------------------------------------------------------
	// Product view model
	//-----------------------------------------------------------------
	function ProductVM(productId, articleNumber, name, name2, variantValues) {
		var self = this;
		self.id = productId;
		self.articleNumber = articleNumber;
		self.name = name;
		self.name2 = name2;
		self.media = ko.observableArray([]);
		self.variantValues = variantValues;
		self.title = function() {
			var title = '';
			
			if(!_.isEmpty(self.articleNumber)) {
				title = gc.ctxobj.plain(self.articleNumber);
			}
			
			if(self.variantValues) {
				_.each(self.variantValues, function(vv) {
					if(!_.isEmpty(vv.value)) {
						title += ', ' + gc.ctxobj.any(vv.value, gc.app.currentUserLang());
					}
				});
			} else if(self.name) {
				if(!_.isEmpty(self.name)) {
					title += ', ' + gc.ctxobj.any(self.name, gc.app.currentUserLang());
				}
			}
			
			return title == '' ? self.id : title;
		};
	}
	
	//-----------------------------------------------------------------
	// Media view model (found in observableArray productVM.media).
	//-----------------------------------------------------------------
	function MediaVM(mediaAssetId, productId, storeId, path, webPath, webThumbnailPath, previewImagePath, previewImageWebPath, previewImageWebThumbnailPath, title, position, mimeType, mediaTypeIds, defaultVariantImage, defaultBundleImage, defaultProgrammeImage, enabled) {
		var self = this;
		self.id = mediaAssetId;
		self.productId = productId;
		self.storeId = storeId;
		self.path = ko.observable(path);
		self.webPath = ko.observable(webPath);
		self.webThumbnailPath = ko.observable(webThumbnailPath);
		self.previewImagePath = ko.observable(previewImagePath);
		self.previewImageWebPath = ko.observable(previewImageWebPath);
		self.previewImageWebThumbnailPath = ko.observable(previewImageWebThumbnailPath);
		self.title = ko.observable(title);
		self.position = ko.observable(position);
		self.mimeType = ko.observable(mimeType);
		self.mediaTypeIds = ko.observableArray(mediaTypeIds);
		self.defaultVariantImage = ko.observable(defaultVariantImage);
		self.defaultBundleImage = ko.observable(defaultBundleImage);
		self.defaultProgrammeImage = ko.observable(defaultProgrammeImage);
		self.enabled = ko.observable(enabled);

		self.filename = ko.computed(function() {
			return self.path().substring(self.path().lastIndexOf('/')+1);
		});
		
		self.fileExtension = ko.computed(function() {
			return self.path().substring(self.path().lastIndexOf('.')+1);
		});
		
		self.isDocument = ko.computed(function() {
			return self.mimeType().startsWith('text/') || self.mimeType().startsWith('application/');
		});
	}

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductMediaController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductMediaController)) {
			throw new TypeError("ProductMediaController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.utils = gc.utils;
		this.productId = null;
		this.articleNumber = null;
		this.name = null;
		// Products and variants.
		this.products = ko.observableArray([]);
		this.mediaTypeOptions = ko.observableArray([]);
		this.catalogMediaTypes = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'addMediaAsset', 'removeMediaAsset', 'refreshMediaAssets', 'updatePositions', 'activate', 'attached');
	}

	ProductMediaController.prototype = {
		constructor : ProductMediaController,
    	saveData : function(view, parent, toolbar, args) {
			var self = this;
			
			var updates = [];
			var pVM = _.findWhere(self.products(), { id: args.productId });
			
			if(!_.isEmpty(pVM)) {
				var mediaAssets = pVM.media();

				if(!_.isEmpty(mediaAssets)) {
					
					_.each(mediaAssets, function(data) {
						
						var updateModel = gc.app.newUpdateModel()
							.id(data.id)
							.field('title', data.title(), true)
							.field('mediaTypeIds', data.mediaTypeIds() || [])
							.field('position', data.position() || 99);
					
						updates.push(updateModel.data());
					});
					
					productAPI.updateMediaAssets(pVM.id, updates).then(function() {
						toolbar.hide();
					});
				}
			}
		},
		//---------------------------------------------------------------------------------
		// Removes image in backend and from observable array.
		//---------------------------------------------------------------------------------
		removeMediaAsset : function(mediaVM, event) {
			var self = this;
			
			productAPI.removeMediaAsset(mediaVM.id, mediaVM.productId).then(function (data) {
				var pVM = _.findWhere(self.products(), { id: mediaVM.productId });
				var mVM = _.findWhere(pVM.media(), { id: mediaVM.id });

				pVM.media.remove(mVM);
			});
		},
		//---------------------------------------------------------------------------------
		// Uploads image to REST service after it has been selected by user.
		//---------------------------------------------------------------------------------
		addMediaAsset : function(productId, file, dz) {
			var self = this;
			
			// addMediaAsset() seems to be called again after the field has been cleared,
			// so we make sure that a file has actually been selected.
			if(!_.isUndefined(productId) && !_.isUndefined(file)) {
				var fd = new FormData();
				fd.append( 'file', file );
				
				// Upload the image and add the returned created object to the observable array.
				productAPI.uploadMediaAsset(productId, fd).then(function (mediaAsset) {
					var pVM = _.findWhere(self.products(), { id: '' + productId });
					var mediaVM = new MediaVM(mediaAsset.id, mediaAsset.productId, mediaAsset.firstStoreId, mediaAsset.path, mediaAsset.webPath, mediaAsset.webThumbnailPath, mediaAsset.previewImagePath, mediaAsset.previewImageWebPath, mediaAsset.previewImageWebThumbnailPath, mediaAsset.title, mediaAsset.position, mediaAsset.mimeType, mediaAsset.mediaTypeIds, mediaAsset.variantDefault, mediaAsset.bundleDefault, mediaAsset.programmeDefault, mediaAsset.enabled);

					pVM.media.push(mediaVM);
					
					// Clear the selected image in preview field and input.
					dz.removeFile(file);
				});
			}
		},
		//---------------------------------------------------------------------------------
		// Gets images from backend and re-populates the observable images array.
		//---------------------------------------------------------------------------------
		refreshMediaAssets : function(productId) {
			var self = this;

			// Remove old values and re-populate with values freshly obtained from DB.
			var pVM = _.findWhere(self.products(), { id: '' + productId });
			var media = pVM.media;
			
			media.removeAll();
			
			productAPI.getMediaAssets(productId,  ['text/*', 'application/*']).then(function(data) {
				_.each(data.data.catalogMediaAssets, function(mediaAsset) {
					if(mediaAsset.productId == productId) {
						var mediaVM = new MediaVM(mediaAsset.id, mediaAsset.productId, mediaAsset.firstStoreId, mediaAsset.path, mediaAsset.webPath, mediaAsset.webThumbnailPath, mediaAsset.previewImagePath, mediaAsset.previewImageWebPath, mediaAsset.previewImageWebThumbnailPath, mediaAsset.title, mediaAsset.position, mediaAsset.mimeType, mediaAsset.mediaTypeIds, mediaAsset.variantDefault, mediaAsset.bundleDefault, mediaAsset.programmeDefault, mediaAsset.enabled);
						media.push(mediaVM);
					}
				});
			});
		},
		//---------------------------------------------------------------------------------
		// Update image positions. Called by jquery-sortable after media-asset has been dropped.
		//---------------------------------------------------------------------------------
		updatePositions : function(productId, domMediaAssetList) {
			var self = this;
			
			var mediaAssetPositions = {};
			
			domMediaAssetList.each(function(index, elem) {
                var item = $(elem),
	                pos = item.index()+1,
	                id = $(item).attr('data-id');

                mediaAssetPositions[id] = pos;
			});
			
			productAPI.updateMediaAssetPositions(productId, mediaAssetPositions).then(function() {
				self.refreshMediaAssets(productId);
			});
		},
		// ---------------------------------------------
		// Durandal callback.
		// ---------------------------------------------
		activate : function(id) {
			var self = this;
			
			self.productId = id;
			
			var mediaTypes = gc.app.dataGet('catalogMediaTypes');
			self.catalogMediaTypes(mediaTypes);
			
			if(!_.isEmpty(mediaTypes)) {
				self.mediaTypeOptions.push( { id : '', text : function() {
						return gc.app.i18n('app:modules.product.mediaTypeSelectTitle', {}, gc.app.currentLang);
					}
				});
				_.forEach(mediaTypes, function(mediaType) {
					self.mediaTypeOptions.push( { id : mediaType.id, text : mediaType.label.i18n } );
				});
			}
		},
		// ---------------------------------------------
		// Durandal callback.
		// ---------------------------------------------
		attached : function() {
			var self = this;
			
			Dropzone.autoDiscover = false;
			
			$(document).on('click', '#tab-prd-details-documents', function() {

				$('#header-store-pills').show();

				// Reset array before pushing new results into it.
				self.products([]);

				//---------------------------------------------------------------------------
				// First get the actual product to make sure that it exists.
				//---------------------------------------------------------------------------
				return productAPI.getProduct(self.productId).then(function(product) {
				
					// Append the attribute meta-data as we only have the attributeId at this point.
					gc.attributes.appendAttributes(product);
				
					self.articleNumber = gc.attributes.find(product.attributes, 'article_number').value;
					self.name = gc.attributes.find(product.attributes, 'name').value;
					self.name2 = gc.attributes.find(product.attributes, 'name2').value;
					
					var variantAttributes = productAPI.getVariantAttributes(product.attributes);
					var variantValues = undefined;
					
					if(!_.isEmpty(variantAttributes)) {
						variantValues = gc.attributes.flattenedValues(variantAttributes);
					}
					
					self.products.push(new ProductVM(self.productId, self.articleNumber, self.name, self.name2, variantValues));
				//---------------------------------------------------------------------------
				// Then get all the variants, so that we can group the images accordingly.
				//---------------------------------------------------------------------------
				}).then(function(data) {
					productAPI.getVariants(self.productId).then(function(data) {
						var products = data.data.products;
					
						// Append the attribute meta-data as we only have the attributeId at this point.
						gc.attributes.appendAttributes(products);
					
						_.each(products, function(product) {
							var variantAttributes = productAPI.getVariantAttributes(product.attributes);
							var variantValues = gc.attributes.flattenedValues(variantAttributes);

							self.products.push(new ProductVM(product.id, gc.attributes.find(product.attributes, 'article_number').value, gc.attributes.find(product.attributes, 'name').value, gc.attributes.find(product.attributes, 'name2').value, variantValues));
						});
					//---------------------------------------------------------------------------
					// Then get all of the media assets and add them to the matching products.
					//---------------------------------------------------------------------------
					}).then(function(data) {
						productAPI.getMediaAssets(self.productId,  ['text/*', 'application/*']).then(function(data) {
							_.each(data.data.catalogMediaAssets, function(mediaAsset) {
								var pVM = _.findWhere(self.products(), {id: mediaAsset.productId});
								var mediaVM = new MediaVM(mediaAsset.id, mediaAsset.productId, mediaAsset.firstStoreId, mediaAsset.path, mediaAsset.webPath, mediaAsset.webThumbnailPath, mediaAsset.previewImagePath, mediaAsset.previewImageWebPath, mediaAsset.previewImageWebThumbnailPath, mediaAsset.title, mediaAsset.position, mediaAsset.mimeType, mediaAsset.mediaTypeIds, mediaAsset.variantDefault, mediaAsset.bundleDefault, mediaAsset.programmeDefault, mediaAsset.enabled);
								pVM.media.push(mediaVM);
							});
						});
					//---------------------------------------------------------------------------
					// Finally make the media-asset lists sortable.
					//---------------------------------------------------------------------------
					}).then(function(data) {
						$('.sortableMediaAssets').sortable({
				            update: function() {
				            	var productId = $(this).data('productid');
				            	self.updatePositions(productId, $(this).children('tr'));
				            }
				        });
						
						/* Dropzone magic. Automatically uploads file and adds the saved entry to list of media-assets in productVM. */
						$('.dropzone-media-documents').each(function( index, el ) {
							$form = $(this).get(0);
							var productId = $(this).data('productid');
							var dz = new Dropzone($form, { url: '/api/v1/products/' + productId + '/media-assets/'});
							
/*							
							dz.on("sending", function(file, xhr, formData) {
								var activeStore = gc.app.activeStore();
								
								if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
									xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
								}
							});
*/	
							
							dz.on("success", function(file, mediaAsset) {
								var pVM = _.findWhere(self.products(), { id: '' + productId });
								var mediaVM = new MediaVM(mediaAsset.id, mediaAsset.productId, mediaAsset.firstStoreId, mediaAsset.path, mediaAsset.webPath, mediaAsset.webThumbnailPath, mediaAsset.previewImagePath, mediaAsset.previewImageWebPath, mediaAsset.previewImageWebThumbnailPath, mediaAsset.title, mediaAsset.position, mediaAsset.mimeType, mediaAsset.mediaTypeIds, mediaAsset.variantDefault, mediaAsset.bundleDefault, mediaAsset.programmeDefault, mediaAsset.enabled);

								// Add new db-entry to product-media-list.
								pVM.media.push(mediaVM);
								// Remove file from preview.
								dz.removeFile(file);
							});
						});
					});
				});
			});
		},
		detached : function() {
			var self = this;
			$(document).off('click', '#tab-prd-details-documents');
		}
	}
	
	return ProductMediaController;
});