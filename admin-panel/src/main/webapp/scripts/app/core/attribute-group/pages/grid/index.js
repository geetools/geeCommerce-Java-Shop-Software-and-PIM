define(['durandal/app', 'knockout', 'gc/gc', 'gc-attribute-group'], function (app, ko, gc, attributeGroupAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeGroupGridIndexController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeGroupGridIndexController)) {
			throw new TypeError("AttributeGroupGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeAttributeGroup');
	}

	AttributeGroupGridIndexController.prototype = {
		constructor: AttributeGroupGridIndexController,
		removeAttributeGroup: function (attributeGroup) {
			var self = this;
			var yes = gc.app.i18n('app:common.yes');
			var no = gc.app.i18n('app:common.no');

			app.showMessage(gc.app.i18n('app:modules.attribute-group.confirmDelete'), gc.ctxobj.val(attributeGroup.label, gc.app.currentLang()), [yes, no]).then(function (confirm) {
				if (confirm == yes) {
					attributeGroupAPI.removeAttributeGroup(attributeGroup.id).then(function () {
						self.pager.removeData(attributeGroup);
					});
				}
			});
		},
		activate: function (data) {
			var self = this;

			gc.app.pageTitle(gc.app.i18n('app:modules.attribute-group.title'));
			gc.app.pageDescription(gc.app.i18n('app:modules.attribute-group.subtitle'));

			// Pager columns
			var pagerColumns = [
			//	{'name': '', 'label': ''},
				{'name': 'code', 'label': 'app:modules.attribute-group.gridColCode'},
				{'name': 'label', 'label': 'app:modules.attribute-group.gridColLabel'},
				{'name': 'position', 'label': 'app:modules.attribute-group.gridColPosition'},
			//	{'name': 'column', 'label': 'app:modules.attribute-group.gridColColumn'},
				{'name': '', 'label': ''}

			];

			// Init the pager.
			self.pager = new gc.Pager(attributeGroupAPI.pagingOptions({columns: pagerColumns}));

			// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
			return self.pager.load();
		}
	}

	return AttributeGroupGridIndexController;

});