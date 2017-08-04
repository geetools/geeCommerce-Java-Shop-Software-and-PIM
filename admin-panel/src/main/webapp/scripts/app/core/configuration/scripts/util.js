define([ 'knockout', 'gc/gc', 'gc-conf' ], function(ko, gc, confAPI) {

	function Node(data, root, controller) {

		root = typeof root !== 'undefined' ? root : true;

		var self = this;
		self.root = root;
		self.id = null;
		self.idhash = null;
		self.key = null;
		self.data = ko.observable();
		self.displayLabel = ko.observable("NEW NODE");

		self.parent = ko.observable();
		self.children = ko.observableArray();

		self.targetId = ko.observable();
		self.targetType = ko.observable();
		self.enabled = ko.observable(true);
		self.level = ko.observable(0);
		self.useTargetLabel = ko.observable();
		self.label = ko.observableArray([]);
		self.controller = controller;

		if (data != null) {
			self.id = data.id;
			self.idhash = "#" + data.id;
			self.treeItemId = "item_" + data.id;
			self.key = data.key;
			self.data(data);
			if (data.displayLabel)
				self.displayLabel(data.displayLabel);
			self.label(data.label);
			self.useTargetLabel(data.useTargetObjectLabel)
			self.targetId(data.targetObjectId);
			self.targetType(data.targetObjectType);
			self.enabled(data.enabled);
			self.level(data.level);
		}

		self.dLabel = ko.computed(function() {
			var label = "???";
			var v1 = self.targetId();
			var v2 = self.label();
			var v3 = self.useTargetLabel()

			if (self.controller && self.controller.labelsMap) {
				if (self.useTargetLabel() && self.targetId()
						&& self.controller.labelsMap[self.targetId()]) {
					label = self.controller.labelsMap[self.targetId()]();
				} else {
					if (self.label()) {
						label = gc.ctxobj.val(self.label(), gc.app
								.currentUserLang(), 'any');
					}
				}
				self.displayLabel(label);
			}

			if (!label)
				label = "???";
			return label;
		});

		self.addNode = function() {
			var data = {
				'displayLabel' : '[ Newly created node ]',
				'enabled' : false,
				'useTargetLabel' : false,
				'id' : ''
			};
			var child = new Node(data, false, self.controller);
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

		self.toObject = function() {
			var treeObject = {};

			// treeObject.root = self.root;
			// treeObject.id = self.id;
			// treeObject.treeItemId = self.treeItemId;
			// treeObject.data = self.data;
			// treeObject.displayLabel = self.displayLabel;

			// treeObject.parent = self.parent;
			treeObject['children'] = new Array(self.children().length);

			treeObject['tar_obj'] = self.targetId();
			treeObject['tar_obj_type'] = self.targetType();
			treeObject['enabled'] = self.enabled();
			treeObject['level'] = self.level();
			treeObject['key'] = self.key;
			treeObject['_id'] = self.id;
			treeObject['tar_obj_label'] = self.useTargetLabel();
			treeObject['label'] = self.label();

			for (var i = 0; i < self.children().length; i++) {
				treeObject.children[i] = self.children()[i].toObject();
				treeObject.children[i]["position"] = i;
			}
			return treeObject;
		}
	}

	return {
		toTree : function(flatList, controller) {

			var rootNode;
			var i = 0;

			if (_.isEmpty(flatList) || _.isEmpty(flatList[0])) {
				// Navigation list is empty
				console.warn("Warning: Navigation tree is empty.");
				rootNode = new Node(null, true, controller);
				return;
			} else if (flatList[0].level === 0) {
				// First item is root
				rootNode = new Node(flatList[i], true, controller);
				i++;
			} else {
				// First item is not root
				rootNode = new Node(null, true, controller);
			}

			for (; i < flatList.length; i++) {

				var newNode = new Node(flatList[i], false, controller);

				if (newNode.data().level === 1) {
					newNode.parent(rootNode);
					rootNode.children.push(newNode);
				} else {
					var parentNode = rootNode
							.findChild(newNode.data().parentId);

					if (!_.isUndefined(parentNode)) {
						newNode.parent(parentNode);
						parentNode.children.push(newNode);
					}
				}
			}

			return rootNode;
		},
		jsonToTree : function(jsonObject, controller, parentNode) {
			var self = this;
			var newNode;
			var i = 0;

			if (_.isEmpty(jsonObject)) {
				// Navigation list is empty
				console.warn("Warning: Configuration node is empty.");
				parentNode = new Node(null, true, controller);
				return;
			} 
			else {
				// First item is not root
				parentNode = new Node(jsonObject, true, controller);
			}
			
			for (; i < jsonObject.children.length; i++) {
				var newNode = self.jsonToTree(jsonObject.children[i], controller, newNode);
				parentNode.children.push(newNode);
			}

			return parentNode;
		}
	}
});