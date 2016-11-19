define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-coupon' ], function(app, ko, gc, couponAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponBaseController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponBaseController)) {
			throw new TypeError("CouponController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.couponVM = {};

        this.modal = (function () {
            var pleaseWaitDiv = $(
                // '<div class="modal hide" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false"><div class="modal-header"><h1>Generating coupons...</h1></div><div class="modal-body"><div class="progress progress-striped active"><div class="bar" style="width: 100%;"></div></div></div></div>'
                '<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-hidden="true" style="padding-top:15%; overflow-y:visible;">' +
                '<div class="modal-dialog modal-m">' +
                '<div class="modal-content">' +
                '<div class="modal-header"><h3 style="margin:0;">Generating coupons...</h3></div>' +
                '<div class="modal-body">' +
                '<div class="progress progress-striped active" style="margin-bottom:0;"><div class="progress-bar" style="width: 100%"></div></div>' +
                '</div>' +
                '</div></div></div>'
            );
            return {
                showPleaseWait: function() {
                    pleaseWaitDiv.modal();
                },
                hidePleaseWait: function () {
                    pleaseWaitDiv.modal('hide');
                }
            };
        })();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    CouponBaseController.prototype = {
		constructor : CouponBaseController,
        couponCodePatterns : ko.observableArray(),
        rawCouponCodePatterns : {},
        saveData : function() {
            var self = this;

            var updateModel = gc.app.newUpdateModel();

        },
        generateAdditionalCodes : function(data) {
            var self = this;
            self.modal.showPleaseWait();
            couponAPI.generateAdditionalCoupons(self.couponVM.id(), self.couponVM.generation.quantity()).then(function(){
                self.modal.hidePleaseWait();
                alert(self.couponVM.generation.quantity() + " codes were generated");
            });
        },
		activate : function(couponId) {
			var self = this;
			
			self.couponVM = gc.app.sessionGet('couponVM');

            self.patternDescription = ko.computed(function() {
                var descr = '';
                if(this.couponVM.generation.pattern()){
                    var id = this.couponVM.generation.pattern();
                    var pattern = this.rawCouponCodePatterns[id];
                    if(pattern){
                        if(pattern.isPattern){
                            descr += '<b>Pattern:</b><br> ' + pattern.pattern +'<br>';
                            descr += '<b>Rules:</b> <br>';
                            for(key in pattern.productionRules){
                                descr += key + " -> " + pattern.productionRules[key] + '<br>';
                            }
                        } else {
                            descr += '<b>Characters for code:</b><br> '  + pattern.terminalString;
                        }
                    }
                }
                return descr;
            }, self);

            couponAPI.getCouponCodePatterns().then(function(data) {
                self.couponCodePatterns.removeAll();
                data.data.couponCodePatterns.each(function(elem, index) {
                    self.rawCouponCodePatterns[elem.id] = elem;
                });
                self.couponVM.generation.patterns = self.rawCouponCodePatterns;
                gc.ctxobj.enhance(data.data.couponCodePatterns, [ 'name' ], 'any');
                data.data.couponCodePatterns.each(function(elem, index) {
                     var name = elem.name.i18n();
                    var val = elem.id;

                    self.couponCodePatterns.push({'value': val, 'label': name});
                });
            });
		},
		attached : function() {
			var self = this;
		}
	};

	return CouponBaseController;
});