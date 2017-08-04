define(['jquery', 'bootstrap', 'gc/gc', 'catalog/api', 'catalog/utils/media'], function ($, Bootstrap, gc, catalogAPI, mediaUtil) {

	return {
		// -----------------------------------------------------------------------------
		// Find variant product by selected options.
		// -----------------------------------------------------------------------------
		findVariant : function(variantVM, variantProducts) {
			var foundVariant;
			var selectedOptions = _.values(variantVM.selectedOptions);
			
			if(_.isEmpty(selectedOptions)) {
				return undefined;
			}
			
			_.each(variantProducts, function(variantProduct) {
				var diff = _.difference(variantProduct.options, selectedOptions);
				
				if(diff.length == 0 && variantProduct.options.length == selectedOptions.length) {
					foundVariant = variantProduct;
				}
			});

			return foundVariant;
		},
		// -----------------------------------------------------------------------------
		// Find variant product by variant product-id.
		// -----------------------------------------------------------------------------
		findVariantById : function(variantProductId, variantProducts) {
			var foundVariant;
			
			if(_.isEmpty(variantProductId)) {
				return undefined;
			}
			
			_.each(variantProducts, function(variantProduct) {
				if(variantProduct.id == variantProductId) {
					foundVariant = variantProduct;
				}
			});

			return foundVariant;
		},
		// -----------------------------------------------------------------------------
		// Attempt to find matching option elements for selected variant.
		// -----------------------------------------------------------------------------
		getPreselectedOptionElements : function(variantProduct, variantOptions) {
		
			var foundElements = [];
		
			_.each(variantProduct.options, function(option) {
				_.each(variantOptions, function(variantOption) {
					var foundOption = _.findWhere(variantOption.options, {id: option});
					
					if(!_.isUndefined(foundOption)) {
						foundElements.push($('#wd_option_' + variantOption.attribute_code + '_' + option));
					}
				});
			});

			return foundElements;
		},
		// -----------------------------------------------------------------------------
		// Mark currently clicked element as "selected".
		// -----------------------------------------------------------------------------
		highlightSelectedOption : function(element) {
			if(_.isUndefined(element)) {
				return;
			} else if(_.isArray(element)) {
				_.each(element, function($el) {
					$el.closest('ul').find('li>a').removeClass('selected');
					$el.addClass('selected');
				});
			} else {
				$el = $(element);
				if(!_.isUndefined($el)) {
					$el.closest('ul').find('li>a').removeClass('selected');
					$el.addClass('selected');
				}
			}
		},
		// -----------------------------------------------------------------------------
		// Get preselected variant product-id from URI.
		// -----------------------------------------------------------------------------
		getPreselectedVariantFromURI : function() {
			var hash = window.location.hash;
			
			if(!_.isUndefined(hash)) {
				return hash.substr(1);
			}
		},
		// -----------------------------------------------------------------------------
		// Set variant product-id in URI-hash.
		// -----------------------------------------------------------------------------
		setPreselectedVariantInURI : function(productVariantId) {
			window.location.hash = '#' + productVariantId;		
		},
		// -----------------------------------------------------------------------------
		// Set the label of the currently selected option.
		// -----------------------------------------------------------------------------
		setSelectedOptionLabel : function(element, optionLabel) {
			$(element).closest('div.wd-variant-options').prev('div.wd-variant-attribute').children('span.wd-variant-selected-value').first().text(optionLabel);
		},
		// -----------------------------------------------------------------------------
		// Activates all options of the current attribute (where click took place).
		// -----------------------------------------------------------------------------
		activateOptions : function(element) {
			$(element).closest('ul').find('li>a').removeClass('disabled');
		},		
		// -----------------------------------------------------------------------------
		// Deactivates options that are not compatible with the currently selected one.
		// -----------------------------------------------------------------------------
		deactivateUnavailableOptions : function(element, variantVM, variantOptions) {
			var selectedAttrCode = $(element).data('attr');
			var selectedOptionId = $(element).data('option').toString();

			// Iterate though all options.
			$('.wd-variant-options>ul>li>a').each(function(index) {
				var attrCode = $(this).data('attr');
				var optionId = $(this).data('option').toString();

				// No need to check the element that was clicked. We need to check the 
				// compatibility of the "other" ones.
				if(attrCode != selectedAttrCode) {
					var variantAttr = _.find(variantOptions, {attribute_code : attrCode});
					var variantOption = _.find(variantAttr.options, {id : optionId});

					// Mark as disabled if clicked option can not be found in the
					// group of options of this option.
					if(!_.contains(variantOption.inGroupWithOptions, selectedOptionId)) {
						$(this).addClass('disabled');
					} else {
						$(this).removeClass('disabled');
					}
				}
			});
		},
		// -----------------------------------------------------------------------------
		// Returns the variant image URLs.
		// -----------------------------------------------------------------------------
		getVariantImages : function(variantProduct) {
			var variantImages = [];
			var idx = 0;
		
			if(!_.isEmpty(variantProduct.gallery)) {
				_.each(variantProduct.gallery, function(image) {
					variantImages.push({
						origImage: image.path, 
						largeImage: mediaUtil.buildImageURL(image.path, 330, 330), 
						thumbnail: mediaUtil.buildImageURL(image.path, 50, 50), 
						zoomImage: mediaUtil.buildImageURL(image.path, 1024, 1024), 
						index: idx++
					});
				});
			}
		
			return variantImages;
		}
	};
});