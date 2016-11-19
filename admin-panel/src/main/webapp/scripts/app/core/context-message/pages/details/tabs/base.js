define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-context-message', 'knockout-validation' ], function(app, ko, router, gc, contextMessageAPI, validation) {

    function ContextMessageVM(contextMessageId) {
        var self = this;
        self.id = ko.observable(contextMessageId);
        self.key = ko.observable();
        self.value = ko.observableArray();

        self.isNew = ko.observable(false);

        if(contextMessageId == 'new'){
            self.isNew(true);
        }

    }


    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ContextMessageBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContextMessageBaseController)) {
            throw new TypeError("ContextMessageBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.contextMessageVM = {};
        this.contextMessageId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    ContextMessageBaseController.prototype = {
        constructor : ContextMessageBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.context-message.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.context-message.detailsSubtitle',
        saveData : function(view, parent, toolbar) {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('key', self.contextMessageVM.key());
            updateModel.field('value', self.contextMessageVM.value(), true);

            if(self.contextMessageVM.isNew()) {
                contextMessageAPI.createMessage(updateModel).then(function(data) {
                    router.navigate('//context-messages/details/' + data.id);
                    toolbar.hide();
                })
            } else {
                contextMessageAPI.updateMessage(self.contextMessageId(), updateModel).then(function(data) {
                    toolbar.hide();
                })
            }


        },
        activate : function(data) {
            var self = this;
            self.contextMessageId(data);
            var vm = new ContextMessageVM(data);
            self.contextMessageVM = vm;

            if(!vm.isNew()){
                contextMessageAPI.getMessage(self.contextMessageId()).then(function(data) {
                    console.log(data);
                    vm.key(data.key);
                    vm.value(data.value);


                });
            }
            
        }
    }

    return ContextMessageBaseController;
});