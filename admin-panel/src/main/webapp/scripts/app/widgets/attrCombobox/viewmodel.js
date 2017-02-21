define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute', 'magicsuggest' ], function(app, composition, ko, i18n, gc, attrAPI, magicsuggest) {
	var ctor = function() {
        var self = this;

	};

	ctor.prototype.activate = function(options) {
		var self = this;

		// Selected values - typically some knockout observable.
		self.value = options.value;
		// For which type is this attribute (product, order, customer ...)?
		self.forType = options.forType;
		// For which attribute is this combobox.
		self.attributeId = options.attributeId;
		// Sets a custom class in the wrapper div.
		self.customCls = options.customCls || '';
		// Options that are passed directly to magicSuggest (http://nicolasbize.com/magicsuggest/doc.html).
		self.msOptions = options.ms || {};
		// Determines if suggestions should match in a case-sensitive manner.
		self.matchCase = options.ms.matchCase || false;
		// How many characters should be typed before data is to be fetched?
		self.minChars = self.msOptions.minChars || 2;
		// How many suggestions can be displayed at once.
		self.maxSuggestions = self.msOptions.maxSuggestions || 20;
		// How much time should pass until the next ajax call is made?
		self.minTimeBetweenDataFetches = options.dataInterval || 500;
		// How much time should pass between key-strokes before we consider fetching data?
		self.minTimeBetweenKeyStrokes = options.keyStrokeInterval || 300;

		// Internal value for counting key-strokes.
		self._keypressCount = 0;
		// Internal value for remembering last time data was fetched.
		self._lastDataFetchTime = 0;

		self.data = [];

		if(_.isUndefined(self.msOptions.minChars)) {
			self.msOptions.minChars = self.minChars;
		}

		if(_.isUndefined(self.msOptions.maxSuggestions)) {
			self.msOptions.maxSuggestions = self.maxSuggestions;
		}

		// Add translation to magicSuggect for "max entry" text.
		if(_.isUndefined(self.msOptions.maxEntryRenderer)) {
			self.msOptions.maxEntryRenderer = function(v) {
			    return gc.app.message(v > 1 ? 'app:common.msMaxEntryRenderer2' : 'app:common.msMaxEntryRenderer1', gc.app.currentUserLang(), { value: v });
		  };
		}

		// Add translation to magicSuggect for "max selection" text.
		if(_.isUndefined(self.msOptions.maxSelectionRenderer)) {
			self.msOptions.maxSelectionRenderer = function(v) {
			    return gc.app.message(v > 1 ? 'app:common.msMaxSelectionRenderer2' : 'app:common.msMaxSelectionRenderer1', gc.app.currentUserLang(), { value: v });
		  };
		}

		// Add translation to magicSuggect for "min characters" text.
		if(_.isUndefined(self.msOptions.minCharsRenderer)) {
			self.msOptions.minCharsRenderer = function(v) {
			    return gc.app.message(v > 1 ? 'app:common.msMinCharsRenderer2' : 'app:common.msMinCharsRenderer1', gc.app.currentUserLang(), { value: v });
		  };
		}

		// Add translation to magicSuggect for "no suggestions" text.
		if(_.isUndefined(self.msOptions.noSuggestionText)) {
			self.msOptions.noSuggestionText = gc.app.message('app:common.msNoSuggestionText', gc.app.currentUserLang());
		}
	};

    ctor.prototype.attached = function(view) {
        var self = this;

        var ms = $(view).find('.attrComboboxMagicsuggest').first().magicSuggest(self.msOptions);

				// Initialize the combobox with previous values.
				self.initValuesAndData(self.value, ms);

				// Here we are listening for key presses so that we can fetch data accordingly.
				$(ms).on('keyup', function(e, m, v) {
					var t = new Date().getTime();

						self._keypressCount++;
						var currentKeyPressCount = self._keypressCount;

						// Here we are putting the data fetching in a timeout function so that we can check if more characters habe been entered
						// by the user. If the use has entered more characters we will wait until there is a short pause. This avoids fetching
						// data on every keypress.
						window.setTimeout(function() {
							if(currentKeyPressCount == self._keypressCount) {
								// Once we have passed the keypress check, we will do a final check on the actual fetching interval.
								// New data will only be fetched after this interval to avoid making too many ajax calls.
								var timeSinceLastFetch = t - self._lastDataFetchTime;
								if(timeSinceLastFetch < self.minTimeBetweenDataFetches)
									return;

								self._lastDataFetchTime = t;

								// After having passed both of the previous interval checks we can now do the actual data fetching.
								attrAPI.findAttributeOptions(self.attributeId, m.getRawValue(), gc.app.currentLang(), self.maxSuggestions, self.matchCase).then(function(response) {
										var data = [];

										if(!_.isEmpty(response.data.options)) {
												_.each(response.data.options, function(option) {
														gc.ctxobj.enhance(option, [ 'label' ], 'any');

														if (option.id && option.label && option.label.i18n) {
																data.push({ id: option.id, name: option.label.i18n() });
														}
												});

												ms.setData(data);
										}
								});
							}
						}, self.minTimeBetweenKeyStrokes);
				});

        self._internalSelectionChange = false;

        // When the selection changes we check the last value to see if it already exists in the backend.
        // If it does not, we create it and update the selection and data arrays in magicsuggest in order
        // to replace the plain String with the {id,name} equivalent.
        $(ms).on('selectionchange', function(e, m) {

            if(self._internalSelectionChange === true) {
                self._internalSelectionChange = false;
                return;
            }

            var that = this;
            var currentSelection = this.getValue();
            var len = currentSelection.length;

            if(len > 0) {
                // Get the entry that was entered last by the user.
                var lastEntry = currentSelection[len-1];

                // If it is a String, we know that it does not exist yet (otherwise it would be an id.)
                if(_.isString(lastEntry) && !self.isId(lastEntry)) {
                    // Turn the plain string into a context-object with specified language.
                    var ctxVal = gc.ctxobj.create(lastEntry, gc.app.currentUserLang());

                    // Make API call to create option in backend.
                    attrAPI.createOption(self.attributeId, {
                        attributeId: self.attributeId,
                        label: ctxVal,
                        position: 0
                    }, true, self.matchCase).then(function(savedOption) {
                        //Remove new text that was entered and replace it with the id just received from saving.
                        var newSelection = _.without(currentSelection, lastEntry);
                        newSelection.push(savedOption.id);

                        // After saving the option, we get all of the currently selected options
                        // so that we can update the data value of magicsuggest.
                        attrAPI.getAttributeOptions(self.attributeId, newSelection).then(function(response) {
                            var newData = [];

                            if(!_.isEmpty(response.data['attribute-options'])) {
                                // Re-populate the data array with all of the currently selected options,
                                // including the new one.
                                _.each(response.data['attribute-options'], function(attrOption) {
                                    gc.ctxobj.enhance(attrOption, [ 'label' ], 'any');
                                    newData.push({ id: attrOption.id, name: attrOption.label.i18n() });
                                });

                                // We mark the current change as an internal one to stop an endless loop from taking place.
                                self._internalSelectionChange = true;
                                that.setData(newData);
                                that.setSelection([]);

                                // We mark the current change as an internal one to stop an endless loop from taking place.
                                self._internalSelectionChange = true;
                                that.setValue(newSelection);

                                // Update object's observable with new value.
                                self.value(newSelection);
                            }
                        });

                    });
                } else {
                    // If the last option already exists or a value has been removed,
                    // we just update the object's observable value.
                    self.value(this.getValue());
                }
            } else {
                self.value([]);
            }
        });
    };

    ctor.prototype.initValuesAndData = function(value, ms) {
        var self = this;
        var optionIds = ko.unwrap(value);

        if(optionIds && _.isArray(optionIds) && !_.isEmpty(optionIds)) {
            attrAPI.getAttributeOptions(self.attributeId, optionIds).then(function(response) {
                var newData = [];
                var newSelection = [];

                if(!_.isEmpty(response.data['attribute-options'])) {
                    _.each(response.data['attribute-options'], function(attrOption) {
                        gc.ctxobj.enhance(attrOption, [ 'label' ], 'any');
                        newData.push({ id: attrOption.id, name: attrOption.label.i18n() });
                        newSelection.push(attrOption.id);
                    });

                    // We mark the current change as an internal one to stop an endless loop from taking place.
                    self._internalSelectionChange = true;
                    ms.setData(newData);
                    ms.setSelection([]);

                    // We mark the current change as an internal one to stop an endless loop from taking place.
                    self._internalSelectionChange = true;
                    ms.setValue(newSelection);
                }
            });
        }
    };

    ctor.prototype.isId = function(str) {
        var regex = /[\d]{10,}/g;
        return regex.test(str);
    };

	return ctor;
});
