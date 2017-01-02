define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
        thumbnailXS : function(mainImagePath) {
            var self = this;
            return _.isEmpty(mainImagePath) ? undefined : self.buildImageURL(mainImagePath, 50);
        },
        thumbnail : function(mainImagePath) {
            var self = this;
            return _.isEmpty(mainImagePath) ? undefined : self.buildImageURL(mainImagePath, 70);
        },
		buildImageURL : function(imgURI, width, height, defaultURI) {
		
			if(_.isUndefined(imgURI) && !_.isUndefined(defaultURI)) {
				return defaultURI;
			}
			
			if(_.isUndefined(imgURI))
				return;

			// --------------------------------------------------------------------------
			// Size parameter already exists in URI.
			// --------------------------------------------------------------------------
			
			if(/.+___s:(?:[\d]+)?x(?:[\d]+)?\.(?:jpg|jpeg|png|gif)$/.test(imgURI)) {
			    var sizeParam = '';
			    
			    if(width)
			        sizeParam += width;

			    sizeParam += 'x';

			    if(height)
			        sizeParam += height;
				
			    // Replace previous size with new size if it already exists in URI.
				return imgURI.replace(/(.+___s:)(?:[\d]+)?x(?:[\d]+)?\.(jpg|jpeg|png|gif)$/, '$1' + sizeParam + '.$2');
			}

			// --------------------------------------------------------------------------
			// Size parameter does not exist yet in URI.
			// --------------------------------------------------------------------------
			
		    var sizeParam = 's:';
		    if(width) {
		        sizeParam += width;
		    }

		    sizeParam += 'x';

		    if(height) {
		        sizeParam += height;
		    }

		    var urlParams = '___';
		    if(sizeParam.length > 3) {
		        urlParams += sizeParam;
		    }

		    var url = '';

		    url += imgURI;

		    if(urlParams && urlParams.length > 3) {
		        url = url.replace(/(.*)\.(jpg|jpeg|png|gif)$/, '$1' + urlParams + '.$2');
		    }

		    return url;
		}		
	};
});