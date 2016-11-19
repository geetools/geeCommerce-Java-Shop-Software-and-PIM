define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        val : function(ctxObject, lang, mode, context) {
            var _val = undefined;
            var _mode = mode || 'strict';

            if (ctxObject instanceof Array && !_.isEmpty(ctxObject)) {

                // --------------------------------------------------------------
                // All modes use this. When in strict-mode, we expect an exact
                // match. No fallback attempts are made - this is the default.
                // --------------------------------------------------------------
                _.some(ctxObject, function(element, index) {
                    if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                        // Merchant scope with language.
                        if (context.scope == 'merchant' && element.l == lang && element.m == context.id) {
                            _val = element.val;
                            return true;
                            // Merchant scope without language.
                        } else if (context.scope == 'merchant' && _.isEmpty(lang) && _.isEmpty(element.l) && element.m == context.id) {
                            _val = element.val;
                            return true;
                            // Store scope with language.
                        } else if (context.scope == 'store' && element.l == lang && element.s == context.id) {
                            _val = element.val;
                            return true;
                            // Store scope without language.
                        } else if (context.scope == 'store' && _.isEmpty(lang) && _.isEmpty(element.l) && element.s == context.id) {
                            _val = element.val;
                            return true;
                            // RequestContext scope with language.
                        } else if (context.scope == 'request_context' && element.l == lang && element.rc == context.id) {
                            _val = element.val;
                            return true;
                            // Store scope without language.
                        } else if (context.scope == 'request_context' && _.isEmpty(lang) && _.isEmpty(element.l) && element.rc == context.id) {
                            _val = element.val;
                            return true;
                        }
                    } else {
                        if (element.l == lang && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                            _val = element.val;
                            return true;
                            // Without language.
                        } else if (_.isEmpty(lang) && _.isEmpty(element.l) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                            _val = element.val;
                            return true;
                        }
                    }
                });

                // --------------------------------------------------------------
                // In none-strict modes, we also attempt other languages,
                // starting with the default language.
                // --------------------------------------------------------------
                if ((_.isUndefined(_val) || _.isNull(_val)) && _mode != 'strict') {
                    if ((_.isUndefined(_val) || _.isNull(_val)) && (_mode == 'closest' || _mode == 'any')) {
                        _.some(ctxObject, function(element, index) {
                            if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                                // Merchant scope with default language.
                                if (context.scope == 'merchant' && element.l == gc.app.defaultLanguage() && element.m == context.id) {
                                    _val = element.val;
                                    return true;
                                    // Store scope with default language.
                                } else if (context.scope == 'store' && element.l == gc.app.defaultLanguage() && element.s == context.id) {
                                    _val = element.val;
                                    return true;
                                    // RequestContext scope default with language.
                                } else if (context.scope == 'request_context' && element.l == gc.app.defaultLanguage() && element.rc == context.id) {
                                    _val = element.val;
                                    return true;
                                }
                            } else {
                                if (element.l == gc.app.defaultLanguage() && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                    _val = element.val;
                                    return true;
                                }
                            }
                        });
                    }

                    // --------------------------------------------------------------
                    // Secondary language.
                    // --------------------------------------------------------------
                    if ((_.isUndefined(_val) || _.isNull(_val)) && (_mode == 'closest' || _mode == 'any')) {
                        _.some(ctxObject, function(element, index) {
                            if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                                // Merchant scope with secondary language.
                                if (context.scope == 'merchant' && element.l == gc.app.secondaryLanguage() && element.m == context.id) {
                                    _val = element.val;
                                    return true;
                                    // Store scope with secondary language.
                                } else if (context.scope == 'store' && element.l == gc.app.secondaryLanguage() && element.s == context.id) {
                                    _val = element.val;
                                    return true;
                                    // RequestContext scope with secondary language.
                                } else if (context.scope == 'request_context' && element.l == gc.app.secondaryLanguage() && element.rc == context.id) {
                                    _val = element.val;
                                    return true;
                                }
                            } else {
                                if (element.l == gc.app.secondaryLanguage() && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                    _val = element.val;
                                    return true;
                                }
                            }
                        });
                    }

                    // --------------------------------------------------------------
                    // Global (without language specified).
                    // --------------------------------------------------------------
                    if ((_.isUndefined(_val) || _.isNull(_val)) && (_mode == 'closest' || _mode == 'any')) {
                        _.some(ctxObject, function(element, index) {
                            if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                                // Merchant scope without language.
                                if (context.scope == 'merchant' && _.isEmpty(element.l) && element.m == context.id) {
                                    _val = element.val;
                                    return true;
                                    // Store scope without language.
                                } else if (context.scope == 'store' && _.isEmpty(element.l) && element.s == context.id) {
                                    _val = element.val;
                                    return true;
                                    // RequestContext scope with language.
                                } else if (context.scope == 'request_context' && _.isEmpty(element.l) && element.rc == context.id) {
                                    _val = element.val;
                                    return true;
                                }
                            } else {
                                if (_.isEmpty(element.l) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                    _val = element.val;
                                    return true;
                                }
                            }
                        });
                    }

                    // --------------------------------------------------------------
                    // Global (without store specified).
                    // --------------------------------------------------------------
                    if ((_.isUndefined(_val) || _.isNull(_val)) && (_mode == 'closest' || _mode == 'any')) {
                        _.some(ctxObject, function(element, index) {
                            if (_.isEmpty(element.l) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                _val = element.val;
                                return true;
                            }
                        });
                    }

                    // --------------------------------------------------------------
                    // If we have still not found anything in 'any' mode,
                    // we simply return the first value that exists.
                    // --------------------------------------------------------------
                    if ((_.isUndefined(_val) || _.isNull(_val)) && _mode == 'any') {
                        _.some(ctxObject, function(element, index) {
                            if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                                // Merchant scope.
                                if (context.scope == 'merchant' && !_.isUndefined(element.val) && !_.isNull(element.val) && element.m == context.id) {
                                    _val = element.val;
                                    return true;
                                    // Store scope.
                                } else if (context.scope == 'store' && !_.isUndefined(element.val) && !_.isNull(element.val) && element.s == context.id) {
                                    _val = element.val;
                                    return true;
                                    // RequestContext scope.
                                } else if (context.scope == 'request_context' && !_.isUndefined(element.val) && !_.isNull(element.val) && element.rc == context.id) {
                                    _val = element.val;
                                    return true;
                                }
                            } else {
                                if (!_.isUndefined(element.val) && !_.isNull(element.val) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                    _val = element.val;
                                    return true;
                                }
                            }
                        });
                    }

                    if ((_.isUndefined(_val) || _.isNull(_val)) && _mode == 'any') {
                        _.some(ctxObject, function(element, index) {
                            if (!_.isUndefined(element.val) && !_.isNull(element.val)) {
                                _val = element.val;
                                return true;
                            }
                        });
                    }
                }
            }

            return _val;
        },
        closest : function(ctxObject, lang, context) {
            var self = this;
            return self.val(ctxObject, lang, 'closest', context);
        },
        any : function(ctxObject, lang, context) {
            var self = this;
            return self.val(ctxObject, lang, 'any', context);
        },
        plain : function(ctxObject) {
            var self = this;
            return self.val(ctxObject);
        },
        global : function(ctxObject) {
            var self = this;
            return self.val(ctxObject);
        },
        obj : function(ctxObject, lang, context) {
            var obj = null;

            if (ctxObject instanceof Array && !_.isEmpty(ctxObject)) {
                _.some(ctxObject, function(element, index) {
                    if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                        // Merchant scope with language.
                        if (context.scope == 'merchant' && element.l == lang && element.m == context.id) {
                            obj = element;
                            return true;
                            // Merchant scope without language.
                        } else if (context.scope == 'merchant' && _.isEmpty(lang) && _.isEmpty(element.l) && element.m == context.id) {
                            obj = element;
                            return true;
                            // Store scope with language.
                        } else if (context.scope == 'store' && element.l == lang && element.s == context.id) {
                            obj = element;
                            return true;
                            // Store scope without language.
                        } else if (context.scope == 'store' && _.isEmpty(lang) && _.isEmpty(element.l) && element.s == context.id) {
                            obj = element;
                            return true;
                            // RequestContext scope with language.
                        } else if (context.scope == 'request_context' && element.l == lang && element.rc == context.id) {
                            obj = element;
                            return true;
                            // Store scope without language.
                        } else if (context.scope == 'request_context' && _.isEmpty(lang) && _.isEmpty(element.l) && element.rc == context.id) {
                            obj = element;
                            return true;
                        }
                    } else {
                        if (element.l == lang && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                            obj = element;
                            return true;
                            // Without language.
                        } else if (_.isEmpty(lang) && _.isEmpty(element.l) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                            obj = element;
                            return true;
                        }
                    }
                });
            }

            return !_.isEmpty(obj) ? obj : undefined;
        },
        filter : function(ctxObject, lang, context, regexOrVal) {
            var objArr = [];

            if (ctxObject instanceof Array && !_.isEmpty(ctxObject)) {
                _.each(ctxObject, function(element, index) {
                    if (regexOrVal === undefined || ((regexOrVal instanceof RegExp) && regexOrVal.test(element.val)) || (!(regexOrVal instanceof RegExp) && regexOrVal === element.val)) {

                        if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                            // Merchant scope with language.
                            if (context.scope == 'merchant' && element.l == lang && element.m == context.id) {
                                objArr.push(element);
                                // Merchant scope without language.
                            } else if (context.scope == 'merchant' && _.isEmpty(lang) && _.isEmpty(element.l) && element.m == context.id) {
                                objArr.push(element);
                                // Store scope with language.
                            } else if (context.scope == 'store' && element.l == lang && element.s == context.id) {
                                objArr.push(element);
                                // Store scope without language.
                            } else if (context.scope == 'store' && _.isEmpty(lang) && _.isEmpty(element.l) && element.s == context.id) {
                                objArr.push(element);
                                // RequestContext scope with language.
                            } else if (context.scope == 'request_context' && element.l == lang && element.rc == context.id) {
                                objArr.push(element);
                                // Store scope without language.
                            } else if (context.scope == 'request_context' && _.isEmpty(lang) && _.isEmpty(element.l) && element.rc == context.id) {
                                objArr.push(element);
                            }
                        } else {
                            if (element.l == lang && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                objArr.push(element);
                                // Without language.
                            } else if (_.isEmpty(lang) && _.isEmpty(element.l) && _.isEmpty(element.m) && _.isEmpty(element.s) && _.isEmpty(element.rc)) {
                                objArr.push(element);
                            }
                        }
                    }
                });
            }

            return objArr;
        },
        set : function(ctxObject, lang, value, context) {
            var self = this;
            var ctxObject = ctxObject || [];
            var ctxElement = self.obj(ctxObject, lang, context);

            if (_.isUndefined(ctxElement) || _.isNull(ctxElement)) {
                ctxElement = {};
                ctxElement.val = value;

                if (!_.isEmpty(lang)) {
                    ctxElement.l = lang;
                }

                if (!_.isUndefined(context) && !_.isUndefined(context.id) && !_.isUndefined(context.scope)) {
                    if (context.scope == 'merchant') {
                        ctxElement.m = context.id;
                    } else if (context.scope == 'store') {
                        ctxElement.s = context.id;
                    } else if (context.scope == 'request_context') {
                        ctxElement.rc = context.id;
                    }
                }

                ctxObject.push(ctxElement);
            } else {
                ctxElement.val = value;
            }
        },
        unset : function(ctxObject, lang, value, context) {
            var self = this;
            var ctxObject = ctxObject || [];
            var ctxElement = self.obj(ctxObject, lang, context);

            if (!_.isUndefined(ctxElement) && !_.isNull(ctxElement)) {
                var idx = _.findIndex(ctxObject, ctxElement);
                ctxObject.splice(idx, 1);
            }
        },
        unsetWhere : function(ctxObject, lang, value, context, regexOrVal) {
            var self = this;
            var ctxObject = ctxObject || [];
            var ctxElements = self.filter(ctxObject, lang, context, regexOrVal);

            for (var i = 0; i < ctxElements.length; i++) {
                var ctxElement = ctxElements[i];

                if (!_.isUndefined(ctxElement) && !_.isNull(ctxElement)) {
                    var idx = _.findIndex(ctxObject, ctxElement);
                    ctxObject.splice(idx, 1);
                }
            }
        },
        clone : function(ctxObject) {
            var self = this;
            var clone = [];

            if (_.isArray(ctxObject) && !_.isEmpty(ctxObject)) {
                for (var i = 0; i < ctxObject.length; i++) {
                    var obj = ctxObject[i];
                    var objCopy = {};

                    if (obj.hasOwnProperty('m')) {
                        objCopy.m = obj.m;
                    }

                    if (obj.hasOwnProperty('s')) {
                        objCopy.s = obj.s;
                    }

                    if (obj.hasOwnProperty('l')) {
                        objCopy.l = obj.l;
                    }

                    if (obj.hasOwnProperty('c')) {
                        objCopy.c = obj.c;
                    }

                    if (obj.hasOwnProperty('v')) {
                        objCopy.v = obj.v;
                    }

                    if (obj.hasOwnProperty('rc')) {
                        objCopy.rc = obj.rc;
                    }

                    if (obj.hasOwnProperty('val')) {
                        objCopy.val = obj.val;
                    }

                    clone.push(objCopy);
                }
            }

            return clone;
        },
        equals : function(ctxObject1, ctxObject2) {
            var self = this;

            if (_.isEmpty(ctxObject1) && _.isEmpty(ctxObject2))
                return true;

            if ((_.isEmpty(ctxObject1) && !_.isEmpty(ctxObject2)) || (_.isEmpty(ctxObject2) && !_.isEmpty(ctxObject1)) || ctxObject1.length != ctxObject2.length)
                return false;

            if ((_.isArray(ctxObject1) && !_.isArray(ctxObject2)) || (_.isArray(ctxObject2) && !_.isArray(ctxObject1)))
                return false;

            var isEqual = true;

            for (var i = 0; i < ctxObject1.length; i++) {
                var obj1 = ctxObject1[i];
                var val1 = obj1.val;

                if (!_.isEmpty(obj1.m)) {
                    var obj2 = _.findWhere(ctxObject2, {
                        m : obj1.m
                    });

                    if (obj2 === undefined) {
                        isEqual = false;
                        break;
                    }

                    var val2 = obj2.val;

                    if (val1 !== val2) {
                        isEqual = false;
                        break;
                    }
                } else if (!_.isEmpty(obj1.s)) {
                    var obj2 = _.findWhere(ctxObject2, {
                        s : obj1.s
                    });

                    if (obj2 === undefined) {
                        isEqual = false;
                        break;
                    }

                    var val2 = obj2.val;

                    if (val1 !== val2) {
                        isEqual = false;
                        break;
                    }
                } else if (!_.isEmpty(obj1.rc)) {
                    var obj2 = _.findWhere(ctxObject2, {
                        rc : obj1.rc
                    });

                    if (obj2 === undefined) {
                        isEqual = false;
                        break;
                    }

                    var val2 = obj2.val;

                    if (val1 !== val2) {
                        isEqual = false;
                        break;
                    }
                } else {
                    var val2 = self.global(ctxObject2);
                    if (val1 !== val2) {
                        isEqual = false;
                        break;
                    }
                }
            }

            return isEqual;
        },
        reduce : function(ctxObjects, keyProperty, valueProperty, lang, dropEmptyValues, aliasMap) {
            var self = this;
            var reducedObjects = [];
            dropEmptyValues = dropEmptyValues || false;
            aliasMap = aliasMap || [];

            _.each(ctxObjects, function(obj) {
                var _option = {};
                var _val = self.val(obj[valueProperty], lang);

                if (!(dropEmptyValues && _.isEmpty(_val))) {
                    _option[_.isEmpty(aliasMap[keyProperty]) ? keyProperty : aliasMap[keyProperty]] = obj[keyProperty];
                    _option[_.isEmpty(aliasMap[valueProperty]) ? valueProperty : aliasMap[valueProperty]] = self.val(obj[valueProperty], lang);

                    reducedObjects.push(_option);
                }
            });

            return reducedObjects;
        },
        /* Optimized version of decorate (no auto searching of context objects). */
        enhance : function(rootObjects, includeProperties, mode, langCallback) {
            var self = this;
            var mode = mode || 'strict';

            if (_.isEmpty(rootObjects) || _.isEmpty(includeProperties))
                return;

            var _rootObjects = [];

            if (_.isArray(rootObjects)) {
                _rootObjects = rootObjects;
            } else if (_.isObject(rootObjects)) {
                _rootObjects.push(rootObjects);
            } else {
                return;
            }

            // Don't bother enhancing again if it has already happened.
            var enhanced = _rootObjects.enhanced || {};
            if (enhanced.total === _rootObjects.length) {
                return;
            }

            _.each(_rootObjects, function(obj) {
                for ( var prop in obj) {
                    if (obj.hasOwnProperty(prop) && _.contains(includeProperties, prop)) {
                        var _objProp = obj[prop];
                        _objProp.i18n = function(lang) {
                            if (lang === undefined) {
                                return self.val(_objProp, !_.isUndefined(langCallback) && _.isFunction(langCallback) ? langCallback() : gc.app.currentLang(), mode);
                            } else {
                                return self.val(_objProp, !_.isUndefined(langCallback) && _.isFunction(langCallback) ? langCallback() : lang, mode);
                            }
                        };
                    }
                }
            });

            _rootObjects.enhanced = {
                total : _rootObjects.length
            };
        },
        decorate : function(rootObjects, mode, langCallback, includeProperties, autoSearchEnabled) {
            var self = this;
            var includeProperties = includeProperties || [];
            var autoSearchEnabled = autoSearchEnabled == undefined ? true : autoSearchEnabled;

            if (_.isNull(rootObjects) || _.isUndefined(rootObjects))
                return;

            var _rootObjects = [];

            if (_.isArray(rootObjects)) {
                _rootObjects = rootObjects;
            } else if (_.isObject(rootObjects)) {
                _rootObjects.push(rootObjects);
            } else {
                return;
            }

            _.each(_rootObjects, function(obj) {
                for ( var prop in obj) {
                    if (obj.hasOwnProperty(prop)) {
                        if ((autoSearchEnabled && self.isValid(obj[prop], false)) || _.contains(includeProperties, prop)) {
                            var _objProp = obj[prop];
                            _objProp.i18n = function(lang) {
                                if (lang === undefined) {
                                    return self.val(_objProp, !_.isUndefined(langCallback) && _.isFunction(langCallback) ? langCallback() : gc.app.currentLang(), mode || 'strict');
                                } else {
                                    return self.val(_objProp, !_.isUndefined(langCallback) && _.isFunction(langCallback) ? langCallback() : lang, mode || 'strict');
                                }
                            };
                        }
                    }
                }
            });
        },
        isValid : function(ctxObject, printError) {
            var self = this;
            var printError = printError == undefined ? true : printError;

            if (_.isEmpty(ctxObject) || !(ctxObject instanceof Array)) {
                if (printError)
                    console.log('ctxObject is not a valid array.', ctxObject);

                return false;
            }

            var _valid = true;

            _.some(ctxObject, function(element, index) {

                // context-element-object cannot be empty and must be an object.
                if (_.isEmpty(element) || !_.isObject(element)) {
                    if (printError)
                        console.log('Element in ctxObject-array is either empty or not a valid object.', element);

                    _valid = false;
                    // break out of ._some loop.
                    return true;
                }

                // Value must always be set.
                if (_.isUndefined(element.val) || _.isNull(element.val)) {
                    if (printError)
                        console.log('Property "val" not set in element of ctxObject-array.', element);

                    _valid = false;
                    // break out of ._some loop.
                    return true;
                }

                for ( var prop in element) {
                    if (element.hasOwnProperty(prop)) {
                        if (prop != 'm' && prop != 's' && prop != 'l' && prop != 'c' && prop != 'v' && prop != 'rc' && prop != 'val') {
                            if (printError)
                                console.log('Found invalid property in element of ctxObject-array.', prop, element);

                            _valid = false;
                            break;
                        }

                        if ((prop == 'm' || prop == 's' || prop == 'v' || prop == 'rc') && (_.isUndefined(element[prop]) || _.isNull(element[prop]) || !/[0-9]/.test(element[prop]) || element[prop] < 1)) {

                            if (printError)
                                console.log('Invalid value found for property [m, s, v, rc]. Must be a number greater than 0.', prop, element);

                            _valid = false;
                            break;
                        } else if ((prop == 'l' || prop == 'c') && (_.isEmpty(element[prop]) || !_.isString(element[prop]) || element[prop].length != 2)) {
                            if (printError)
                                console.log('Invalid value found for property [c, l]. Must be a string containg a 2-character iso-code.', prop, element);

                            _valid = false;
                            break;
                        } else {
                            // TODO check validity of value.
                        }
                    }

                    // break out of ._some loop.
                    if (!_valid)
                        return true;
                }
            });

            return _valid;
        }
    };
});
