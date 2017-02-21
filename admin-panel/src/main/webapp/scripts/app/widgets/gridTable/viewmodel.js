define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.pager = options.pager;
		
		if(options.forType) {
		    self.forType = options.forType;
		}
        
        self.pager.addOnLoadListener(function(data, status, xhr, ctx) {
            self.numSelectedRows(0);
            
            if(!(self.selectMode == 'all_pages' && !_.isUndefined(ctx) && !_.isUndefined(ctx.page))) {
                self.uncheckedIds([]);
            }
        });
        
        self.pager.addOnPageClickListener(function() {
            if(self.selectMode == 'all_pages') {
                self.checkAllOnCurrentPage();
                self.updateNumSelectedRows();
            }
        });
		
		self.columns = options.columns;
		
		self.searchURI = options.searchURI;
		
		self.menuItems = options.menuItems;

		self.loadedIds = ko.observableArray([]);
		
        self.uncheckedIds = ko.observableArray([]);
        
        self.uncheckedIds.subscribe(function(newVal) {
            console.log('NEW VALUE UNCHECKEDIDS:::: ', newVal, self.selectMode);
        });
		
        self.numSelectedRows = ko.observable(0);
        
        self.numSelectedRows.subscribe(function(newVal) {
            var msgPefix = self.forType || '';
            gc.app.channel.publish(msgPefix + '.gt.onchange', { numSelectedRows: newVal, checkedIds: self.idsCurrentlyCheckedOnPage(), uncheckedIds: self.uncheckedIds(), selectMode: self.selectMode });
        });
        
		self.selectMode = 'current_page';
		
        var options = [];
        
        _.forEach(self.pager.limitOptions(), function(item) {
            options.push({
                id : item.value,
                text : item.label
            });
        });
		
		self.limitOptions = options;
	};
    
    ctor.prototype.attached = function(view) {
        var self = this;
        self.view = view;
        
        $(document).on('click', '.chbx-page-select-all', function() {
            self.selectMode = 'current_page';
            
            self.checkAllOnCurrentPage();
            self.uncheckedIds([]);
            self.updateNumSelectedRows();
        });

        $(view).on('click', 'thead>tr>th.th-select>i', function() {
           self.selectMode = 'current_page';

           var _checked = $(this).data('checked') || false;

           if(_checked) {
               self.uncheckAllOnCurrentPage();
               $(this).data('checked', false);
           } else {
               self.checkAllOnCurrentPage();
               $(this).data('checked', true);
           }
           
           self.uncheckedIds([]);
           self.updateNumSelectedRows();
        });
        
        $(document).on('click', '.chbx-pages-select-all', function() {
            self.selectMode = 'all_pages';
            
            self.checkAllOnCurrentPage();
            self.uncheckedIds([]);
            self.updateNumSelectedRows();
        });
        
        $(view).on('click', '.grid-table th.td-select>input[type="checkbox"]', function() {
            var val = $(this).val();

            if(!$(this).is(":checked")) {
                if(self.selectMode === 'all_pages') {
                    console.log('NOOOOT CHECKED --------------------> ', $(this).val());
                    
                    if(self.uncheckedIds.indexOf(val) === -1) {
                        self.uncheckedIds.push(val);
                    }
                }
            }
            
            self.updateNumSelectedRows();            
        });
    };

    ctor.prototype.idsCurrentlyCheckedOnPage = function() {
        var self = this;
        
        var bodyEL = $(self.view).closest('body');
        
        console.log('ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ111111111111 view: ', $(self.view));
        console.log('ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ111111111111 bodyEL: ', bodyEL);
        
        
        var gridTableEL = bodyEL.find('.grid-table').first();
        var checkboxes = gridTableEL.find('th.td-select>input[type="checkbox"]:checked');

        var _checkedIds = [];
        
        checkboxes.each(function(idx, checkbox) {
            _checkedIds.push($(checkbox).val());
        });

        return _checkedIds;
    };
    
    ctor.prototype.checkAllOnCurrentPage = function() {
        var self = this;

        var bodyEL = $(self.view).closest('body');
        var gridTableEL = bodyEL.find('.grid-table').first();
        var checkboxes = gridTableEL.find('th.td-select>input[type="checkbox"]');
        
        checkboxes.each(function(idx, checkbox) {
            var _val = $(checkbox).val();
            if(!(self.selectMode == 'all_pages' && self.uncheckedIds.indexOf(_val) !== -1)) {
                $(checkbox).prop("checked", true);
            }
        });
    };
    
    ctor.prototype.uncheckAllOnCurrentPage = function() {
        var self = this;

        var bodyEL = $(self.view).closest('body');
        var gridTableEL = bodyEL.find('.grid-table').first();
        var checkboxes = gridTableEL.find('th.td-select>input[type="checkbox"]');
        
        checkboxes.each(function(idx, checkbox) {
            $(checkbox).prop("checked", false);
        });
    };
    
    ctor.prototype.updateNumSelectedRows = function() {
        var self = this;

        if(self.selectMode === 'all_pages') {
            var _totalCount = self.pager.totalCount();
            var _ignoreCount = self.uncheckedIds().length;
            
            self.numSelectedRows(_totalCount-_ignoreCount);
        } else {
            var _currentlyCheckedOnPage = self.idsCurrentlyCheckedOnPage();
            self.numSelectedRows(_currentlyCheckedOnPage.length);
        }
    };

	return ctor;
});