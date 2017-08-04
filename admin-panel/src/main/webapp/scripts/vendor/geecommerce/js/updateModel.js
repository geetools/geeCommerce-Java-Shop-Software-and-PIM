define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	"use strict";

	/**
	 * Class constructor.
	 */
	function UpdateModel(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof UpdateModel)) {
			throw new TypeError("UpdateModel constructor cannot be called as a function.");
		}

		options = options || {};

		this._id = options._id;
		this._attributes = options.attributes || [];
		this._options = options.options || [];
		this._xOptions = options.xOptions || [];
    this._fields = options.fields || [];
    this._vars = options.vars || [];
    this._optOuts = options.optOuts || [];
    this._merchantIds = options.merchantIds || [];
    this._storeIds = options.storeIds || [];
    this._requestContextIds = options.requestContextIds || [];
    this._saveAsNewCopy = options.saveAsNewCopy || false;
		this._ids = options.ids;
		this._ignoreIds = options.ignoreIds;
		this._searchKeyword = options.searchKeyword;
		this._query = options.query;

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'stringify', 'data', 'variable', 'attr', 'options', 'field', 'optOut', 'id', 'merchantIds', 'storeIds', 'requestContextIds', 'saveAsNewCopy');
	}

	UpdateModel.prototype = {
		constructor : UpdateModel,
		stringify : function() {
			return JSON.stringify(this.data());
		},
		data : function() {
			return {
				_id : this._id,
				fields : this._fields,
				attributes : this._attributes,
				options : this._options,
        xOptions : this._xOptions,
        vars : this._vars,
				optOuts : this._optOuts,
				merchantIds : this._merchantIds,
				storeIds : this._storeIds,
				requestContextIds : this._requestContextIds,
				saveAsNewCopy : this._saveAsNewCopy,
				ids : this._ids,
				ignoreIds : this._ignoreIds,
				searchKeyword : this._searchKeyword,
				query : this._query
			};
		},
        id : function(id) {
            this._id = id;
            return this;
        },
        context : function(contextModel) {
            if(_.isEmpty(contextModel))
                return;

            this._merchantIds = contextModel.merchantIds();
            this._storeIds = contextModel.storeIds();
            this._requestContextIds = contextModel.requestContextIds();
            return this;
        },
        merchantIds : function(merchantIds) {
            this._merchantIds = merchantIds;
            return this;
        },
        storeIds : function(storeIds) {
            this._storeIds = storeIds;
            return this;
        },
        requestContextIds : function(requestContextIds) {
            this._requestContextIds = requestContextIds;
            return this;
        },
        saveAsNewCopy : function(saveAsNewCopy) {
            this._saveAsNewCopy = saveAsNewCopy;
            return this;
        },
				whereIds : function(ids) {
            this._ids = ids;
            return this;
        },
				whereIgnoreIds : function(ignoreIds) {
            this._ignoreIds = ignoreIds;
            return this;
        },
				whereSearchKeyword : function(searchKeyword) {
            this._searchKeyword = searchKeyword;
            return this;
        },
				whereQuery : function(query) {
            this._query = query;
            return this;
        },
		field : function(name, value, isCtxObj) {
			var _isCtxObj = isCtxObj || false;
			var _field = {};

			if(_isCtxObj) {
				_field['ctxObj:' + name] = value;
			} else {
				_field[name] = value;
			}

			this._fields.push(_field);

			return this;
		},
		attr : function(code, value) {
			// Allow resetting of values. Only check none empty arrays for validity.
			if ((_.isArray(value) && _.isEmpty(value)) || gc.ctxobj.isValid(value)) {
				this._attributes.push({
					code : code,
					value : value
				});
			} else {
				console.log('Unable to add value as it is not a valid contextObject.', code, value);
			}

			return this;
		},
		optOut : function(code, optOut) {
			if (gc.ctxobj.isValid(optOut)) {
				this._optOuts.push({
					code : code,
					value : optOut
				});
			} else {
				console.log('Unable to add optOut-value as it is not a valid contextObject.', code, value);
			}

			return this;
		},
		options : function(code, optionIds) {
			if(!_.isArray(optionIds) && !_.isUndefined(optionIds)) {
				optionIds = [optionIds];
			}

			if(!_.isUndefined(optionIds) && !_.isNull(optionIds)) {
				this._options.push({
					code : code,
					optionIds : optionIds
				});
			}

			return this;
		},
		xOptions : function(code, xOptionIds) {
			if (gc.ctxobj.isValid(xOptionIds)) {
				this._xOptions.push({
					code : code,
					xOptionIds : xOptionIds
				});
			} else {
				console.log('Unable to add value as it is not a valid xOptionId object (=contextObject).', code, xOptionIds);
			}

			return this;
		},
        variable : function(name, value, isCtxObj) {
            var _isCtxObj = isCtxObj || false;
            var _var = {};

            if(_isCtxObj) {
                _var['ctxObj:' + name] = value;
            } else {
                _var[name] = value;
            }

            this._vars.push(_var);

            return this;
        }
	};

	return UpdateModel;
});
