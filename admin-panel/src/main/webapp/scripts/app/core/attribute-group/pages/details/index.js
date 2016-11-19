define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'knockout-validation', 'gc-attribute-group' ], function(app, ko, router, gc, validation, attributeGroupAPI ) {

    function AttributeGroupVM(attributeGroupId) {
        var self = this;
        self.id = ko.observable(attributeGroupId);
        self.code = ko.observable();
        self.label = ko.observableArray();
        self.position = ko.observable();
        self.items = ko.observableArray();
        self.isNew = ko.observable(false);

        if(attributeGroupId == 'new'){
            self.isNew(true);
        }
        
    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function AttributeGroupDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof  AttributeGroupDetailsIndexController)) {
            throw new TypeError(" AttributeGroupDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.attributeGroupId = ko.observable();
        this.attributeGroupVM = null;

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    AttributeGroupDetailsIndexController.prototype = {
        constructor : AttributeGroupDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.attribute-group.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.attribute-group.detailsSubtitle',
        activate : function(attributeGroupId) {
            var self = this;
            self.attributeGroupId(attributeGroupId);

            self.attributeGroupVM = new AttributeGroupVM(attributeGroupId);

            if (attributeGroupId == 'new') {
                gc.app.pageTitle('New Attribute Group');
                gc.app.pageDescription('Create a new attribute group');


                gc.app.sessionPut('attributeGroupVM', self.attributeGroupVM);
            } else {

                gc.app.pageTitle(self.pageTitle());
                gc.app.pageDescription(self.pageDescription);

                return attributeGroupAPI.getAttributeGroup(self.attributeGroupId()).then(function(data) {
                    console.log(data);
                    self.attributeGroupVM.code(data.code);
                    self.attributeGroupVM.label(data.label);
                    self.attributeGroupVM.position(data.position);
                    //   vm.column(data.column);

                    var items = [];
                    _.each(data.items, function (item) {
                        items.push({id: item.id, type: item.type});
                    });
                    console.log("--ITEMS--");
                    console.log(items);
                    self.attributeGroupVM.items(items);
                    
                    gc.app.sessionPut('attributeGroupVM', self.attributeGroupVM);
                    gc.app.pageTitle(self.pageTitle());
                });
            }

        },
        attached : function() {
            var self = this;

        },
        compositionComplete : function() {
            $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
                console.log('_________$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$', e);
            });

        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return  AttributeGroupDetailsIndexController;
});