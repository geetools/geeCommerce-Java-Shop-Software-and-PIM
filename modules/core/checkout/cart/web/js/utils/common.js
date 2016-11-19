define([ 'gc/gc' ], function(gc) {

	return {
        attributeValue : function (attributes, code) {
            if(!attributes)
                return;
            var attribute = _.findWhere(attributes, {code: code});
            if(attribute && attribute.value);
                return attribute.value[0].val;
        }
	};
});