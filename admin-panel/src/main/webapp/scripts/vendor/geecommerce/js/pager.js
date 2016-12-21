define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    "use strict";

    /**
     * Class constructor.
     */
    function Pager(options) {

        var self = this;

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof Pager)) {
            throw new TypeError("Pager constructor cannot be called as a function.");
        }

        // console.log(options);
        options = options || {};

        console.log('@@@@@@@@@@@@@@@@@@@@AAA options: ', options);
        
        this._url = options.url;
        this._searchUrl = options.searchUrl;
        this._queryUrl = options.queryUrl;
        this._mapping = options.mapping;
        this._fields = options.fields;
        this._attributes = options.attributes;
        this._defaultFilter = options.filter;
        this._filter = {};
        this._sort = options.sort;
        this._sortDirection = 'ASC', this._offset = 0;
        this._showNumPages = options.showNumPages || 10;
        this._currentPage = 1;
        this._limit = options.limit || 25;
        this._totalCount = options.totalCount || 0;
        this._getArray = options.getArray;

        if (typeof options.onload === "function") {
            this._onload = options.onload;
        }

        this._onerror = options.onerror;
        this._oncomplete = options.oncomplete;
        this._isSearch = false;
        this._isQuery = false;
        this._busy = false;
        this._isInitialized = false;
        this._isMultiContext = options.multiContext;
        this._cookieName = options.cookieName;

        this.columns = ko.observableArray();
        this.data = ko.observableArray();
        this.pages = ko.observableArray();

        this.searchKeyword = ko.observable().extend({
            rateLimit : {
                method : "notifyWhenChangesStop",
                timeout : 400
            }
        });

        this.query = ko.observable();
        this._query = "";

        this.limitOptions = ko.observableArray([ {
            value : 10,
            label : "10"
        }, {
            value : 25,
            label : "25"
        }, {
            value : 50,
            label : "50"
        }, {
            value : 100,
            label : "100"
        }, {
            value : 250,
            label : "250"
        }, {
            value : 1000,
            label : "1000"
        }, {
            value : 2000,
            label : "2000"
        }, {
            value : 5000,
            label : "5000"
        } ]);

        this.registerObservableColumns(options.columns);

        // Toggle selector for loading image.
        this.toggleSelector1 = '.pager-toggle-1';
        this.toggleSelector2 = '.pager-toggle-2';

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'registerObservableColumns', 'activateSubscribers', 'columnValue', 'sort', 'applyFilter', 'page', 'refresh', 'load', 'removeData', 'getData', 'setDefaultFilter', 'resetDefaultFilter', 'loadState', 'saveState');

        console.log('@@@@@@@@@@@@@@@@@@@@AAA options: ', options, this._limit);
        
        self.loadState();

        console.log('@@@@@@@@@@@@@@@@@@@@AAA options: ', options, this._limit);
        
        this.limit = ko.observable(this._limit);

        
        console.log('@@@@@@@@@@@@@@@@@@@@AAA options: ', options, this.limit());
        
        
    }

    Pager.prototype = {
        constructor : Pager,
        // -----------------------------------------------------------------------------------------
        // Event-handler for processing page-clicks. Loads new data from rest-API.
        // -----------------------------------------------------------------------------------------
        page : function(page, event) {
            var self = this;

            this._currentPage = parseInt(page.num);
            this._offset = (this._currentPage <= 0 ? 0 : this._currentPage - 1) * this._limit;

            this._setPages();

            self.saveState();

            return this.load();
        },
        isActive : function(page) {
            return page.num == this._currentPage;
        },
        registerObservableColumns : function(columns) {
            var self = this;

            var hasStoreColumn = false;

            var observableColumns = [];
            _.each(columns, function(column) {
                if (column.reloadOnStoreChange === true)
                    hasStoreColumn = true;

                observableColumns.push({
                    name : column.name,
                    label : column.label,
                    cookieKey : column.cookieKey,
                    combined : column.combined,
                    type : column.type,
                    useRegexp : column.useRegexp,
                    wildcard : _.has(column, 'wildcard') ? column.wildcard : true,
                    date : _.has(column, 'isDate') ? column.date : false,
                    selectOptions : column.selectOptions || [],
                    value : ko.observable().extend({
                        rateLimit : {
                            method : "notifyWhenChangesStop",
                            timeout : 400
                        }
                    }),
                    sort : ko.observable(),
                    dateValue : {
                        startDate : ko.observable(null).extend({
                            date : true,
                            rateLimit : {
                                method : "notifyWhenChangesStop",
                                timeout : 400
                            }
                        }),
                        endDate : ko.observable(null).extend({
                            date : true,
                            rateLimit : {
                                method : "notifyWhenChangesStop",
                                timeout : 400
                            }
                        }),
                    },
                    isDate : function() {
                        return column.date;
                    },
                    reloadOnStoreChange : column.reloadOnStoreChange,
                });
            });

            if (hasStoreColumn === true || self._isMultiContext) {
                gc.app.sessionKGet('activeContext').subscribe(function(ctxId) {
                    self._currentPage = 1;
                    self._offset = (self._currentPage - 1) * self._limit;

                    self.load();
                });
            }

            this.columns(observableColumns);
        },
        activateSubscribers : function() {
            var self = this;

            _.each(self.columns(), function(observableColumn) {
                observableColumn.value.subscribe(function(val) {
                    self._currentPage = 1;
                    self._offset = (self._currentPage - 1) * self._limit;

                    if (self._isInitialized) {
                        self.saveState();
                    }

                    self.load();
                });
            });

            self.limit.subscribe(function(newValue) {
                console.log(newValue);

                if (self._isInitialized && newValue != self._limit) {
                    self._currentPage = 1;
                    self._offset = 0;
                    self._limit = newValue;

                    self.saveState();

                    self.load();
                }
            });

            self.searchKeyword.subscribe(function(newValue) {
                if (_.isEmpty(newValue)) {
                    self._isSearch = false;
                } else {
                    self._isSearch = true;
                }
                self._offset = 0;
                self.load();
            });

            self.activateQuerySubscriber();
        },
        activateQuerySubscriber : function() {
            var self = this;

            self.query.subscribe(function(newValue) {
                console.log(newValue)
                if (_.isEmpty(newValue)) {
                    self._isQuery = false;
                    self._query = "";
                } else {
                    self._isQuery = true;
                    self._query = newValue;
                }

                self._offset = 0;
                self.load();
            });
        },
        columnValue : function(name, value) {
            var column = _.findWhere(ko.unwrap(this.columns), {
                name : name
            });
            console.log(column);
            console.log(this.columns());
            if (!_.isUndefined(column)) {
                column.value(value);
            }
        },
        // -----------------------------------------------------------------------------------------
        // Event-handler for processing sort-clicks. Loads new data from rest-API.
        // -----------------------------------------------------------------------------------------
        sort : function(column, event) {
            var self = this;
            // Only change direction if sort-field has not changed.
            if (column.name == this._sort[0] || ('-' + column.name) == this._sort[0]) {
                if (this._sortDirection == 'ASC') {
                    this._sortDirection = 'DESC';
                } else {
                    this._sortDirection = 'ASC';
                }
            }

            // Add the hyphen if we are doing a descending sort.
            if (this._sortDirection == 'ASC') {
                this._sort = [ column.name ];
            } else {
                this._sort = [ '-' + column.name ];
            }

            self.saveState();

            return this.load();
        },
        // -----------------------------------------------------------------------------------------
        // Event-handler for processing filter inputs (ONKEYUP). Loads new data from rest-API.
        // -----------------------------------------------------------------------------------------
        applyFilter : function() {
            var self = this;

            // Reset filter
            this._filter = {};

            // First apply the default configured filters.
            _.each(this._defaultFilter, function(field) {
                var name = field.name;
                var val = field.value;

                self._filter[name] = val;
            });

            // Then move onto filters populated by user.
            _.each(this.columns(), function(column) {
                var name = '';
                var val = '';
                // console.log(column);
                if (!column.combined) {
                    name = column.name;
                    val = column.value();
                } else {
                    var v = column.value();
                    if (v) {
                        var arr = v.split('__');
                        if (arr && arr.length == 2) {
                            name = arr[0];
                            val = arr[1];
                        }
                    }
                }

                var isNumber = !isNaN(parseFloat(val)) && isFinite(val);
                var isBoolean = typeof val === 'boolean' || val == 'true' || val == 'false';

                // Add currently entered values.
                if (column.isDate()) {
                    if (column.dateValue.startDate() != null || column.dateValue.endDate() != null) {

                        var dateFilter = {
                            dateRange : {
                                startDate : gc.utils.toServerTime(column.dateValue.startDate()),
                                endDate : gc.utils.toServerTime(column.dateValue.endDate())
                            }
                        }

                        self._filter[name] = JSON.stringify(dateFilter);
                    }
                } else if (val && val != '' && (val.length > 2 || isNumber || isBoolean)) {
                    console.log(column)
                    if (!_.isEmpty(column.type) && column.type == 'ContextObject') {
                        console.log(column.useRegexp);
                        if (column.useRegexp) {
                            self._filter[name] = '$regex:{{val:"' + val + '"}}';
                        } else if (column.wildcard) {
                            self._filter[name] = '$regex:{{val:"' + val + '.*"}}';
                        } else {
                            self._filter[name] = '{{val:"' + val + '"}}';
                        }
                    } else if (name.startsWith('$attr.')) {
                        // Add wildcard expression.
                        self._filter[name] = val + (_.has(column, 'wildcard') && !column.wildcard ? '' : '*');
                    } else if ((!isNumber && !isBoolean && column.type != 'simple') || column.type == 'wildcard') {
                        // Add wildcard expression.
                        self._filter[name] = '$wc:' + val + '*';
                    } else {
                        self._filter[name] = val;
                    }
                }

            });
        },
        saveState : function() {
            var self = this;

            if (self._isInitialized) {
                var data = {
                    s : self._sort,
                    sd : self._sortDirection,
                    p : self._currentPage,
                    l : self._limit
                };
                var filters = {};

                var cols = self.columns();

                if (!_.isEmpty(cols)) {
                    _.each(cols, function(column) {
                        if (column.cookieKey) {
                            filters[column.cookieKey] = column.value();
                        }
                    });
                }

                data['f'] = filters;

                localStorage.setItem(self.cookieName(), JSON.stringify(data));
            }
        },
        cookieName : function() {
            var self = this;
            
            if(_.isEmpty(self._cookieName)) {
                var key = 'pgr_' + self._url.replace(/\/api\/v[0-9]+\//g, '');
                key = key.replace(/[\-\/]/g, '_');

                while (key.indexOf('__') != -1)
                    key = key.replace(/__/g, '_');

                return key;
            } else {
                return self._cookieName;
            }
        },
        loadState : function() {
            var self = this;

            var json = localStorage.getItem(self.cookieName());

            if (json) {
                var data = JSON.parse(json);

                if (!_.isEmpty(data)) {
                    self._sort = data.s;
                    self._sortDirection = data.sd, self._currentPage = data.p;
                    self._limit = data.l;
                    this._offset = (this._currentPage <= 0 ? 0 : this._currentPage - 1) * this._limit;

                    if (!_.isEmpty(data.f)) {
                        var columns = self.columns();

                        for ( var property in data.f) {
                            var filter = data.f;
                            if (filter.hasOwnProperty(property)) {
                                var val = filter[property];
                                var column = _.findWhere(columns, {
                                    cookieKey : property
                                });

                                if (column) {
                                    column.value(val);
                                }
                            }
                        }
                    }
                }
            }
        },
        refresh : function() {
            return this.load();
        },
        // -----------------------------------------------------------------------------------------
        // Loads data from rest-API.
        // -----------------------------------------------------------------------------------------
        load : function() {
            var self = this;

            // We are already in the process of loading data.
            if (self._busy) {
                return;
            }

            self._busy = true;

            $("button").prop("disabled", true);
            $("input[type='submit']").prop("disabled", true);

            if (!_.isUndefined(self.toggleSelector1)) {
                var $t1 = $(self.toggleSelector1);
                if (!_.isUndefined($t1)) {
                    $t1.show();
                }
            }

            if (!_.isUndefined(self.toggleSelector2)) {
                var $t2 = $(self.toggleSelector2);
                if (!_.isUndefined($t2)) {
                    $t2.hide();
                }
            }

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            // Initialize the filter before making the request.
            self.applyFilter();
            var filter = self._filter || {};

            if (self._isMultiContext) {
                var activeCtx = gc.app.sessionGet('activeContext');

                if (!_.isUndefined(activeCtx) && !_.isUndefined(activeCtx.id)) {
                    if (activeCtx.scope == 'merchant') {
                        filter.merchantIds = activeCtx.id;
                    } else if (activeCtx.scope == 'store') {
                        filter.storeIds = activeCtx.id;
                    } else if (activeCtx.scope == 'request_context') {
                        filter.requestContextIds = activeCtx.id;
                    }
                }
            }

            var url = self._url;

            if (self._isSearch && !_.isUndefined(self._searchUrl)) {
                url = self._searchUrl + '/' + encodeURI(self.searchKeyword());
            }

            if (self._isQuery && !_.isUndefined(self._queryUrl)) {
                url = self._queryUrl;
            }

           console.log(self._query);


            gc.rest.get({
                url : url,
                filter : filter,
                fields : self._fields,
                attributes : self._attributes,
                sort : self._sort,
                offset : self._offset,
                query: self._query,
                limit : self._limit,
                success : function(data, status, xhr) {
                    // Job done.
                    self._busy = false;

                    // Remember the total-count which will be needed for paging.
                    self._totalCount = data._metadata.totalCount;
                    // Sets the data in a knockout conform way.
                    self._setData(data.data, self.searchKeyword());
                    // Computes the page numbers to be displayed.
                    self._setPages();

                    // self.activateSubscribers();
                    self._isInitialized = true;

                    // if (self._onload) {
                    if (typeof self._onload === "function") {
                        self._onload(data, status, xhr);
                    }

                    if (!_.isUndefined(self.toggleSelector1)) {
                        var $t1 = $(self.toggleSelector1);
                        if (!_.isUndefined($t1)) {
                            $t1.hide();
                        }
                    }

                    if (!_.isUndefined(self.toggleSelector2)) {
                        var $t2 = $(self.toggleSelector2);
                        if (!_.isUndefined($t2)) {
                            $t2.show();
                        }
                    }

                    $("button").prop("disabled", false);
                    $("input[type='submit']").prop("disabled", false);

                    deferred.resolve(data, status, xhr);

                },
                error : function(jqXHR, status, error) {
                    self._busy = false;

                    if (!_.isUndefined(self.toggleSelector1)) {
                        var $t1 = $(self.toggleSelector1);
                        if (!_.isUndefined($t1)) {
                            $t1.hide();
                        }
                    }

                    if (!_.isUndefined(self.toggleSelector2)) {
                        var $t2 = $(self.toggleSelector2);
                        if (!_.isUndefined($t2)) {
                            $t2.show();
                        }
                    }

                    // self.activateSubscribers();
                    self._isInitialized = true;

                    if (self._onerror) {
                        self._onerror(jqXHR, status, error);
                    }

                    $("button").prop("disabled", false);
                    $("input[type='submit']").prop("disabled", false);

                    deferred.reject(jqXHR, status, error);
                },
                complete : function(data, status, xhr) {
                    self._busy = false;

                    if (!_.isUndefined(self.toggleSelector1)) {
                        var $t1 = $(self.toggleSelector1);
                        if (!_.isUndefined($t1)) {
                            $t1.hide();
                        }
                    }

                    if (!_.isUndefined(self.toggleSelector2)) {
                        var $t2 = $(self.toggleSelector2);
                        if (!_.isUndefined($t2)) {
                            $t2.show();
                        }
                    }

                    // self.activateSubscribers();
                    self._isInitialized = true;

                    if (self._oncomplete) {
                        self._oncomplete(data, status, xhr);
                    }

                    $("button").prop("disabled", false);
                    $("input[type='submit']").prop("disabled", false);

                    deferred.resolve(data, status, xhr);
                }
            });

            return promise;
        },
        onload : function(callback) {
            var self = this;
            if (typeof callback === "function") {
                self._onload = callback;
            }
        },
        // -----------------------------------------------------------------------------------------
        // Returns a loaded data without knockout data.
        // -----------------------------------------------------------------------------------------
        getData : function() {
            return ko.unwrap(this.data) || [];
        },
        // -----------------------------------------------------------------------------------------
        // Removes an item from the knockout data.
        // -----------------------------------------------------------------------------------------
        removeData : function(item) {
            if (!_.isEmpty(this.data)) {
                this.data.remove(item);
            }
        },
        // -----------------------------------------------------------------------------------------
        // Sets a new value for the default filter.
        // -----------------------------------------------------------------------------------------
        setDefaultFilter : function(filter) {
            if (!_.isUndefined(filter) && _.isArray(filter)) {
                this._defaultFilter = filter;
            }
        },
        // -----------------------------------------------------------------------------------------
        // Empties the default filter.
        // -----------------------------------------------------------------------------------------
        resetDefaultFilter : function() {
            this._defaultFilter = [];
        },
        // -----------------------------------------------------------------------------------------
        // Adds data to the knockout observable array.
        // -----------------------------------------------------------------------------------------
        _setData : function(data, searchKeyword) {
            // If a function exists for retrieving the data array, use that.
            if (typeof this._getArray === 'function') {
                var dataArray = ko.utils.unwrapObservable(this._getArray(data, searchKeyword));

                if (Array.isArray(dataArray)) {
                    this.data(dataArray);
                } else if (dataArray === undefined) {
                    this.data(new Array());
                } else {
                    console.log('pagingOptions.getArray(data) did not return an array');
                }
                // Otherwise, if data is already an array and no 'getArray'
                // function exists, just return that.
            } else if (Array.isArray(data)) {
                this.data(data);
            } else {
                console.log('Data must be an array or accessible through pagingOptions.getArray(data)');
            }
        },
        // -----------------------------------------------------------------------------------------
        // Computes page numbers to be displayed by template.
        // -----------------------------------------------------------------------------------------
        _setPages : function() {
            var numPages = Math.ceil(parseFloat(this._totalCount) / parseFloat(this._limit));
            var pagesArr = Array();

            var showNumPages = numPages < this._showNumPages ? numPages : this._showNumPages;
            var curPage = this._currentPage;

            // Calculate the previous page.
            var prevPage = (curPage - 1) < 0 ? 0 : (curPage - 1);

            // Calculate the first page number to show.
            var pageNum = curPage - Math.ceil(showNumPages / 2);

            // If page number is smaller than zero, add up until it is no more.
            while (pageNum < 0) {
                pageNum++;
            }

            // Add previous page to page array.
            // if(curPage > 1){
            pagesArr.push({
                num : prevPage,
                label : '«'
            });
            // }

            // Add page numbers to page array.
            for (var i = 1; i <= showNumPages; i++) {
                pageNum++;
                if (pageNum <= numPages) {
                    pagesArr.push({
                        num : pageNum,
                        label : pageNum
                    });
                }
            }

            // Add next page to page array.
            if (curPage < numPages) {
                var nextPage = curPage + 1;
                pagesArr.push({
                    num : nextPage,
                    label : '»'
                });
            }

            this.pages(pagesArr);
        }
    };

    return Pager;
});
