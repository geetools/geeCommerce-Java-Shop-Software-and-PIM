define(['jquery', 'bootstrap', 'gc/gc', 'catalog/utils/media', 'jquery-swipe'], function ($, Bootstrap, gc, mediaUtil) {

	return {
		// -----------------------------------------------------------------------------
		// Find variant product by selected options.
		// -----------------------------------------------------------------------------
		renderImages : function(productVM) {

			$('#prd-media').empty();
		
			gc.app.render({ template: 'templates/catalog/product/carousel.html', data: { mainImage: productVM.mainImage, galleryImages: productVM.galleryImages },
				target: '#prd-media' },
				function(data) {
	
				var targetEL = data.target;
	
				// Start the bootstrap carousel for the main image.
				$('.main-carousel').carousel({interval: false});
				
				$('.main-carousel:visible').swipe({
					swipeLeft:function(event, direction, distance, duration, fingerCount) {
						console.log('SWIPE!! ', $(this), event);
					
						$('.main-carousel').carousel('next');    
					},
					swipeRight:function(event, direction, distance, duration, fingerCount) {
						console.log('SWIPE!! ', $(this), event);
						$('.main-carousel').carousel('prev');
					},
					threshold:0
				});

				// Start the zoom plugin for the main image.
				$zoomLinkEL = targetEL.find('.zoom-link');
			    $zoomLinkEL.magnificPopup({
				    gallery: {
				      enabled: true
				    },
				    type: 'image'
				});

			    
			    console.log('THERE????????????????????? ', $('#prd-img-thumbnails>div>ul'));
			    
				// Start the slick slider for the thumbnails.
				// $('#prd-img-thumbnails>div>ul').slick({
				//   infinite: false,
				//   slidesToShow: 5,
				//   slidesToScroll: 3,
				//   centerPadding: '50px',
				//   vertical: true,
				//   arrows: true,
				//   prevArrow: '<button type="button" class="slick-prev"></button>',
				//   nextArrow: '<button type="button" class="slick-next"></button>',
				//   responsive: [
				//     {
				//       breakpoint: 750,
				//       settings: {
				// 		  infinite: false,
				// 		  slidesToShow: 5,
				// 		  slidesToScroll: 2,
				// 		  arrows: true,
        			// 	  centerPadding: '40px',
				// 		  vertical: false,
				// 		  prevArrow: '<button type="button" class="slick-prev"></button>',
				// 		  nextArrow: '<button type="button" class="slick-next"></button>',
				//       }
				//     },
				//     {
				//       breakpoint: 450,
				//       settings: {
				// 		  infinite: true,
				// 		  slidesToShow: 4,
				// 		  arrows: false,
        			// 	  centerPadding: '80px',
				// 		  vertical: false,
				// 		  prevArrow: '<button type="button" class="slick-prev"></button>',
				// 		  nextArrow: '<button type="button" class="slick-next"></button>',
				//       }
				//     }
				//   ]
				// });
				
				// Display large image depending on which thumbnail the user hovers over.
				$(document).on('click', '#prd-img-thumbnails>div>ul li', function() {
					$('.main-carousel').carousel($(this).data('slick-index'));
				});
			});
		},
		moveToImage : function(imageURI) {
			console.log('moveToImage_____ ', imageURI);

			var foundImageEL = $('#prd-img-thumbnails>div>ul').find("[data-orig='" + imageURI + "']");
			
			if(!_.isEmpty(foundImageEL)) {
				var foundListEL = foundImageEL.first().closest('li');
	
				if(!_.isEmpty(foundImageEL)) {
					var idx = foundListEL.data('slick-index');
					
					console.log('moveToImage_____ ', foundImageEL, foundListEL, foundListEL.data('slick-index'));

										
					$('#prd-img-thumbnails>div>ul').slick('slickGoTo', idx);
					$('.main-carousel').carousel(idx+1);
				}
			}
		}
	};
});