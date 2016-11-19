define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-synonym' ], function(app, ko, router, gc, synonymAPI) {

	function SynonymVM(synonymId) {
		var self = this;
		self.id = ko.observable(synonymId);
		self.word = ko.observable();
		self.custom = ko.observable();
		self.synonyms = ko.observableArray();

		self.add = function(target, reason){
			var wordVM = new WordVM(self, '');
			self.synonyms.unshift(wordVM);

		}

		self.isNew = ko.observable(false);

		if(synonymId == 'new'){
			self.isNew(true);
		}
	}


	function WordVM(parent, word){
		var self = this;
		self.parent = parent;
		self.word = ko.observable(word);

		self.deleteSynonym = function() {
			self.parent.synonyms.remove(self);

			$toolbar = $("#synonymBaseForm").closest('form').find('.toolbar-trigger').first();
			// Make sure that the save/cancel toolbar sees the change.
			$toolbar.click();
			$toolbar.trigger('change');
		};


	}
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function SynonymController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof SynonymController)) {
			throw new TypeError("SynonymController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.synonymVM = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	SynonymController.prototype = {
		constructor : SynonymController,
		activate : function(synonymId) {
			var self = this;
			var vm = new SynonymVM(synonymId);
			self.synonymVM = vm ;

			if(!vm.isNew()) {
				synonymAPI.getSynonym(synonymId).then(function (synonym) {
					vm.word(synonym.word);
					vm.custom(synonym.custom);
					var synonymsArray = [];
					_.each(synonym.synonyms, function (word) {
						var wordVM = new WordVM(self.synonymVM, word);
						synonymsArray.push(wordVM);
					});

					vm.synonyms(synonymsArray);
				});
			}
		},
		saveData : function(view, parent, toolbar) {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			updateModel.field('word', self.synonymVM.word());
			updateModel.field('custom', true);

			var synonymsArray = [];
			_.each(self.synonymVM.synonyms(), function(word) {
				synonymsArray.push(word.word());
			});
			updateModel.field('synonyms', synonymsArray);


			if(self.synonymVM.isNew()) {
				synonymAPI.createSynonym(updateModel).then(function(data) {
					router.navigate('//synonyms/details/' + data.id);
					toolbar.hide();
				})
			} else {
				synonymAPI.updateSynonym(self.synonymVM.id(), updateModel).then(function(data) {
					toolbar.hide();
				})
			}

			synonymAPI.updateSynonym(self.synonymVM.id(), updateModel);
		},
		attached : function() {
			var self = this;
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-synonym-details-general', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};
	
	return SynonymController;
});