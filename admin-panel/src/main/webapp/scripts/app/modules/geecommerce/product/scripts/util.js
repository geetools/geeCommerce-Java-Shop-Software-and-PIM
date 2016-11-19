define([ 'knockout', 'gc/gc', 'speakingurl' ], function(ko, gc, getSlug) {
    return {
        getProductStatusIcon : function(articleStatus, articleStatuses) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');
            
            var reqCtxCount = 0;
            var reqCtxApprovedCount = 0;

            for (var m = 0; m < availableContexts.length; m++) {
                var isApprovedForOnline = false;

                var merchant = availableContexts[m];
                var globalProductStatusId;
                var merchantProductStatusId;
                var storeProductStatusId;
                var reqCtxProductStatusId;

                if (merchant.scope == 'global') {
                    globalProductStatusId = gc.ctxobj.global(articleStatus);
                    
                    if (!_.isEmpty(globalProductStatusId)) {
                        var statusObj = _.findWhere(articleStatuses, {
                            id : globalProductStatusId
                        });
                        
                        if (!_.isUndefined(statusObj)) {
                            var statusCode = statusObj.code;
                            if (statusCode == 'approved') {
                                isApprovedForOnline = true;
                            } else {
                                isApprovedForOnline = false;
                            }
                        }
                    } else {
                        isApprovedForOnline = false;
                    }

                    continue;
                }

                merchantOnlineStatusId = gc.ctxobj.val(articleStatus, undefined, undefined, merchant);

                if (_.isUndefined(merchantOnlineStatusId)) {
                    merchantOnlineStatusId = globalProductStatusId;
                }

                if (!_.isEmpty(merchantOnlineStatusId)) {
                    var statusObj = _.findWhere(articleStatuses, {
                        id : merchantOnlineStatusId
                    });

                    if (!_.isUndefined(statusObj)) {
                        var statusCode = statusObj.code;

                        if (statusCode == 'approved') {
                            isApprovedForOnline = true;
                        } else {
                            isApprovedForOnline = false;
                        }
                    }
                } else {
                    isApprovedForOnline = false;
                }

                if (merchant.stores) {
                    for (var s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];
                        storeOnlineStatusId = gc.ctxobj.val(articleStatus, undefined, undefined, store);

                        if (_.isUndefined(storeOnlineStatusId)) {
                            storeOnlineStatusId = merchantOnlineStatusId;
                        }

                        if (!_.isEmpty(storeOnlineStatusId)) {
                            var statusObj = _.findWhere(articleStatuses, {
                                id : storeOnlineStatusId
                            });

                            if (!_.isUndefined(statusObj)) {
                                var statusCode = statusObj.code;

                                if (statusCode == 'approved') {
                                    isApprovedForOnline = true;
                                } else {
                                    isApprovedForOnline = false;
                                }
                            }
                        } else {
                            isApprovedForOnline = false;
                        }

                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];
                                reqCtxOnlineStatusId = gc.ctxobj.val(articleStatus, undefined, undefined, reqCtx);
                                reqCtxCount++;
                                
                                if (_.isUndefined(reqCtxOnlineStatusId)) {
                                    reqCtxOnlineStatusId = storeOnlineStatusId;
                                }

                                if (!_.isEmpty(reqCtxOnlineStatusId)) {
                                    var statusObj = _.findWhere(articleStatuses, {
                                        id : reqCtxOnlineStatusId
                                    });

                                    if (!_.isUndefined(statusObj)) {
                                        var statusCode = statusObj.code;

                                        if (statusCode == 'approved') {
                                            isApprovedForOnline = true;
                                        } else {
                                            isApprovedForOnline = false;
                                        }
                                    }
                                } else {
                                    isApprovedForOnline = false;
                                }

                                if (isApprovedForOnline)
                                    reqCtxApprovedCount++;
                            }
                        }
                    }
                }
            }
            
            if (reqCtxApprovedCount === 0) {
                return "product-status-cross fa fa-square-o";
            } else if (reqCtxCount > reqCtxApprovedCount) {
                return "product-status-tick-light fa fa-check-square-o";
            } else if (reqCtxCount === reqCtxApprovedCount) {
                return "product-status-tick fa fa-check-square-o";
            }
        },
        getProductDescriptionStatusIcon : function(descriptionStatus, descriptionStatuses) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            var reqCtxCount = 0;
            var reqCtxCompleteCount = 0;

            for (var m = 0; m < availableContexts.length; m++) {
                var descStatus = 'not_started';

                var merchant = availableContexts[m];
                var globalProductStatusId;
                var merchantProductStatusId;
                var storeProductStatusId;
                var reqCtxProductStatusId;

                if (merchant.scope == 'global') {
                    globalProductStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, merchant);

                    if (!_.isEmpty(globalProductStatusId)) {
                        var statusObj = _.findWhere(descriptionStatuses, {
                            id : globalProductStatusId[0]
                        });

                        if (!_.isUndefined(statusObj)) {
                            descStatus = statusObj.code;
                        } else {
                            descStatus = 'not_started';
                        }
                    } else {
                        descStatus = 'not_started';
                    }

                    continue;
                }

                merchantOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, merchant);

                if (_.isUndefined(merchantOnlineStatusId)) {
                    merchantOnlineStatusId = globalProductStatusId;
                }

                if (!_.isEmpty(merchantOnlineStatusId)) {
                    var statusObj = _.findWhere(descriptionStatuses, {
                        id : merchantOnlineStatusId[0]
                    });

                    if (!_.isUndefined(statusObj)) {
                        descStatus = statusObj.code;
                    } else {
                        descStatus = 'not_started';
                    }
                } else {
                    descStatus = 'not_started';
                }

                if (merchant.stores) {
                    for (var s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];
                        storeOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, store);

                        if (_.isUndefined(storeOnlineStatusId)) {
                            storeOnlineStatusId = merchantOnlineStatusId;
                        }

                        if (!_.isEmpty(storeOnlineStatusId)) {
                            var statusObj = _.findWhere(descriptionStatuses, {
                                id : storeOnlineStatusId[0]
                            });

                            if (!_.isUndefined(statusObj)) {
                                descStatus = statusObj.code;
                            } else {
                                descStatus = 'not_started';
                            }
                        } else {
                            descStatus = 'not_started';
                        }

                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];
                                reqCtxOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, reqCtx);
                                reqCtxCount++;

                                if (_.isUndefined(reqCtxOnlineStatusId)) {
                                    reqCtxOnlineStatusId = storeOnlineStatusId;
                                }

                                if (!_.isEmpty(reqCtxOnlineStatusId)) {
                                    var statusObj = _.findWhere(descriptionStatuses, {
                                        id : reqCtxOnlineStatusId[0]
                                    });

                                    if (!_.isUndefined(statusObj)) {
                                        descStatus = statusObj.code;
                                    } else {
                                        descStatus = 'not_started';
                                    }
                                } else {
                                    descStatus = 'not_started';
                                }

                                if (descStatus == 'complete')
                                    reqCtxCompleteCount++;
                            }
                        }
                    }
                }
            }

            if (reqCtxCompleteCount === 0 && descStatus == 'in_progress') {
                return "product-status-in-progress fa fa-pencil-square-o";
            } else if (reqCtxCompleteCount === 0 && descStatus == 'not_started') {
                return "product-status-cross fa fa-square-o";
            } else if (reqCtxCount > reqCtxCompleteCount) {
                return "product-status-tick-light fa fa-check-square-o";
            } else if (reqCtxCount === reqCtxCompleteCount) {
                return "product-status-tick fa fa-check-square-o";
            }
        },
        getProductDescriptionStatusLabel : function(descriptionStatus, descriptionStatuses) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            var reqCtxCount = 0;
            var reqCtxCompleteCount = 0;

            for (var m = 0; m < availableContexts.length; m++) {
                var descStatus = '-';
                var descStatusCode = 'not_started';

                var merchant = availableContexts[m];
                var globalProductStatusId;
                var merchantProductStatusId;
                var storeProductStatusId;
                var reqCtxProductStatusId;

                if (merchant.scope == 'global') {
                    globalProductStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, merchant);

                    if (!_.isEmpty(globalProductStatusId)) {
                        var statusObj = _.findWhere(descriptionStatuses, {
                            id : globalProductStatusId[0]
                        });

                        if (!_.isUndefined(statusObj)) {
                            descStatus = ko.isObservable(statusObj.text) || _.isFunction(statusObj.text) ? statusObj.text() : statusObj.text;
                            descStatusCode = statusObj.code;
                        } else {
                            descStatus = '-';
                            descStatusCode = 'not_started';
                        }
                    } else {
                        descStatus = '-';
                        descStatusCode = 'not_started';
                    }

                    continue;
                }

                merchantOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, merchant);

                if (_.isUndefined(merchantOnlineStatusId)) {
                    merchantOnlineStatusId = globalProductStatusId;
                }

                if (!_.isEmpty(merchantOnlineStatusId)) {
                    var statusObj = _.findWhere(descriptionStatuses, {
                        id : merchantOnlineStatusId[0]
                    });

                    if (!_.isUndefined(statusObj)) {
                        descStatus = ko.isObservable(statusObj.text) || _.isFunction(statusObj.text) ? statusObj.text() : statusObj.text;
                        descStatusCode = statusObj.code;
                    } else {
                        descStatus = '-';
                        descStatusCode = 'not_started';
                    }
                } else {
                    descStatus = '-';
                    descStatusCode = 'not_started';
                }

                if (merchant.stores) {
                    for (var s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];
                        storeOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, store);

                        if (_.isUndefined(storeOnlineStatusId)) {
                            storeOnlineStatusId = merchantOnlineStatusId;
                        }

                        if (!_.isEmpty(storeOnlineStatusId)) {
                            var statusObj = _.findWhere(descriptionStatuses, {
                                id : storeOnlineStatusId[0]
                            });

                            if (!_.isUndefined(statusObj)) {
                                descStatus = ko.isObservable(statusObj.text) || _.isFunction(statusObj.text) ? statusObj.text() : statusObj.text;
                                descStatusCode = statusObj.code;
                            } else {
                                descStatus = '-';
                                descStatusCode = 'not_started';
                            }
                        } else {
                            descStatus = '-';
                            descStatusCode = 'not_started';
                        }

                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];
                                reqCtxOnlineStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, reqCtx);
                                reqCtxCount++;

                                if (_.isUndefined(reqCtxOnlineStatusId)) {
                                    reqCtxOnlineStatusId = storeOnlineStatusId;
                                }

                                if (!_.isEmpty(reqCtxOnlineStatusId)) {
                                    var statusObj = _.findWhere(descriptionStatuses, {
                                        id : reqCtxOnlineStatusId[0]
                                    });

                                    if (!_.isUndefined(statusObj)) {
                                        descStatus = ko.isObservable(statusObj.text) || _.isFunction(statusObj.text) ? statusObj.text() : statusObj.text;
                                        descStatusCode = statusObj.code;
                                    } else {
                                        descStatus = '-';
                                        descStatusCode = 'not_started';
                                    }
                                } else {
                                    descStatus = '-';
                                    descStatusCode = 'not_started';
                                }

                                if (descStatusCode == 'complete')
                                    reqCtxCompleteCount++;
                            }
                        }
                    }
                }
            }

            return descStatus;
        },
        getProductImageStatusIcon : function(imageStatus) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            var imageStatusCode = 'not_started';

            if (!_.isEmpty(imageStatus)) {
                imageStatusCode = gc.ctxobj.global(imageStatus[0].label, gc.app.currentUserLang());
            }

            if (imageStatusCode == 'in_progress') {
                return "product-status-in-progress fa fa-pencil-square-o";
            } else if (imageStatusCode == 'complete') {
                return "product-status-tick fa fa-check-square-o";
            } else {
                return "product-status-cross fa fa-square-o";
            }
        },
        getProductImageStatusLabel : function(imageStatus) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            if (!_.isEmpty(imageStatus)) {
                return gc.ctxobj.closest(imageStatus[0].label, gc.app.currentUserLang());
            }
        },
        getProductVisibleIcon : function(visibleStatus) {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            var reqCtxCount = 0;
            var reqCtxVisibleCount = 0;

            for (var m = 0; m < availableContexts.length; m++) {
                var isVisible = false;

                var merchant = availableContexts[m];
                var globalProductVisible;
                var merchantProductVisible;
                var storeProductVisible;
                var reqCtxProductVisible;

                if (merchant.scope == 'global') {
                    globalProductVisible = gc.ctxobj.val(visibleStatus, undefined, undefined, merchant);

                    if (globalProductVisible && globalProductVisible === true) {
                        isVisible = true;
                    } else {
                        isVisible = false;
                    }

                    continue;
                }

                merchantProductVisible = gc.ctxobj.val(visibleStatus, undefined, undefined, merchant);

                if (merchantProductVisible === undefined) {
                    merchantProductVisible = globalProductVisible;
                }

                if (merchantProductVisible && merchantProductVisible === true) {
                    isVisible = true;
                } else {
                    isVisible = false;
                }

                if (merchant.stores) {
                    for (var s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];
                        storeProductVisible = gc.ctxobj.val(visibleStatus, undefined, undefined, store);

                        if (storeProductVisible === undefined) {
                            storeProductVisible = merchantProductVisible;
                        }

                        if (storeProductVisible && storeProductVisible === true) {
                            isVisible = true;
                        } else {
                            isVisible = false;
                        }

                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];
                                reqCtxProductVisible = gc.ctxobj.val(visibleStatus, undefined, undefined, reqCtx);
                                reqCtxCount++;

                                if (reqCtxProductVisible === undefined) {
                                    reqCtxProductVisible = storeProductVisible;
                                }

                                if (reqCtxProductVisible && reqCtxProductVisible === true) {
                                    isVisible = true;
                                } else {
                                    isVisible = false;
                                }

                                if (isVisible)
                                    reqCtxVisibleCount++;
                            }
                        }
                    }
                }
            }

            if (reqCtxVisibleCount === 0) {
                return "product-status-cross fa fa-square-o";
            } else if (reqCtxCount > reqCtxVisibleCount) {
                return "product-status-tick-light fa fa-check-square-o";
            } else if (reqCtxCount === reqCtxVisibleCount) {
                return "product-status-tick fa fa-check-square-o";
            }
        },
        newURI : function(lang, category, brand, name) {
            var self = this;

            if (!_.isEmpty(category) && !_.isEmpty(brand) && !_.isEmpty(name)) {
                var uri = '';

                if (name.indexOf(category) === -1) {
                    uri += category + '-';
                }

                if (name.indexOf(brand) === -1 && category.indexOf(brand) === -1) {
                    uri += brand + '-';
                }

                uri += name;

                var baseURI = gc.app.message('app:modules.product.baseFriendlyURI', lang);

                if (!_.isEmpty(baseURI)) {
                    uri = baseURI + '/' + getSlug(uri) + '.html';
                } else {
                    uri = '/' + getSlug(uri) + '.html';
                }

                return uri;
            }
        }
    }
});