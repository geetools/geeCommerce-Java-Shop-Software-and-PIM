define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-merchant' ], function(app, ko, gc, merchantAPI) {

    function ViewVM(id) {
		var self = this;
        self.id = ko.observable(id);
        self.code = ko.observable();
        self.name = ko.observable();
        self.parentView = ko.observable();

		self.editMode = ko.observable(false);

        self.edit = function () {
			self.editMode(true)
        }
        
        self.save = function () {
			self.editMode(false)

			if(self.id() != 'new'){

			} else {

			}
        }


        
    }
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function MerchantTabViewsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof MerchantTabViewsController)) {
			throw new TypeError("MerchantTabViewsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.merchantVM = {};
        this.merchantId = ko.observable();
		this.views = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'removeView', 'addNewView',  'initViews');
	}

    MerchantTabViewsController.prototype = {
		constructor : MerchantTabViewsController,
		saveData : function(view, parent, toolbar) {
            var self = this;
            var updates = [];

			if (!_.isEmpty(self.views())) {

				_.each(self.views(), function(data) {

					var updateModel = gc.app.newUpdateModel().id(data.id() != 'new'? data.id() : null)
						.field('code', data.code())
						.field('name', data.name())
						.field('parentViewId', data.parentView());

					updates.push(updateModel.data());
				});

				merchantAPI.updateViews(self.merchantId(), updates).then(function(data) {
					self.merchantVM.data = data;
                    self.initViews();
                    toolbar.hide();
				});
			}

		},
		initViews: function () {
            var self = this;
            var views = []

            _.each(self.merchantVM.data.views, function (view) {
                var viewVM = new ViewVM(view.id);
                viewVM.code(view.code);
                viewVM.name(view.name);
                viewVM.parentView(view.parentViewId);

                views.push(viewVM)
            })

            self.views(views)
        },
		activate : function(merchantId) {
			var self = this;
			
			self.merchantVM = gc.app.sessionGet('merchantVM');
			self.merchantId(merchantId);

			self.initViews();
		},
		addNewView : function(data, element) {
            var self = this;
            var viewVM = new ViewVM('new');
            self.views.unshift(viewVM)
        },
        removeView : function(data, element) {
            var self = this;
            var _data = ko.toJS(data);
            
            if(_data.id == 'new'){
                self.views.remove(data);
			} else {
            	merchantAPI.removeView(self.merchantId(), _data.id).then(function () {
                    self.views.remove(data);
                })
			}
        },
	};

	return MerchantTabViewsController;
});