define(['jquery', 'bootstrap', 'gc/gc', 'catalog/api', 'catalog/utils/media', 'jquery-slick'],
    function ($, Bootstrap, gc, catalogAPI, mediaUtil) {

        return {
            init: function (widgetParams) {

                var self = this;
                var wdContainer = '#' + widgetParams.widgetId;

                function VariantVM() {
                    var self = this;

                    self.selectedOptions = {};
                    self.setOption = function (attrCode, optionId) {
                        self.selectedOptions[attrCode] = optionId.toString();
                    }
                }

                function ProductVM() {
                    var self = this;
                    self.id = widgetParams.productId;
                }

                var productVM = new ProductVM();
                var variantVM = new VariantVM();

                // ---------------------------------------------------------------
                // Set up variants once price container has loaded.
                // ---------------------------------------------------------------
                catalogAPI.getVariants(productVM.id).then(function (response) {

                    if (_.isEmpty(response.data) || _.isEmpty(response.data.results))
                        return;

                    var variantOptions = response.data.results.variant_options;
                    var variantProducts = response.data.results.variant_products;

                    if (_.isEmpty(variantOptions) || _.isEmpty(variantProducts))
                        return;

                    gc.app.render({
                            slice: 'gui-widgets/cms_product_variants/variants',
                            data: {variants: variantOptions},
                            process: true,
                            target: wdContainer
                        },
                        function (data) {

                            var targetEL = data.target;

                            // --------------------------------------------------------------------
                            // See if there is a preselected variant product-id in the URI-hash
                            // and if there is highlight the options and show appropriate images.
                            // --------------------------------------------------------------------
                            var preselectedVariantId = self.getPreselectedVariantFromURI();

                            console.log('preselectedVariantId::: ', preselectedVariantId);

                            // if(!preselectedVariantId){
                            //     preselectedVariantId = Object.keys(variantProducts)[0];
                            // }
                            if(!preselectedVariantId){
                                $('.prd-cart-btn button').addClass("disabled");
                            }

                            
                            if (preselectedVariantId) {
                                var preselectedVariant = self.findVariantById(preselectedVariantId, variantProducts);
                                var preselectedOptionElements = self.getPreselectedOptionElements(preselectedVariant, variantOptions, wdContainer);

                                console.log('!!preselectedVariant!! ', preselectedOptionElements);

                                if (!_.isEmpty(preselectedOptionElements)) {
                                    _.each(preselectedOptionElements, function ($el) {
                                        var attrCode = $el.data('attr');
                                        var optionId = $el.data('option');

                                        variantVM.setOption(attrCode, optionId);
                                    });

                                    _.each(preselectedOptionElements, function ($el) {
                                        var optionLabel = $el.data('label');
                                        self.setSelectedOptionLabel($el, optionLabel);

                                        self.deactivateUnavailableOptions($el, variantVM, variantOptions, wdContainer);
                                    });

                                    self.highlightSelectedOption(preselectedOptionElements);

                                    var selectedProductVariant = self.findVariant(variantVM, variantProducts);

                                    // Tell cart form which variant has been selected.
                                    if (!_.isUndefined(selectedProductVariant) && !_.isUndefined(selectedProductVariant.id)) {
                                        $('#prd-cart-form-product-id').val(selectedProductVariant.id);
                                        $('.prd-cart-btn button').removeAttr("disabled");
                                        $('.prd-cart-btn button').removeClass("disabled");
                                        self.setPreselectedVariantInURI(selectedProductVariant.id);


                                        $(wdContainer).find("#selected-prd-variant").val(selectedProductVariant.id);

                                        var variantImages = self.getVariantImages(selectedProductVariant);

                                        console.log(variantImages)

                                        // publish message to move to image
                                        gc.app.channel.publish('product.variants', {variantMasterId: productVM.id, origImage: variantImages[0].origImage});

                                    } else {
                                        $('.prd-cart-btn button').addClass("disabled");
                                    }
                                }
                            }

                            // --------------------------------------------------------------------
                            // Handle highlighting and images when user clicks on an option
                            // and attempt to find a matching product variant.
                            // --------------------------------------------------------------------
                            $(wdContainer + ' .wd-variant-options a').on('click', function () {

                                // Don't do anything if option is disabled.
                                if ($(this).hasClass('disabled')) {
                                    return false;
                                }

                                var attrCode = $(this).data('attr');
                                var optionId = $(this).data('option');
                                var optionLabel = $(this).data('label');

                                self.highlightSelectedOption($(this));

                                self.deactivateUnavailableOptions($(this), variantVM, variantOptions, wdContainer);

                                self.setSelectedOptionLabel($(this), optionLabel);

                                variantVM.setOption(attrCode, optionId);

                                var selectedProductVariant = self.findVariant(variantVM, variantProducts);

                                // Tell cart form which variant has been selected.
                                if (!_.isUndefined(selectedProductVariant) && !_.isUndefined(selectedProductVariant.id)) {
                                    $('#prd-cart-form-product-id').val(selectedProductVariant.id);
                                    $('.prd-cart-btn button').removeAttr("disabled");
                                    $('.prd-cart-btn button').removeClass("disabled");
                                    self.setPreselectedVariantInURI(selectedProductVariant.id);


                                    $(wdContainer).find("#selected-prd-variant").val(selectedProductVariant.id);

                                    var variantImages = self.getVariantImages(selectedProductVariant);

                                    console.log(variantImages)

                                    // publish message to move to image
                                    gc.app.channel.publish('product.variants', {variantMasterId: productVM.id, origImage: variantImages[0].origImage});

                                } else {
                                    $('.prd-cart-btn button').addClass("disabled");
                                }
                            });
                        });
                });
            },
            // -----------------------------------------------------------------------------
            // Find variant product by selected options.
            // -----------------------------------------------------------------------------
            findVariant: function (variantVM, variantProducts) {
                var foundVariant;
                var selectedOptions = _.values(variantVM.selectedOptions);

                if (_.isEmpty(selectedOptions)) {
                    return undefined;
                }

                _.each(variantProducts, function (variantProduct) {
                    var diff = _.difference(variantProduct.options, selectedOptions);

                    console.log('findVariant: ', diff.length, variantProduct.options, selectedOptions);

                    if (diff.length == 0 && variantProduct.options.length == selectedOptions.length) {
                        foundVariant = variantProduct;
                    }
                });

                return foundVariant;
            },
            // -----------------------------------------------------------------------------
            // Find variant product by variant product-id.
            // -----------------------------------------------------------------------------
            findVariantById: function (variantProductId, variantProducts) {
                var foundVariant;

                if (_.isEmpty(variantProductId)) {
                    return undefined;
                }

                _.each(variantProducts, function (variantProduct) {
                    if (variantProduct.id == variantProductId) {
                        foundVariant = variantProduct;
                    }
                });

                return foundVariant;
            },
            // -----------------------------------------------------------------------------
            // Attempt to find matching option elements for selected variant.
            // -----------------------------------------------------------------------------
            getPreselectedOptionElements: function (variantProduct, variantOptions, wdContainer) {

                var foundElements = [];

                if(variantProduct) {
                    _.each(variantProduct.options, function (option) {
                        _.each(variantOptions, function (variantOption) {
                            var foundOption = _.findWhere(variantOption.options, {id: option});

                            if (!_.isUndefined(foundOption)) {
                                foundElements.push($('#option_' + variantOption.attribute_code + '_' + option));
                            }
                        });
                    });
                }
                return foundElements;
            },
            // -----------------------------------------------------------------------------
            // Mark currently clicked element as "selected".
            // -----------------------------------------------------------------------------
            highlightSelectedOption: function (element) {
                if (_.isUndefined(element)) {
                    return;
                } else if (_.isArray(element)) {
                    _.each(element, function ($el) {
                        $el.closest('ul').find('li>a').removeClass('selected');
                        $el.addClass('selected');
                    });
                } else {
                    $el = $(element);
                    if (!_.isUndefined($el)) {
                        $el.closest('ul').find('li>a').removeClass('selected');
                        $el.addClass('selected');
                    }
                }
            },
            // -----------------------------------------------------------------------------
            // Get preselected variant product-id from URI.
            // -----------------------------------------------------------------------------
            getPreselectedVariantFromURI: function () {
                var hash = window.location.hash;

                if (!_.isUndefined(hash)) {
                    return hash.substr(1);
                }
            },
            // -----------------------------------------------------------------------------
            // Set variant product-id in URI-hash.
            // -----------------------------------------------------------------------------
            setPreselectedVariantInURI: function (productVariantId) {
                window.location.hash = '#' + productVariantId;
            },
            // -----------------------------------------------------------------------------
            // Set the label of the currently selected option.
            // -----------------------------------------------------------------------------
            setSelectedOptionLabel: function (element, optionLabel) {
                $(element).closest('div.wd-variant-options').prev('div.wd-variant-attribute').children('span.wd-variant-selected-value').first().text(optionLabel);
            },
            // -----------------------------------------------------------------------------
            // Activates all options of the current attribute (where click took place).
            // -----------------------------------------------------------------------------
            activateOptions: function (element) {
                $(element).closest('ul').find('li>a').removeClass('disabled');
            },
            // -----------------------------------------------------------------------------
            // Deactivates options that are not compatible with the currently selected one.
            // -----------------------------------------------------------------------------
            deactivateUnavailableOptions: function (element, variantVM, variantOptions, wdContainer) {
                var selectedAttrCode = $(element).data('attr');
                var selectedOptionId = $(element).data('option').toString();

                // Iterate though all options.
                $(wdContainer + ' .wd-variant-options>ul>li>a').each(function (index) {
                    var attrCode = $(this).data('attr');
                    var optionId = $(this).data('option').toString();

                    // No need to check the element that was clicked. We need to check the
                    // compatibility of the "other" ones.
                    if (attrCode != selectedAttrCode) {
                        var variantAttr = _.find(variantOptions, {attribute_code: attrCode});
                        var variantOption = _.find(variantAttr.options, {id: optionId});

                        // Mark as disabled if clicked option can not be found in the
                        // group of options of this option.
                        if (!_.contains(variantOption.inGroupWithOptions, selectedOptionId)) {
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
            getVariantImages: function (variantProduct) {
                var variantImages = [];
                var idx = 0;

                console.log(variantProduct)


                if (!_.isEmpty(variantProduct.gallery)) {
                    _.each(variantProduct.gallery, function (image) {
                        variantImages.push({
                            origImage: image.path,
                            largeImage: mediaUtil.buildImageURL(image.path, 330, 330),
                            thumbnail: mediaUtil.buildImageURL(image.path, 50, 50),
                            zoomImage: mediaUtil.buildImageURL(image.path, 1024, 1024),
                            index: idx++
                        });
                    });
                } else if(variantProduct.originalVariantImage) {
                    var image = variantProduct.originalVariantImage;
                    variantImages.push({
                        origImage: image.path,
                        largeImage: mediaUtil.buildImageURL(image.path, 330, 330),
                        thumbnail: mediaUtil.buildImageURL(image.path, 50, 50),
                        zoomImage: mediaUtil.buildImageURL(image.path, 1024, 1024),
                        index: idx++
                    });
                }

                return variantImages;
            }
        }
    });




