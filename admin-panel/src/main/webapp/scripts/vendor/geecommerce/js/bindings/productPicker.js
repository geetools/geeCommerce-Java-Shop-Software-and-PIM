define(['knockout', 'gc/gc', 'gc/pager', 'gc-product'], function (ko, gc, pager, productAPI) {

    return {
        init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {

            var $element = $(element);
            var value = valueAccessor();
            var allBindings = allBindings();
            var pickerOptions = allBindings.pickerOptions || {};

            var searchProductResults = {};

            // -----------------------------------------------------------
            // Create the combobox
            // -----------------------------------------------------------
            $element.autocomplete({
                minLength: 3,
                focus: function (event, ui) {
                    return false;
                },
                select: function (event, ui) {
                    value(ui.item.value);
                    return false;
                },
                source: function (request, response) {
                    console.log("PP :: searching for ::" + queryStr);
                    var queryStr = $element.val();

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
                            console.log("PP:: SEARCHING FOR ARTICLE NUMBER with NO results.");

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
                                    console.log("PP:: NO SEARCHING RESULTS FOUND...");
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
                        autocompleteSrc.push({imgsrc: webThumbnailPath, label: nameVal + " - " + numberVal + " (" + product.id + ")", value: formatedVal});
                    }
                }

            }).autocomplete('instance')._renderItem = function (ul, item) {
                var imgBlock= "";
                if (item.imgsrc)
                    imgBlock = "<img src='" + item.imgsrc + "' />";

                return $("<li>")
                    .attr("data-value", item.value)
                    .append("<a style='padding: 5px'>" + imgBlock + "<span style='padding: 5px;'>" + item.label + "</span></a>")
                    .appendTo(ul);
            };

            // $element.change(function () {
            //     var newValue = $element.val();
            //     if (_.isUndefined(newValue))
            //         newValue = '';
            //
            //     value(newValue);
            // });


            $element.val(value()) //TODO: replace temp fix
        },
        update: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
            var value = valueAccessor();
            var valueUnwrapped = ko.unwrap(value);
            value(valueUnwrapped);
        }
    };
});
