define(['knockout', 'gc/gc', 'gc-media-asset'], function (ko, gc, mediaAssetAPI) {

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
            //value(valueUnwrapped);

            if (!_.isUndefined(valueUnwrapped) && !_.isEmpty(valueUnwrapped)) {
                var mediaAssetId = valueUnwrapped;
                mediaAssetAPI.getMediaAsset(mediaAssetId).then(function (mediaAsset) {
                    console.log("LOAD MEDIA ASSET");
                    if (!_.isUndefined(mediaAsset)) {
                        setElementData(mediaAsset);
                    }
                }, function(reason) {
                    clearElementData();
                }) ;
            } else {
                clearElementData();
            }

            function setElementData(mediaAsset) {
                clearElementData();
                var ctxName = gc.ctxobj.val(mediaAsset.name, gc.app.currentUserLang(), "any");

                if(mediaAsset.mimeType.indexOf("image") == 0){
                    //insert image view
                    $element.append("<a href='" + mediaAsset.url + "' target='_blank'>" +
                                    "<img src='" + mediaAsset.url + "' class='img-responsive'/>" +
                                    "</a>");
                } else {
                    //insert file view
                    $element.append("<a href='" + mediaAsset.url + "' target='_blank'>" +
                        ctxName +
                        "</a>");

                }
            }

            function clearElementData() {
                $element.html("");
            }
        }
    };
});