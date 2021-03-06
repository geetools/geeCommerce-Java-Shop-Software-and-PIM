<#if upsellProducts??>
	<div class="row">
		<div class="col-xs-12">
			<hr>
		</div>
	</div>
	<div class="clear"></div>
	<div id="up_sell_products">
		<div class="product_tape_container">
			<div class="transparent_top"></div>
			<div class="transparent">
				<div class="product_tape_header">Upselling Products</div>
				<br/>
				<div class="image_carousel">
					<div id="product-tape-up-sell">
						<#list upsellProducts as upsellProduct>
							<div class="outerItemTopSellerContainer" style="display:inline; float: left; margin-right: 20px; margin-top: 20px;">
			                   	<div style="display:inline" class="innerItemTopSellerContainer" product="${upsellProduct.id}">
		                        	<a href="<@url target=upsellProduct />"><img class="product-image" product="${upsellProduct.id}"
		                     		 src="${upsellProduct.mainThumbnailURI}"/></a>
		                  			<p><a href="<@url target=upsellProduct />">${upsellProduct.attr("name").val}</a></p>
		                   			<p class="product-price" product="${upsellProduct.id}"><span class="oldPrice"></span><span class="finalPrice"></span></p>
		                		</div>
		           			 </div>
						</#list>
					</div><!-- /product-tape-up-sell -->
					<div class="clearfix"></div>
<!-- TODO					
					<a href="#" id="product_tape_prev_up_sell" class="prev disabled" style="display: block;"><span>prev</span></a>
					<a href="#" id="product_tape_next_up_sell" class="next" style="display: block;"><span>next</span></a>
					<div id="product_tape_pag_up_sell" class="pagination" style="display: block;"><a href="#" class="selected"><span>1</span></a><a href="#"><span>2</span></a><a href="#"><span>3</span></a></div>
-->
								</div> <!-- /image_carousel -->	
				<div class="clearfix"></div>	
			</div><!-- /transparent -->	
		</div><!-- /product_tape_container -->
	</div>
</#if>
