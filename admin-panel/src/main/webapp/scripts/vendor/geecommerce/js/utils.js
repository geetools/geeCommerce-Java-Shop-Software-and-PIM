define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		unwrap : function(obj) {
			var self = this;
			
		    if (!_.isUndefined(obj) && typeof obj == "object") {
		    	
		        for (var attr in obj) {
		        	
		        	if(attr == '$gc') {
		        		delete obj[attr];
		        	}
		        	
		            // Recursive call to solve the "smaller" problem
		        	self.unwrap(obj[attr]);
		        }
		    } else {
		        // Not an object, ignore
		    }
		},
		triggerToolbar : function(element) {
			var formEL = $(element).closest('form');
			
			if(formEL.length) {
				var formId = formEL.attr('id');
				var toolbar = formEL.closest('div.main-content').find("[data-for='" + formId + "']").first();
				
                $(toolbar).attr('data-init', '1');
                $(toolbar).find('div.loader').hide();
                $(toolbar).find('div.buttons').show();
                $(toolbar).fadeIn(600);
			} else {
				// Imitate a click and a change, as this causes the toolbar to appear.
				$(element).closest('form').find('.toolbar-trigger').click();
				$(element).closest('form').find('.toolbar-trigger').trigger("change");
			}
		},
		toLanguageMap : function(languages) {
			var langMap = [];

			_.each(languages, function(obj) {
				langMap.push({
					code : obj.iso6391Code,
					label : gc.ctxobj.val(obj.label, 'native')
				});
			});

			return langMap;
		},
		toCountryMap : function(countries) {
			var countryMap = [];

			_.each(countries, function(obj) {
				countryMap.push({
					code : obj.code,
					label : obj.name
				});
			});

			return countryMap;
		},
		toCurrencyMap : function(currencies) {
			var currencyMap = [];

			_.each(currencies, function(obj) {
				currencyMap.push({
					code : obj.code,
					label : obj.name,
					symbol : obj.symbol
				});
			});

			return currencyMap;
		},
		unsubscribe : function(subscriptions) {
			if (subscriptions) {
				while (subscriptions.length) {
					subscriptions.pop().unsubscribe();
				}
			}
		},
		imagePath : function(path) {
			return 'https://' + gc.app.confGet('productImagesSubdomain') + gc.app.confGet('productImagesUriPrefix') + '/' + ko.unwrap(path);
		},
		fromServerTime : function(date) {
			if(!date)
				return date;

			console.log(date);
			if (typeof date == 'string' || date instanceof String){
				if(date.indexOf('Z') == -1){
					date = date + 'Z'
				}
				console.log(date);
			    date = new Date(date);
			}

			console.log(date);
			var serverOffset = gc.app.confGet('serverTimezoneOffset');
			var d = new Date();
			var utc = date.getTime() - (60000 * serverOffset);
			var nd = new Date(utc  + (d.getTimezoneOffset() * 60000));
			console.log(nd);
			return nd;
		},
		toServerTime : function(date) {
			if(!date)
				return date;

			if (typeof date == 'string' || date instanceof String)
				date = new Date(date);

			var serverOffset = gc.app.confGet('serverTimezoneOffset');
			var d = new Date();
			var utc = date.getTime() + (serverOffset * 60000);
			var nd = new Date(utc - (60000 * d.getTimezoneOffset()));

			return nd
		},
		startOfTheDay: function(date) {
			if(!date)
				return date;

			date.setHours(0,0,0,0);
			return date;
		},
		endOfTheDay: function(date) {
			if(!date)
				return date;

			date.setHours(23,59,59,999);
			return date;
		},
		typedValue: function(element) {
            var self = this;
            
		    $el = $(element);
		    
		    var val = $el.val();
		    var type = $el.attr('type');
		    
		    if(type == 'number') {
		        return self.asNumber(val);
		    } else {
		        return val;
		    }
		},
        asNumber: function(val) {
            var self = this;
            
            if(_.isString(val)) {
                if(val.indexOf(',') != -1 || val.indexOf('.') != -1) {
                    var commaIdx = val.lastIndexOf(',');
                    var dotIdx = val.lastIndexOf('.');
                    
                    if(commaIdx > dotIdx) {
                        return parseFloat(Math.round(Math.round(1000*val.replace('.', '').replace(',', '.'))/10)/100);
                    } else {
                        return parseFloat(Math.round(Math.round(1000*val.replace(',', ''))/10)/100);
                    }
                } else {
                    return parseInt(val);
                }
            } else {
                return val;
            }
        }
	};
});
