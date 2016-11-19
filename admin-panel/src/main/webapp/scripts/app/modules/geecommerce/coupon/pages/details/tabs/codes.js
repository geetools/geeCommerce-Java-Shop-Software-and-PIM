define(['durandal/app', 'knockout', 'gc/gc', 'gc-coupon'], function (app, ko, gc, couponAPI) {

    return {
        app: gc.app,
        pager: {},
        couponVM: {},
        qty: ko.observable(''),
        mark: ko.observable(true),
        exportMarkedAndUsed: ko.observable(false),
        exportUrl: {},
        activate: function(data) {
            var self = this;

            self.couponVM = gc.app.sessionGet('couponVM');
            self.exportUrl =  ko.computed(function() {
                var url = '/api/v1/coupons/' + self.couponVM.id() + '/export/codes';
                var params = '';
                if(self.mark()){
                    params = 'mark=true'
                }
                if(self.exportMarkedAndUsed()){
                    if(params !== '' ){
                        params += '&';
                    }
                    params += 'exportMarkedAndUsed=true'
                }
                if(self.qty() && self.qty() !== ''){
                    if(params !== '' ){
                        params += '&';
                    }
                    params += 'qty=' + self.qty();
                }
                if(params !== '')
                url += '?' + params;
                return url;
            }, self);
            // Pager columns
            var pagerColumns = [
                {'name' : 'code', 'label' : 'Code'}
            ];

            // Init the pager.
            this.pager = new gc.Pager(couponAPI.codesPagingOptions(data, {columns : pagerColumns}));

            if(!self.couponVM.isNew()) {


                // We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
                return this.pager.load();
            }
        }
    }
});