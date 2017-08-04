define(['knockout', 'gc/gc', 'gc/pager', 'gc-product'], function (ko, gc, pager, productAPI) {

    return {
        init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {

            var $element = $(element);
            var value = valueAccessor();
            var allBindings = allBindings();
            var pickerOptions = allBindings.pickerOptions || {};

            if(!$element.html()){
                $element.append('<div class="product-selector-empty">'+
                    '<p class="product-selector-message" data-i18n="app:modules.slide-show.productSelectorHint">'+
                    'Click here to select a product'+
                    '</p>'+
                    '</div>'+
                    '<div class="product-selector-selected" hidden>'+
                    '<div class="col-sm-3 product-img"></div>'+
                    '<div class="col-sm-5 product-name" style="padding-top: 20px; font-weight: bold"></div>'+
                    '<div class="col-sm-3 product-change" style="padding-top: 13px">'+
                    '<button type="button" class="btn btn-default product-change-btn">Change</button>'+
                    '</div>'+
                    '</div>'+
                    '<div class="product-picker" hidden>'+
                    '<div class="col-sm-8 col-xs-8">'+
                    '<input class="form-control product-picker-input" type="text"  style="float: left">'+
                    '</div>'+
                    '<div class="col-sm-4 col-xs-4 product-picker-cancel">'+
                    '<button type="button" class="btn btn-default picker-cancel-btn">Cancel</button>'+
                    '</div>'+
                    '<div class="product-picker-message"></div>'+
                    '</div>')
            }


            var searchProductResults = {};

            var valueUnwrapped = ko.unwrap(value);
            value(valueUnwrapped);

            var productEmpty = $(element).find("div.product-selector-empty");
            var productSelected = $(element).find("div.product-selector-selected");
            var productPicker = $(element).find("div.product-picker");
            var productPickerInput = $(element).find("input.product-picker-input");

            if (!_.isUndefined(valueUnwrapped) && !_.isEmpty(valueUnwrapped)) {
                productEmpty.prop("hidden", true);
                productSelected.removeAttr("hidden");
            }

            productEmpty.on("click", function () {
                toggleProductPicker();
                toggleElement(productEmpty);
            });

            $(element).find("button.product-change-btn").on("click", function () {
                toggleProductPicker();
                toggleElement(productSelected);
            });

            // cancel and close the input field for picker
            $(element).find("button.picker-cancel-btn").on("click", function () {
                value.valueHasMutated();
            });

            function toggleElement(element) {
                if (element.prop("hidden"))
                    element.removeAttr("hidden");
                else
                    element.prop("hidden", true);
            }

            function toggleProductPicker() {
                if (productPicker.prop("hidden")) {
                    productPicker.removeAttr("hidden");
                    productPickerInput.val("");
                    showProductPickerMessage("");
                } else {
                    productPicker.prop("hidden", true);
                }
            }

            function showProductPickerMessage(msg) {
                $(element).find("div.product-picker-message").html(msg);
            }

            // -----------------------------------------------------------
            // Create the combobox
            // -----------------------------------------------------------
            var inputElement = $(element).find("input.product-picker-input");
            inputElement.autocomplete({
                minLength: 3,
                focus: function (event, ui) {
                    return false;
                },
                select: function (event, ui) {
                    value(ui.item.value);
                    if (value() == ui.item.value) {
                        value.valueHasMutated();
                    }
                    return false;
                },
                source: function (request, response) {
                    var queryStr = inputElement.val();

                    var autocompleteSrc = [];

                    // Pager columns
                    var pagerColumns = [
                        {'name': '$attr.article_number', 'label': 'Artikelnummer'},
                        {'name': '$attr.name', 'label': 'Name'}
                    ];

                    var pagingOptions = productAPI.pagingOptions({columns: pagerColumns, filter: [], attributes: []})
                    pagingOptions.fields.push('attributeOptions');
                    pagingOptions.fields.push('properties');
                    pagingOptions.fields.push('sortOrder');

                    // Init the pager.
                    searchProductResults = new gc.Pager(pagingOptions);

                    searchProductResults.columnValue('$attr.article_number', undefined);
                    searchProductResults.columnValue('$attr.name', undefined);

                    searchProductResults.columnValue('$attr.article_number', queryStr);
                    searchProductResults.load().then(function (data) {
                        if (_.isEmpty(data.data.products)) {
                            searchProductResults.columnValue('$attr.article_number', undefined);
                            searchProductResults.columnValue('$attr.name', queryStr);
                            searchProductResults.load().then(function (data2) {
                                if (!_.isEmpty(data2.data.products)) {
                                    //console.log("PP:: Products found by name::" + data2.data.products.length);

                                    var productIds = [];
                                    _.each(data2.data.products, function (product) {
                                        productIds.push(product.id);
                                    });

                                    productAPI.getImageMediaAssets(productIds).then(function (mediaAssets) {
                                        var assetsMap = mediaAssets.data.results;
                                        _.each(data2.data.products, function (product) {
                                            var assets = assetsMap[product.id];
                                            if (typeof assets !== 'undefined' && assets.length > 0) {
                                                addSourceItem(product, assets[0].webThumbnailPath);
                                            } else {
                                                addSourceItem(product, null);
                                            }
                                        });

                                        response(autocompleteSrc);
                                    });

                                } else {
                                    showProductPickerMessage("No product found");
                                }
                            });
                        } else {
                            //console.log("PP:: Products found by article number::" + data.data.products.length);

                            var productIds = [];
                            _.each(data.data.products, function (product) {
                                productIds.push(product.id);
                            });

                            productAPI.getImageMediaAssets(productIds).then(function (mediaAssets) {
                                var assetsMap = mediaAssets.data.results;
                                _.each(data.data.products, function (product) {
                                    var assets = assetsMap[product.id];
                                    if (typeof assets !== 'undefined' && assets.length > 0) {
                                        addSourceItem(product, assets[0].webThumbnailPath);
                                    } else {
                                        addSourceItem(product, null);
                                    }
                                });

                                response(autocompleteSrc);
                            });

                        }
                    });

                    function addSourceItem(product, webThumbnailPath) {
                        var name = gc.attributes.find(product.attributes, "name");
                        var number = gc.attributes.find(product.attributes, "article_number");

                        var nameVal = gc.ctxobj.val(name.value, gc.app.currentUserLang(), "closest");
                        var numberVal = gc.ctxobj.val(number.value, gc.app.currentUserLang(), "closest");

                        var formatedVal = numberVal;
                        if (!_.isUndefined(pickerOptions)) {
                            if (pickerOptions.format == "id") {
                                formatedVal = product.id;
                            }
                        }

                        //console.log("Source item: " +  "imgsrc:" + webThumbnailPath + " label:" + nameVal + " - " + numberVal + " (" + product.id + ")" + " value:" + formatedVal);
                        autocompleteSrc.push({
                            imgsrc: webThumbnailPath,
                            label: nameVal + " - " + numberVal,
                            value: formatedVal
                        });
                    }
                }

            }).autocomplete('instance')._renderItem = function (ul, item) {
                var imgBlock = "";
                if (item.imgsrc)
                    imgBlock = "<img src='" + item.imgsrc + "' />";

                return $("<li>")
                    .attr("data-value", item.value)
                    .append("<a style='padding: 5px'>" + imgBlock + "<span style='padding: 5px;'>" + item.label + "</span></a>")
                    .appendTo(ul);
            };


            inputElement.val(value());

        },
        update: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
            var $element = $(element);
            var value = valueAccessor();
            var options = allBindings.options || {};

            var valueUnwrapped = ko.unwrap(value);
            value(valueUnwrapped);

            if (!_.isUndefined(valueUnwrapped) && !_.isEmpty(valueUnwrapped)) {
                var productId = valueUnwrapped;
                productAPI.getProduct(productId).then(function (product) {
                    if (!_.isUndefined(product)) {
                        var mimeTypes = ['image/*', 'video/*'];
                        productAPI.getMediaAssets(product.id, mimeTypes).then(function (mediaAssets) {
                            var assets = mediaAssets.data.catalogMediaAssets;
                            setElementData(product, assets[0].webThumbnailPath);
                        });
                    }
                }, function (reason) {
                    showProductPicker(false);
                });
            } else {
                showProductViewerElement(false);
                showProductPicker(false);
            }

            function setElementData(product, webThumbnailPath) {
                var name = gc.attributes.find(product.attributes, "name");
                var number = gc.attributes.find(product.attributes, "article_number");

                var nameVal = gc.ctxobj.val(name.value, gc.app.currentUserLang(), "closest");
                var numberVal = gc.ctxobj.val(number.value, gc.app.currentUserLang(), "closest");

                $element.find("div.product-img").html("<img src='" + webThumbnailPath + "'>");
                $element.find("div.product-name").html("<span>" + nameVal + "(" + numberVal + ")" + "</span>");

                showProductViewerElement(true);
                showProductPicker(false);
            }

            function showProductViewerElement(show) {
                if (show) {
                    $(element).find("div.product-selector-empty").prop("hidden", true);
                    $(element).find("div.product-selector-selected").removeAttr("hidden");
                } else {
                    $(element).find("div.product-img").html("");
                    $(element).find("div.product-name").html("");
                    $(element).find("div.product-selector-empty").removeAttr("hidden");
                    $(element).find("div.product-selector-selected").prop("hidden", true);
                }
            }

            function showProductPicker(show) {
                if (show) {
                    $(element).find("div.product-picker").removeAttr("hidden");
                } else {
                    $(element).find("div.product-picker").prop("hidden", true);
                }

                $(element).find("input.product-picker-input").val("");
            }
        }
    };
});
