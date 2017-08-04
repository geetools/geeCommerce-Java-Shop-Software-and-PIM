define(['knockout', 'gc/gc', 'gc/pager', 'gc-product'], function (ko, gc, pager, productAPI) {

    return {
        init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {

            var $element = $(element);
            var value = valueAccessor();
            var allBindings = allBindings();
            var options = allBindings.options || {};

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
                }, function(reason) {
                    clearElementData();
                }) ;
            } else {
                clearElementData();
            }

            function setElementData(product, webThumbnailPath) {
                var name = gc.attributes.find(product.attributes, "name");
                var number = gc.attributes.find(product.attributes, "article_number");

                var nameVal = gc.ctxobj.val(name.value, gc.app.currentUserLang(), "closest");
                var numberVal = gc.ctxobj.val(number.value, gc.app.currentUserLang(), "closest");

                $element.find("div.product-viewer-img").html("<img src='"+ webThumbnailPath + "'>");
                $element.find("div.product-viewer-name").html("<span>" + nameVal+ "</span>");
            }

            function clearElementData() {
                $element.find("div.product-viewer-img").html("");
                $element.find("div.product-viewer-name").html("");
            }
        }
    };
});