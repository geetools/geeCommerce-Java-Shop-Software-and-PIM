define( [ 'ko', 'gc/gc' ], function( _, ko, gc ) {
	var Attribute = function( options ) {
		this.backendLabel = null;
		this.code = null;
		this.code2 = null;

		gc.BaseViewModel.apply( this, arguments );
	};

	_.extend( Attribute.prototype, BaseViewModel.prototype );

	return Attribute;
});