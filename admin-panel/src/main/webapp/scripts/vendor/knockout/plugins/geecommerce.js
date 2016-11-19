define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    return {
        unwrap : function(obj) {
            // Make sure that we are working with an unwrapped object.
            var koUnwrapped = ko.unwrap(obj);

            // If we are dealing with an array, we attempt to unwrap all containing objects.
            if (_.isArray(koUnwrapped)) {
                var unwrappedObjects = [];

                for (var i = 0; i < koUnwrapped.length; i++) {
                    var obj = ko.unwrap(koUnwrapped[i]);

                    if (_.isArray(obj)) {
                        var unwrappedObject = [];

                        // Shallow unwrap all properties and add them to the new Array.
                        for (var j = 0; j < obj.length; j++) {
                            unwrappedObject.push(ko.unwrap(obj[j]));
                        }
                    } else if (_.isObject(obj)) {
                        var keys = Object.keys(obj);
                        var unwrappedObject = {};

                        // Shallow unwrap all properties and add them to the new JSON object.
                        for (var j = 0; j < keys.length; j++) {
                            unwrappedObject[keys[j]] = ko.unwrap(obj[keys[j]]);
                        }
                    } else {
                        unwrappedObject = obj;
                    }

                    unwrappedObjects.push(unwrappedObject);
                }

                return unwrappedObjects;
                // If we are dealing with an object, then shallow unwrap and return int.
            } else if (_.isObject(koUnwrapped)) {
                var keys = Object.keys(koUnwrapped);
                var unwrappedObject = {};

                for (var j = 0; j < keys.length; j++) {
                    unwrappedObject[keys[j]] = ko.unwrap(koUnwrapped[keys[j]]);
                }

                return unwrappedObject;
                // Otherwise just return the outer unwrapped object as it is neither an array nor an object.
            } else {
                return koUnwrapped;
            }
        }
    };
});
