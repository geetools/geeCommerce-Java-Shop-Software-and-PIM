define([ 'knockout', 'gc/gc', 'gc-navigation' ], function(ko, gc, navAPI) {

	function NodeVM(data, root, controller) {

		var self = this;
		
		if (_.isEmpty(data) || _.isEmpty(data.id)) {
			
			self.app = gc.app;
			self.temporaryNodeID = self.app.localStorageGet('temporaryNodeID');
			
			if (self.temporaryNodeID<1) {
				self.temporaryNodeID = 0;
			}
			
			self.temporaryNodeID = (self.temporaryNodeID + 1);
			
			self.app.localStoragePut('temporaryNodeID',self.temporaryNodeID);
			
			self.id = "new_" + self.temporaryNodeID;
			self.treeItemId = "item_undefined_" + self.temporaryNodeID;
			
			//console.log('CHECK!!! self.treeItemId',self.treeItemId,self.id);
		} else {
			self.id = data.id;
			self.treeItemId = "item_undefined_" + self.id;
		}
		
		if (typeof root !== 'undefined') {
			self.root = root;
		} else {
			self.root = true;
		}

		self.root = root;
		//self.id = null;
		self.key = ko.observable();
		self.treeItemId = null;
		self.data = ko.observable();
		self.displayLabel = ko.observable("");

		self.parent = ko.observable();
		self.children = ko.observableArray();

		self.targetId = ko.observable();
		self.targetType = ko.observable();
		self.enabled = ko.observable(true);
		self.level = ko.observable(0);
		self.useTargetLabel = ko.observable();
		self.label = ko.observableArray([]);
		self.externalURL = ko.observableArray([]);
		self.controller = controller;

		if (data != null) {

			self.key(data.key);

			self.data(data);
			if (data.displayLabel)
				self.displayLabel(data.displayLabel);
			self.label(data.label);
			self.useTargetLabel(data.useTargetObjectLabel)
			self.targetId(data.targetObjectId);
			self.targetType(data.targetObjectType);
			self.enabled(data.enabled);
			self.level(data.level);
			self.externalURL(data.externalURL);
		}

		self.dLabel = ko.computed(function() {
					var label = "[ Newly created node ]";
					var v1 = self.targetId();
					var v2 = self.label();
					var v3 = self.useTargetLabel()
					//console.log(self.controller);
					if (self.controller && self.controller.labelsMap) {

						if (self.targetType() == 'LINK') {
							if (self.label()) {
								label = gc.ctxobj.val(self.label(), gc.app.currentUserLang(), 'any');
							}
						} else if (self.useTargetLabel() && self.targetId() && self.controller.labelsMap[self.targetId()]) {
							if (typeof self.controller.labelsMap[self.targetId()] === 'function') {
								label = self.controller.labelsMap[self.targetId()]();
							} else {
								label = self.controller.labelsMap[self.targetId()];
							}

						} else {
							if (self.label()) {
								label = gc.ctxobj.val(self.label(), gc.app.currentUserLang(), 'any');
							}
						}
						self.displayLabel(label);
					}

					if (!label)
						label = "[ Newly created node ]";
					return label;
				});

		self.addNode = function() {
			var data = {
				'displayLabel' : '[ Newly created node ]',
				'enabled' : false,
				'useTargetLabel' : false,
				'id' : ''
			};

			var child = new NodeVM(data, false, self.controller);
			child.parent(self);
			child.useTargetLabel(true);
			self.children.push(child);

			return child;
		}

		self.deleteNode = function() {
			var index = self.parent().children.indexOf(self);
			self.parent().children.splice(index, 1);
		}

		self.dragNode = function() {

		}

		self.findChild = function(id) {

			var foundChild = undefined;
			var _children = self.children();

			for (var i = 0; i < _children.length; i++) {
				var _child = _children[i];

				if (_child.id == id) {
					return _child;
				} else {
					foundChild = _child.findChild(id);
				}

				if (!_.isUndefined(foundChild))
					break;
			}

			return foundChild;
		}

		self.toObject = function(level) {
			var treeObject = {};

			// treeObject.root = self.root;
			// treeObject.id = self.id;
			// treeObject.treeItemId = self.treeItemId;
			// treeObject.data = self.data;
			// treeObject.displayLabel = self.displayLabel;

			// treeObject.parent = self.parent;
			treeObject['children'] = new Array(self.children().length);

            //if(treeObject['tar_obj'])
			treeObject['tar_obj'] = self.targetId();

			treeObject['tar_obj_type'] = self.targetType();
			treeObject['enabled'] = self.enabled();
			treeObject['level'] = level;
			treeObject['key'] = self.key();

			//console.log('Creating new object',self.id);
			if(self.id.substring(0,4)!='new_') {
				treeObject['_id'] = self.id; // no need of _if
			}
			treeObject['tar_obj_label'] = self.useTargetLabel();
			treeObject['label'] = self.label();
			treeObject['ext_url'] = self.externalURL();

			for (var i = 0; i < self.children().length; i++) {
				treeObject.children[i] = self.children()[i].toObject(level + 1);
				treeObject.children[i]["position"] = i;
			}
			
			//console.log('Final tree object',treeObject);
			
			return treeObject;
		}
	}

	return {
		toTree : function(flatList, controller, isNew) {

			var rootNode;
			var i = 0;

			if (_.isEmpty(flatList) || _.isEmpty(flatList[0])) {
				// Navigation list is empty
				rootNode = new NodeVM(null, true, controller);
				console.warn("Warning: Navigation tree is empty.", rootNode);

				return rootNode;
			} else if (flatList[0].level === 0) {
				// First item is root
				rootNode = new NodeVM(flatList[i], true, controller);
				i++;
			} else {
				// First item is not root
				rootNode = new NodeVM(null, true, controller);
			}

			for (; i < flatList.length; i++) {

				var newNode = new NodeVM(flatList[i], false, controller);

				if (newNode.data().level === 1) {
					newNode.parent(rootNode);
					rootNode.children.push(newNode);
				} else {
					var parentNode = rootNode.findChild(newNode.data().parentId);

					if (!_.isUndefined(parentNode)) {
						newNode.parent(parentNode);
						parentNode.children.push(newNode);
					}
				}
			}

			return rootNode;
		}
	}
});