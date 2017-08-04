define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        attachContextInfo : function(data, viewModel) {
            var self = this;

            var ctxModel = viewModel.contextModel();

            if (_.isUndefined(ctxModel)) {
                ctxModel = gc.app.newContextModel();
                viewModel.contextModel(ctxModel);
            }

            ctxModel.fromData(data);
        },
        findValue : function(ctxObject, context, data) {
            var self = this;
            var ctxVal = gc.ctxobj.val(ctxObject, undefined, undefined, context);

            if (!_.isUndefined(ctxVal)) {
                if (!_.isUndefined(data)) {
                    if (_.isArray(ctxVal)) {
                        ctxVal = _.findWhere(ko.unwrap(data), {
                            id : ctxVal[0]
                        });
                    } else {
                        ctxVal = _.findWhere(ko.unwrap(data), {
                            id : ctxVal
                        });
                    }
                }

                if (!_.isUndefined(ctxVal) && !_.isUndefined(ctxVal.text) && _.isFunction(ctxVal.text)) {
                    ctxVal = ctxVal.text();
                }
            }

            return ctxVal;
        },
        textValue : function(id, data, lang) {
            var self = this;
            var obj;
            var text;

            if (!_.isUndefined(id)) {
                if (!_.isUndefined(data)) {
                    if (_.isArray(id)) {
                        obj = _.findWhere(ko.unwrap(data), {
                            id : id[0]
                        });
                    } else {
                        obj = _.findWhere(ko.unwrap(data), {
                            id : id
                        });
                    }
                }

                if (!_.isUndefined(obj) && !_.isUndefined(obj.text) && _.isFunction(obj.text)) {
                    text = obj.text(lang);
                }
            }

            return text;
        }
    }
});
