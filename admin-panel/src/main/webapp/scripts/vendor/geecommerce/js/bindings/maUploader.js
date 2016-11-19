define(['knockout', 'gc/gc'], function (ko, gc) {

    return {
        init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
            var $element = $(element);
            valueAccessor = valueAccessor() || {};

            var path = valueAccessor.path

            var dz = new Dropzone($element[0], { url: '/api/v1/media-assets/system/?path=' + encodeURIComponent(path)});

            dz.on("sending", function(file, xhr, formData) {
  /*              var activeStore = gc.app.activeStore();

                if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                    xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                }*/
            });

            dz.on("success", function(file, data) {
                var mediaAssetId = data.data.mediaAsset.id;

                valueAccessor.value(mediaAssetId);
                // Remove file from preview.
                dz.removeFile(file);
            });

/*
            $.extend(opts, {
                acceptedFiles: 'image/!*',
                addRemoveLinks: true,
                init: dropzoneInit
            });

            $(el).dropzone(opts);*/
        }
    };
});