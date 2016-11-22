define([ 'durandal/app', 'durandal/composition', 'knockout', 'cb/cb',  'cb-media-asset','cb-media-asset/util'], function(app, composition, ko, cb, mediaAssetAPI, mediaAssetUtil) {
	var ctor = function () {
		this.isShowMediaAssets = ko.observable(false);
		this.directories = ko.observableArray([]);
		this.directory = ko.observable();
	};

	ctor.prototype.activate = function (settings) {
		var self = this;
		this.settings = settings;

		return mediaAssetAPI.getMediaAssetDirectories().then(
			function (data) {
				var dirs = mediaAssetUtil.toTree(data.data.mediaAssetDirectories, self);
				self.directories(dirs);
			});
	};

	ctor.prototype.attached = function () {
		jQuery('.media-assets-modal').appendTo("body");
/*		$('.media-assets-modal').draggable({
			handle: ".media-assets-modal-header"
		});*/
	};

	ctor.prototype.toggleTabContent = function (elements, item) {
		var parts = composition.getParts(elements);
		var tabContent = $(parts.tabContent);

		var wrappedCtxObject = this.settings.value;
		var unwrappedCtxObject = ko.unwrap(this.settings.value);
		var disabled = this.settings.disabled;
		var editable = this.settings.editable;
		var enabled = this.settings.enabled;


		if (_.isUndefined(unwrappedCtxObject)) {
			unwrappedCtxObject = [];
			wrappedCtxObject(unwrappedCtxObject);
		}

		// Get value from the context-object depending on the currently selected
		// language.
		var ctxValue = cb.ctxobj.val(unwrappedCtxObject, item.code);

		// Call preBind() function if one was specified.
		if (!_.isUndefined(ctxValue) && !_.isNull(ctxValue) && ctxValue != '' && !_.isUndefined(this.settings.preBind) && !_.isNull(this.settings.preBind) && _.isFunction(this.settings.preBind)) {
			ctxValue = this.settings.preBind(ctxValue);
		}

		// Get textarea wrapped in div.
		var textarea = $(tabContent).children('textarea').first();

		// CKEDITOR custom config (additionally to common config.js)
		$(textarea).ckeditor({
			toolbarGroups: [
				/*				{name: 'document', groups: ['mode', 'document', 'doctools']},
				{name: 'clipboard', groups: ['clipboard', 'undo']},
				{name: 'editing', groups: ['find', 'selection', 'spellchecker']},*/
				{name: 'others'},
		/*		{name: 'insert'},*/
				/**/
				'/',
				{name: 'basicstyles', groups: ['basicstyles', 'cleanup']},
				{name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi']},
				{name: 'styles'},
				{name: 'colors'},
				{name: 'links'},
		/*		{name: 'tools'}*/],
			entities: false,
			basicEntities: false,
			removeButtons: 'Save,Templates,Preview,NewPage,Print',
			startupFocus: false,
			extraAllowedContent: 'video[*]{*};source[*]{*}'

		});

		var elementName = $(textarea).attr('name');
		CKEDITOR.instances[elementName].on('instanceReady',
			function (ev) {
				if ((!_.isUndefined(editable) && !editable) || (!_.isUndefined(enabled) && !enabled)) {
					ev.editor.setReadOnly();
				}
			}
		);

		var ck_editor = $(textarea).ckeditor().editor;

		CKEDITOR.instances[elementName].on('change', function () {
			//Update the context-object to reflect the change.
			cb.ctxobj.set(unwrappedCtxObject, item.code, $(textarea).val());

			// Inform the ko-observable of the change.
			// if (ko.isObservable(wrappedCtxObject)) {
			//     wrappedCtxObject(unwrappedCtxObject);
			// }

			// Make sure that the save/cancel toolbar sees the change.
			$(textarea).trigger('change');
			CKEDITOR.instances[elementName].updateElement();
		});

		CKEDITOR.instances[elementName].on('focus', function () {
			$(textarea).trigger('click');
			CKEDITOR.instances[elementName].updateElement()
		});

		// Set context-value in textarea form field.
		textarea.text(ctxValue);

		if (item.code == cb.app.currentLang()) {
			$(tabContent).addClass('active');
		} else {
			$(tabContent).removeClass('active');
		}

		// Sometimes the value comes later in the viewmodel's life-cycle, so
		// listen for it.
		wrappedCtxObject.subscribe(function (newValue) {
			wrappedCtxObject = newValue;
			unwrappedCtxObject = ko.unwrap(newValue);

			if (_.isUndefined(unwrappedCtxObject)) {
				unwrappedCtxObject = [];
				wrappedCtxObject(unwrappedCtxObject);
			}

			ctxValue = cb.ctxobj.val(unwrappedCtxObject, item.code);

			console.log("-----------------> item code: " + item.code + " value: " + ctxValue);
			var editor = $(textarea).ckeditor().editor;
			editor.setData(ctxValue, function () {
				$(textarea).val(ctxValue);
			});

		});

		if (!_.isUndefined(disabled) && ko.isObservable(disabled)) {
			disabled.subscribe(function (isDisabled) {
				console.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1 DISABLED ::: ', isDisabled);
				if (isDisabled === true) {
					CKEDITOR.instances[elementName].setReadOnly();
					console.log('!!!!!!!!!!!!!! disabed');
				} else {
					CKEDITOR.instances[elementName].setReadOnly(false);
					console.log('!!!!!!!!!!!!!! enabled');
				}
			});
		}
	};


	ctor.prototype.openDirectory = function(directory){
		this.directory(directory);
	};

	ctor.prototype.showMediaAssets = function() {
		this.isShowMediaAssets(true);
	};

	ctor.prototype.cancelSelect = function() {
		this.isShowMediaAssets(false);
	};

	ctor.prototype.saveSelect = function() {
		var mediaAssetId = cb.app.sessionGet('selectedMediaAsset');
		if(mediaAssetId) {
			this.settings.value(mediaAssetId);
		}
		this.isShowMediaAssets(false);
	};

	return ctor;
});