define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-slide-show', 'knockout-validation' ], function(app, ko, router, gc, slideShowAPI, validation) {

    function SlideShowVM(slideShowId) {
        var self = this;
        self.id = ko.observable(slideShowId);
        self.name = ko.observable();
        self.label = ko.observableArray();

        var d = new Date();
        d.setMonth( d.getMonth( ) + 1 );
        self.dateFrom = ko.observable(new Date().toISOString()).extend({ date: true });
        self.dateTo = ko.observable(d).extend({ date: true});
        self.enabled = ko.observableArray([]);

        self.isNew = ko.observable(false);

        if(slideShowId == 'new'){
            self.isNew(true);
        }

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function SlideShowBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof SlideShowBaseController)) {
            throw new TypeError("SlideShowBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.slideShowVM = ko.observable({});
        this.slideShowId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    SlideShowBaseController.prototype = {
        constructor : SlideShowBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.slide-show.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.product-list.detailsSubtitle',
        saveData : function(context) {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('name', self.slideShowVM.name());
            updateModel.field('label', self.slideShowVM.label(), true);
            updateModel.field('dateFrom', gc.utils.toServerTime(gc.utils.startOfTheDay(self.slideShowVM.dateFrom())));
            updateModel.field('dateTo', gc.utils.toServerTime(gc.utils.endOfTheDay(self.slideShowVM.dateTo())));
            updateModel.field('enabled', self.slideShowVM.enabled(), true);

            if(self.slideShowVM.isNew()) {
                slideShowAPI.createSlideShow(updateModel).then(function(data) {
                    router.navigate('//slide-shows/details/' + data.id);
                    context.saved();
                })
            } else {
                slideShowAPI.updateSlideShow(self.slideShowId(), updateModel).then(function(data) {
                    context.saved();
                })
            }
        },
        activate : function(data) {
            var self = this;
            self.slideShowId(data);
            var vm = new SlideShowVM(data);
            self.slideShowVM = vm;

            if(!vm.isNew()){
                return slideShowAPI.getSlideShow(self.slideShowId()).then(function(data) {
                    vm.name(data.name);
                    vm.label(data.label);
                    vm.dateFrom(gc.utils.fromServerTime(data.dateFrom));
                    vm.dateTo(gc.utils.fromServerTime(data.dateTo));
                    vm.enabled(data.enabled);
                });
            }
        },
        attached : function(view, parent) {
            var self = this;
            
            $('#slideShowBaseForm').addClass('save-button-listen-area');
            
            gc.app.onSaveEvent(function(context) {
                var id = $('.tab-content>.active').attr('id');
               
                if(id == 'base') {
                    self.saveData(context);
                }
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        }
    }

    return SlideShowBaseController;
});