define([ 'knockout', 'ckeditor' ], function(ko, CKEDITOR) {
	CKEDITOR.disableAutoInline = true;
	return {
		counter: 0,
		prefix: '__cked_',
		init: function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			if (!element.id) {
				element.id = ko.bindingHandlers.ckeditorInline.prefix + (++ko.bindingHandlers.ckeditorInline.counter);
			}
			var options = allBindingsAccessor().ckeditorOptions || {};
			var ckUpdate = allBindingsAccessor().ckUpdate || function () { };

			// Override the normal CKEditor save plugin

			CKEDITOR.plugins.registered['save'] =
			{
				init: function (editor) {
					editor.addCommand('save',
						{
							modes: { wysiwyg: 1, source: 1 },
							exec: function (editor) {
								var ckValue = editor.getData();
								if (editor.checkDirty()) {
									var self = valueAccessor();
									if (ko.isWriteableObservable(self) && (ko.utils.unwrapObservable(self) !==ckValue)) {
										valueAccessor()(ckValue);
									}

									editor.resetDirty();
								}
								ckUpdate.call(ckValue);
								ckValue = null;
							}
						}
					);
					editor.ui.addButton('Save', { label: 'Save', command: 'save', toolbar: 'document' });
				}
			};

			options.on = {
				instanceReady: function (e) {

				},
				blur: function (e) {
					var ckValue = e.editor.getData();
					if (e.editor.checkDirty()) {

						var self = valueAccessor();
						if (ko.isWriteableObservable(self) && (ko.utils.unwrapObservable(self) !==ckValue)) {
							self(ckValue);
						}

						e.editor.resetDirty();
					}
					ckUpdate.call(ckValue);
					ckValue = null;
				}
			};
			options.floatSpaceDockedOffsetY = 0;
			//options.extraPlugins = 'sourcedialog';
			options.removePlugins = 'sourcearea';

			var editor = CKEDITOR.inline(element, options);

			//handle destroying
			ko.utils.domNodeDisposal.addDisposeCallback(element, function () {

				var existingEditor = CKEDITOR.instances && CKEDITOR.instances[element.id];
				if (existingEditor) {
					existingEditor.destroy(true);
				}

			});

		},
		update: function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
			//handle programmatic updates to the observable
			var value = ko.utils.unwrapObservable(valueAccessor()),
				existingEditor = CKEDITOR.instances && CKEDITOR.instances[element.id];

			if (existingEditor) {
				if (value !== existingEditor.getData()) {
					existingEditor.setData(value, function () {
						this.checkDirty(); // true
					});

				}
			}

		}

	};

});