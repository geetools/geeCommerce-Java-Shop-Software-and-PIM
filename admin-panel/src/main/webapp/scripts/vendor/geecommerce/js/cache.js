define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		put : function(key, value) {
//			console.time("cache-put-" + key);
		
			gc.app.dataPut('cache:' + key, value);
			
//			console.timeEnd("cache-put-" + key);
		},
		get : function(key, options) {
//			console.log(key + '-options: ', options);
		
//			console.time("cache-get-" + key);

			options = options || {};
			var filter = options.filter;

			var data = gc.app.dataGet('cache:' + key);
			var filteredData = [];

//			console.timeEnd("cache-get-" + key);

			if(filter && _.isObject(filter) && !_.isEmpty(filter)) {
//				console.time("cache-get-filter-" + key);

				var firstPropKey = Object.keys(filter)[0];

				//-----------------------------------------------------
				// Filter only contains ids.
				//-----------------------------------------------------				
				if(filter.id && !_.isEmpty(filter.id)) {
//console.log('GETTING FOR IDs: ', filter.id);
					var ids = filter.id.split(',');
					
					_.each(ids, function(id) {
						var attr = _.findWhere(data, {id: id});
						filteredData.push(attr);
					});

//console.log('GETTING FOR IDs: ', filter.id, filteredData);
					
					if(ids.length !== filteredData.length) {
						filteredData = [];
					}
				//-----------------------------------------------------
				// Filter only contains ids #2.
				//-----------------------------------------------------				
				} else if(_.size(filter) === 1 && /[0-9, ]+/.test(filter[firstPropKey])) {
					var ids = filter[firstPropKey].split(',');

					_.each(ids, function(id) {
						var whereClause = {};
						whereClause[firstPropKey] = id;
						
						var foundObjects = _.where(data, whereClause);
						
						if(foundObjects && foundObjects.length > 0) {
							for(var i=0; i<foundObjects.length; i++) {
								filteredData.push(foundObjects[i]);
							}
						}
					});
				//-----------------------------------------------------
				// Filter only contains codes.
				//-----------------------------------------------------				
				} else if(filter.code && !_.isEmpty(filter.code) && _.size(filter) === 1) {
					var codes = filter.code.split(',');
					
					_.each(codes, function(code) {
						var attr = _.findWhere(data, {code: code.trim()});
						filteredData.push(attr);
					});
					
					if(codes.length !== filteredData.length) {
						filteredData = [];
					}
				//-----------------------------------------------------
				// All other filters.
				//-----------------------------------------------------				
				} else {
//					console.log('USING FILTER *** ', filter);
				
					filteredData = _.where(data, filter);
					
//					console.log('Found data for FILTER *** ', filter, filteredData, data);
				}
				
//				console.timeEnd("cache-get-filter-" + key);
			} else {
				filteredData = data;
			}
			
			return filteredData;
		}
	}
});