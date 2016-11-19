define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-coupon' ], function(app, ko, gc, couponAPI) {
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponConditionController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponConditionController)) {
			throw new TypeError("CouponController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.couponVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	CouponConditionController.prototype = {
        booleanCombinationFilterChoice: ko.observableArray([{value:"FOUND", label:"Found"}, {value:"NOT_FOUND", label:"Not Found"}]),
        booleanFilterChoice: ko.observableArray([{value:"AND", label:"All"}, {value:"OR", label:"Any"}]),
        booleanValueFilterChoice: ko.observableArray([{value: 1, label:"True"}, {value: 0, label:"False"}]),
        nodeBCreatingChoice: ko.observableArray([{value:"1", label:"Condition Combination"}, {value:"12", label:"Product Combination"}, {value:"11", label:"Cart Attribute"}]),
        nodeFCreatingChoice: ko.observableArray([{value:"1", label:"Condition Combination"}, {value:"2", label:"Cart Item Attribute"}, {value:"3", label:"Product Item Attribute"}]),
        constructor : CouponConditionController,
		saveData : function() {
			var self = this;

			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(couponId) {
			var self = this;

            self.couponVM = gc.app.sessionGet('couponVM');

		},
		attached : function() {

		}
	};

	return CouponConditionController;
});