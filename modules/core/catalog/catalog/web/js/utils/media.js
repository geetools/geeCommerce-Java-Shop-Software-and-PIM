define([ 'gc/gc' ], function(gc) {

	return {
		thumbnail : function(mainImagePath) {
			var self = this;
			return _.isEmpty(mainImagePath) ? undefined : self.buildImageURL(mainImagePath, 70);
		},
		buildImageURL : function(imgURI, width, height, defaultURI) {
		
			if(_.isUndefined(imgURI) && !_.isUndefined(defaultURI)) {
				return defaultURI;
			}
		
		    var domain = 'demo.commerceboard.com.local';
		    var webPath = '/c/media'; // TODO: load from settings.

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

		    var url = webPath;

		    if(url.lastIndexOf('/')+1 < url.length) {
		        url += '/';
		    }

		    url += imgURI;

		    if(urlParams && urlParams.length > 3) {
		        url = url.replace(/(.*)\.(jpg|jpeg|png|gif)$/, '$1' + urlParams + '.$2');
		    }

		    return url;
		}		
	};
});