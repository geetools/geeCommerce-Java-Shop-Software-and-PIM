<div class="row">

	<!-- ---------------------------------------------- -->
	<!-- Main images 								   --->
	<!-- ---------------------------------------------- -->
	<div class="prd-img-gallery col-xs-12 col-sm-10 col-md-10 col-lg-10">
		<div class="main-carousel carousel slide hidden-sm-up hidden-sm hidden-md hidden-lg" data-ride="carousel">
        	<div class="carousel-inner">
				{{#mainImage}}
		            <div class="item active"><img src="{{{largeImage}}}" data-orig="{{{origImage}}}" data-index="0"></div>
				{{/mainImage}}
           		{{#galleryImages}}
					{{#largeImage}}
			            <div class="item"><img src="{{{largeImage}}}" data-orig="{{{origImage}}}" data-index="{{{index}}}"></div>
					{{/largeImage}}
            	{{/galleryImages}}
        	</div>
    	</div>
		<div class="main-carousel carousel slide hidden-xs-down hidden-xs" data-ride="carousel">
        	<div class="carousel-inner">
				{{#mainImage}}
		            <div class="item table-active active"><a class="zoom-link" href="{{{zoomImage}}}"><img src="{{{largeImage}}}" data-orig="{{{origImage}}}" data-index="0"></a></div>
				{{/mainImage}}
           		{{#galleryImages}}
					{{#largeImage}}
		            	<div class="item"><a class="zoom-link" href="{{{zoomImage}}}"><img src="{{{largeImage}}}" data-orig="{{{origImage}}}" data-index="{{{index}}}"></a></div>
					{{/largeImage}}
            	{{/galleryImages}}
        	</div>
    	</div>
	</div>

	<!-- ---------------------------------------------- -->
	<!-- Thumbnail images 							   --->
	<!-- ---------------------------------------------- -->
	<div id="prd-img-thumbnails" class="col-xs-12 col-sm-2 col-md-2 col-lg-2">
		<div>
			<ul>
				{{#mainImage}}
		            <li><img src="{{{largeImage}}}" data-orig="{{{origImage}}}" alt=""></li>
				{{/mainImage}}
				{{#galleryImages}}
					{{#thumbnail}}
						<li><img src="{{{thumbnail}}}" data-orig="{{{origImage}}}" alt="" /></li>
					{{/thumbnail}}
				{{/galleryImages}}
			</ul>
		</div>
	</div>
	
</div>
    