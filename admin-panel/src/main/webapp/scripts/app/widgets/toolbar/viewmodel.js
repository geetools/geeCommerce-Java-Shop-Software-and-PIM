define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;
		
		this.options = options;

		// Target container where target fields reside. 
		this.targetId = options.targetId;
		// Text to display if it is not a localized text.
		this.text = options.text;
		// Key of text to display if it is a localized text.
		this.i18n = options.i18n;
		// Args that should be passed to the save-callback-function.
		this.passthru = options.passthru;
		
		// Function to call when user clicks on the save button.
		this.saveCallback = options.saveCallback;
		// Function to call when user clicks on the cancel button.
		this.cancelCallback = options.cancelCallback;

		// Optionally set a dirty flag which can be observed for tracking changes (as an alternative to JQUERY :input checking).
		this.dirtyFlag = options.dirtyFlag;
		
		// We use a computed variable here so  that the text changes, when the user
		// chooses another language.
		this.message = ko.computed(function() {
			var lang = gc.app.currentUserLang();

			if(!_.isUndefined(self.i18n)) {
				return i18n.t(self.i18n, lang);
			} else {
				return self.text;
			}
		});

		if ($( '#' + self.targetId + ' .toolbar-trigger').length == 0) {
			$( '#' + self.targetId ).append('<input type="hidden" class="toolbar-trigger" value="" />');
		}
	};

	ctor.prototype.reset = function(view, parent) {
		var self = this;

		var toolbar = $(view).children('div.info-toolbar-outer').first();
		
		$( '#' + self.targetId ).off('change', ':input, textarea');
		$(toolbar).removeAttr('data-init');
	};


	 // Once the html-elements are attached, we can start listening for changes
	 // to the form or for clicks on one of the buttons.
	ctor.prototype.compositionComplete = function(view, parent) {
		var self = this;
		
		// As durandal now automatically sets the outermost div-layer to display:block, we have to move down one level.
		var toolbar = $(view).children('div.info-toolbar-outer').first();
		
		// Remember which form the toolbar belongs to.
		toolbar.attr('data-for', self.targetId);
		
		// If we have a dirtyFlag, use that to track  changes.
		if(!_.isUndefined(self.dirtyFlag) && ko.isObservable(self.dirtyFlag)) {

            self.dirtyFlag.subscribe(function (newValue) {
                $(toolbar).attr('data-init', '1');
                $(toolbar).find('div.loader').hide();
                $(toolbar).find('div.buttons').show();
                $(toolbar).fadeIn(600);

                self.dirtyFlag(false);
            });
        }
		// Otherwise use standard JQUERY-handling to listen for changes.
		if(true) {
			$( '#' + self.targetId ).on('click', ':input, textarea', function(evt1) {
				// Make sure that we only register the change listener once.
				var isInitialized = $(toolbar).attr('data-init');			
				if(_.isUndefined(isInitialized)) {
				
                    if($(evt1.currentTarget).attr('type') == 'checkbox' || $(evt1.currentTarget).attr('type') == 'radio') {
                        $(toolbar).attr('data-init', '1');
                        $(toolbar).find('div.loader').hide();
                        $(toolbar).find('div.buttons').show();
                        $(toolbar).fadeIn(600);
                    }

					$( '#' + self.targetId ).on('change', ':input, textarea', function(evt2) {
						$(toolbar).attr('data-init', '1');
                        $(toolbar).find('div.loader').hide();
                        $(toolbar).find('div.buttons').show();
						//$(toolbar).fadeIn(600);

						function f(){
							$(toolbar).fadeIn(600);
						}
						setTimeout(f, 200);
					});


				}
			});
        }
		
		// Listen for a click on the save button. The callback function is responsible
		// for hiding the toolbar. This can be done with toolbar.hide();
		$(toolbar).on('click', '[data-btn-event="save"]', function(evt) {
			$(toolbar).find('div.buttons').fadeOut(300, function() {
				$(toolbar).find('div.loader').fadeIn(300);
			});
			
			if(!_.isUndefined(self.saveCallback) && _.isFunction(self.saveCallback)) {
				self.saveCallback(toolbar, parent, {
					hide : function() {
						$(toolbar).find('div.loader').fadeOut(300, function() {
							$(toolbar).find('div.buttons').fadeIn(300);
						});
						
						$(toolbar).fadeOut(300);
					}
				}, self.passthru);
			}
			
			self.reset(view, parent);
		});
		
		// Listen for a click on the cancel button. We either simply hide the toolbar
		// or call the callback, which is then responsible for hiding it.
		$(toolbar).on('click', '[data-btn-event="cancel"]', function(evt) {
			if(!_.isUndefined(self.cancelCallback) && _.isFunction(self.cancelCallback)) {
				self.cancelCallback(toolbar, parent, {
					hide : function() {
						$(toolbar).fadeOut(300);
					}
				});
			}
			else {
				// We hide the toolbar by default on cancel if no callback-function has been specified.
				$(toolbar).fadeOut(300);
			}
			
			self.reset(view, parent);
		});
	};

	return ctor;
});