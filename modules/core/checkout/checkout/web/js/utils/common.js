define(['gc/gc'], function (gc) {

    return {
        test: function () {
            var self = this;
        },
        getURLParam: function (param) {
            var reParam = new RegExp('(?:[\?&]|&)' + param + '=([^&]+)', 'i');
            var match = window.location.search.match(reParam);
            return ( match && match.length > 1 ) ? match[1] : null;
        }
    };
});