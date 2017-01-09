<#--<#assign product=actionBean.product>-->
<#assign productId=product.id>

<#import "${t_layout}/1column.ftl" as layout>


<@layout.onecolumn pageModel=product>

		<div class="row">

			<div class="col-xs-12 col-sm-9">
				<#include "view_bundle.ftl"/>
			</div>


			<#if !product.bundle>
				<div id="prd-desc" class="col-xs-12 col-sm-4 col-md-4 col-lg-4 cat-hidden-sd cat-hidden-md cat-hidden-ld">
					<#include "view_description.ftl"/>
				</div>

				<div class="col-xs-12 col-sm-5 col-md-5 col-lg-5">
					<@cms_product_carousel />
				</div>

				<div id="prd-desc" class="col-xs-12 col-sm-4 col-md-4 col-lg-4 cat-hidden-xs">
					<#include "view_description.ftl"/>
					<@cms_product_variants />
				</div>
			</#if>

			<div id="prd-cart" class="col-xs-12 col-sm-3 col-md-3 col-lg-3">
				<div id="prd-cart-box">
					<!-- filled dynamically via JavaScript -->
				</div>
			</div>
	
		</div>

		<div class="row">
			<div id="prd-details" class="col-xs-12">
				<hr />
			</div>
		</div>

		<div class="row">
			<div id="prd-details" class="col-xs-12 col-sm-9 col-md-9 col-lg-9">
				<h3><@message text="Product Description" lang="en" text2="Produktbeschreibung" lang2="de" /></h3>

				<div id="prd-details-reviews" class="prd-text">
					<@customer_review_rating product_id="${productId}" />
				</div>
				
	            <h2 id="prd-details-name">
	            	<@attribute src=product code="name" />
					<span><@attribute src=product code="name2" /></span>
	            </h2>
			
				<div id="prd-details-artno" class="prd-text">
					<@message text="Article No." lang="en" text2="Artikel-Nr." lang2="de" />
					<span><@attribute src=product code="article_number" /></span>
				</div>
				<div id="prd-details-shortdesc" class="prd-text">        
					<@attribute_exists src=product code="short_description" parent=true>
            			<@attribute src=product code="short_description" make="list" parent=true/>
        			</@attribute_exists>
				</div>
				
				<div id="prd-details-desc" class="prd-text">
					<@attribute_exists src=product code="description" parent=true>
						<@attribute src=product code="description" parent=true />
					</@attribute_exists>
				</div>
			
			</div>

			<div id="prd-extra-info" class="col-xs-12 col-sm-12 col-md-3 col-lg-3">
				<div id="prd-extra-info-box">
					<h3><@message text="Share with Friends" lang="en" text2="Mit Freunden teilen" lang2="de" />:</h3>
					<ul class="prd-social-media">
						<li><a href="#"><img src="<@skin path="images/icons/facebook.png" />" /></a></li>
						<li><a href="#"><img src="<@skin path="images/icons/Pinterstt-48.png" />" /></a></li>
						<li><a href="#"><img src="<@skin path="images/icons/twitter.png" />" /></a></li>
					</ul>
				</div>
			</div>
			
		</div>

		<div class="row">
			<div class="col-xs-12">
				<hr />
			</div>
		</div>

		<div class="row">
			<div id="prd-extended-details" class="col-xs-12">
				<@product_details product_id="${productId}"/>
			</div>
		</div>

		<div class="row">
			<div id="prd-upsells" class="col-xs-12">
				<@upsell product_id="${productId}"/>
			</div>
		</div>

		<div class="row">
			<div class="col-xs-12">
				<hr />
			</div>
		</div>


		<div class="row">
			<div id="prd-customer-reviews" class="col-xs-12">
			    <@import uri="/review/summary/${productId}"></@import>
			    
			    <@import uri="/review/product-view/${productId}"></@import>
			</div>
		</div>

</@layout.onecolumn>