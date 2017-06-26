define(['durandal/app', 'knockout', 'gc/gc', 'gc-template'], function (app, ko, gc, templateAPI) {



    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function TemplateGridIndexController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof TemplateGridIndexController)) {
            throw new TypeError("TemplateGridIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.pager = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'removeTemplate');
    }

    TemplateGridIndexController.prototype = {
        constructor : TemplateGridIndexController,
        app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
        pager: {},
        removeTemplate: function(template) {
            var self = this;
            var yes = gc.app.i18n('app:common.yes');
            var no = gc.app.i18n('app:common.no');

            app.showMessage(gc.app.i18n('app:modules.template.confirmDelete'), gc.ctxobj.val(template.label, gc.app.currentLang()), [yes, no]).then(function(confirm) {
                if(confirm == yes) {
                    templateAPI.removeTemplate(template.id).then(function() {
                        self.pager.removeData(template);
                    });
                }
            });
        },
        activate: function(data) {
            var self = this;

            gc.app.pageTitle(gc.app.i18n('app:modules.template.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.template.subtitle'));

            // Pager columns
            var pagerColumns = [
                {'name' : 'label', 'label' : 'app:modules.template.gridColLabel'},
                {'name' : 'uri', 'label' : 'app:modules.template.gridColUri'},
                {'name' : '', 'label' : ''}
            ];

            // Init the pager.
            this.pager = new gc.Pager(templateAPI.getPagingOptions({columns : pagerColumns, multiContext : true }));

            // We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
            return this.pager.load();
        },
        compositionComplete : function() {
            var self = this;
            self.pager.activateSubscribers();
        }
    }
    return TemplateGridIndexController;
});