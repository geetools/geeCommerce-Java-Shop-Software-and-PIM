define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-search-rewrite' ], function(app, ko, router, gc, searchRewriteAPI) {

	function SearchRewriteVM(searchRewriteId) {
		var self = this;
		self.id = ko.observable(searchRewriteId);
		self.keywords = ko.observableArray();
		self.targetUri = ko.observable();

		self.add = function(target, reason){
			var wordVM = new WordVM(self, '');
			self.keywords.unshift(wordVM);
		}

		self.isNew = ko.observable(false);

		if(searchRewriteId == 'new'){
			self.isNew(true);
		}
	}

	function WordVM(parent, word){
		var self = this;
		self.parent = parent;
		self.word = ko.observable(word);

		self.del = function(){
			self.parent.keywords.remove(self);

			$toolbar = $("#searchRewriteBaseForm").closest('form').find('.toolbar-trigger').first();
			// Make sure that the save/cancel toolbar sees the change.
			$toolbar.click();
			$toolbar.trigger('change');
		};


	}
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function SearchRewriteController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof SearchRewriteController)) {
			throw new TypeError("SearchRewriteController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.searchRewriteVM = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	SearchRewriteController.prototype = {
		constructor : SearchRewriteController,
		activate : function(searchRewriteId) {
			var self = this;
			var vm = new SearchRewriteVM(searchRewriteId);
			self.searchRewriteVM = vm ;

			if(!vm.isNew()) {
				searchRewriteAPI.getSearchRewrite(searchRewriteId).then(function (searchRewrite) {
					vm.targetUri(searchRewrite.targetUri);

					var keywordsArray = [];
					_.each(searchRewrite.keywords, function (word) {
						var wordVM = new WordVM(self.searchRewriteVM, word);
						keywordsArray.push(wordVM);
					});

					vm.keywords(keywordsArray);
				});
			}
		},
		saveData : function(view, parent, toolbar) {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			updateModel.field('targetUri', self.searchRewriteVM.targetUri());

			var keywordsArray = [];
			_.each(self.searchRewriteVM.keywords(), function(word) {
				keywordsArray.push(word.word().toLowerCase());
			});
			updateModel.field('keywords', keywordsArray);


			if(self.searchRewriteVM.isNew()) {
				searchRewriteAPI.createSearchRewrite(updateModel).then(function(data) {
					router.navigate('//search-rewrites/details/' + data.id);
					toolbar.hide();
				})
			} else {
				searchRewriteAPI.updateSearchRewrite(self.searchRewriteVM.id(), updateModel).then(function(data) {
					toolbar.hide();
				})
			}

			searchRewriteAPI.updateSearchRewrite(self.searchRewriteVM.id(), updateModel);
		},
		attached : function() {
			var self = this;
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-search-rewrite-details-general', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};
	
	return SearchRewriteController;
});