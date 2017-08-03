define(['jquery', 'bootstrap', 'gc/gc'], function ($, Bootstrap, gc) {

	$('.product-list-filter>ul>li.off, .product-list-filter>ul>li.on').on('click', function(e) {
		e.stopPropagation();
		e.preventDefault();

		window.location.href = $(this).children('a').first().attr('href');
	});

	$('.pl-option-filter input').keyup(function(e) {
		var inpVal = $(this).val();
	
		var filterEL = $(this).closest('div.product-list-filter');
		var entries = filterEL.find('li>a');
		
		$.each(entries, function(idx, foundEL) {
			var optLabel = $(this).text();
			var result = optLabel.match(new RegExp(inpVal, 'i'));
			
			if(result) {
				$(this).removeClass('hide-filter-option');
			} else {
				$(this).addClass('hide-filter-option');
			}
		});
	});
	
});
