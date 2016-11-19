define(['jquery', 'bootstrap', 'gc/gc', 'catalog/api', 'customer-review/api', 'customer-review/utils/common', 'catalog/utils/media', 'page/media', 'page/variants', 'jquery-magnific-popup', 'jquery-slick'], function ($, Bootstrap, gc, catalogAPI, customerReviewAPI, customerReviewUtil, mediaUtil, pageMedia, pageVariants) {

	function ProductVM() {
		var self = this;
		
		self.id = gc.app.pageInfo('id');
		self.mainImage = {};
		self.galleryImages = [];
	}

	function VariantVM() {
		var self = this;
		
		self.selectedOptions = {};
		
		self.setOption = function(attrCode, optionId) {
			self.selectedOptions[attrCode] = optionId.toString();
		}
	}

	var productVM = new ProductVM();
	var variantVM = new VariantVM();

	// ---------------------------------------------------------------
	// Load add to cart and price fragment.
	// ---------------------------------------------------------------

    console.log('----------------------------->>>1 ', productVM.id);
	
	
	gc.app.fragment('/catalog/product/price-container/' + productVM.id, '#prd-cart-box', function(element) {
	
		// ---------------------------------------------------------------
		// Set up variants once price container has loaded.
		// ---------------------------------------------------------------
		catalogAPI.getVariants(productVM.id).then(function(response) {

			if(_.isEmpty(response.data) || _.isEmpty(response.data.results))
				return;

			var variantOptions = response.data.results.variant_options;
			var variantProducts = response.data.results.variant_products;

			if(_.isEmpty(variantOptions) || _.isEmpty(variantProducts))
				return;

			gc.app.render({ template: 'templates/catalog/product/variants.html', data: { variants: variantOptions },
				target: '.prd-variants' },
				function(data) {

				var targetEL = data.target;

				// --------------------------------------------------------------------
				// See if there is a preselected variant product-id in the URI-hash
				// and if there is highlight the options and show appropriate images.
				// --------------------------------------------------------------------
				var preselectedVariantId = pageVariants.getPreselectedVariantFromURI();

				console.log('preselectedVariantId::: ', preselectedVariantId);

				if(preselectedVariantId) {
					var preselectedVariant = pageVariants.findVariantById(preselectedVariantId, variantProducts);
					var preselectedOptionElements = pageVariants.getPreselectedOptionElements(preselectedVariant, variantOptions);

					console.log('!!preselectedVariant!! ', preselectedOptionElements);

					if(!_.isEmpty(preselectedOptionElements)) {
						_.each(preselectedOptionElements, function($el) {
							var attrCode = $el.data('attr');
							var optionId = $el.data('option');

							variantVM.setOption(attrCode, optionId);
						});

						_.each(preselectedOptionElements, function($el) {
							var optionLabel = $el.data('label');
							pageVariants.setSelectedOptionLabel($el, optionLabel);

							pageVariants.deactivateUnavailableOptions($el, variantVM, variantOptions);
						});

						pageVariants.highlightSelectedOption(preselectedOptionElements);
					}
				}

				// --------------------------------------------------------------------
				// Handle highlighting and images when user clicks on an option
				// and attempt to find a matching product variant.
				// --------------------------------------------------------------------
				$('.variant-options a').on('click', function() {

					// Don't do anything if option is disabled.
					if($(this).hasClass('disabled')) {
						return false;
					}

					var attrCode = $(this).data('attr');
					var optionId = $(this).data('option');
					var optionLabel = $(this).data('label');

					pageVariants.highlightSelectedOption($(this));

					pageVariants.deactivateUnavailableOptions($(this), variantVM, variantOptions);

					pageVariants.setSelectedOptionLabel($(this), optionLabel);

					variantVM.setOption(attrCode, optionId);

					var selectedProductVariant = pageVariants.findVariant(variantVM, variantProducts);

					// Tell cart form which variant has been selected.
					if(!_.isUndefined(selectedProductVariant) && !_.isUndefined(selectedProductVariant.id)) {
						$('#prd-cart-form-product-id').val(selectedProductVariant.id);
						$('.prd-cart-btn button').removeAttr("disabled");
						$('.prd-cart-btn button').removeClass("disabled");
						pageVariants.setPreselectedVariantInURI(selectedProductVariant.id);

						var variantImages = pageVariants.getVariantImages(selectedProductVariant);

						pageMedia.moveToImage(variantImages[0].origImage);

					} else {
						$('.prd-cart-btn button').addClass("disabled");
					}
				});
			});
		});
	});

	// ---------------------------------------------------------------
	// Set up images.
	// ---------------------------------------------------------------
	
	catalogAPI.getEnabledViewImages(productVM.id).then(function(response) {
		console.log('images', response.data.catalogMediaAssets);
		
		var _mainImage = _.findWhere(response.data.catalogMediaAssets, {productMainImage: true})
		var _galleryImages = _.where(response.data.catalogMediaAssets, {productGalleryImage: true})

		console.log('mainImage', _mainImage);
		console.log('galleryImages', _galleryImages);

		productVM.mainImage = {
			origImage: _mainImage.path,
			largeImage: _mainImage.webDetailPath ? _mainImage.webDetailPath : mediaUtil.buildImageURL(_mainImage.path, 330, 330), 
			thumbnail: _mainImage.webThumbnailPath ? _mainImage.webThumbnailPath : mediaUtil.buildImageURL(_mainImage.path, 60, 60), 
			zoomImage: _mainImage.webZoomPath ? _mainImage.webZoomPath : mediaUtil.buildImageURL(_mainImage.path, 1024, 1024), 
			index: 0
		};
		
		var idx = 1;
		_.each(_galleryImages, function(image) {
			productVM.galleryImages.push({
				origImage: image.path,
				largeImage: image.webDetailPath ? image.webDetailPath : mediaUtil.buildImageURL(image.path, 330, 330), 
				thumbnail: image.webThumbnailPath ? image.webThumbnailPath : mediaUtil.buildImageURL(image.path, 50, 50), 
				zoomImage: image.webZoomPath ? image.webZoomPath : mediaUtil.buildImageURL(image.path, 1024, 1024), 
				index: idx++
			});
		});

		pageMedia.renderImages(productVM);
	});
	
	
	customerReviewUtil.initSummary();
	
	
});
