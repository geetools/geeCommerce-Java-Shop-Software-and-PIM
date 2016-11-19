define(function (require) {
    var app = require('durandal/app');

    return {
    	pageTitle: 'Dashboard',
    	pageDescription: 'Overview and latest statistics',
        showMessage: function () {
            app.showMessage('Dashboard!');
        }
    };
});