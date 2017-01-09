<#--<#assign product=actionBean.product>-->

<#assign finalPrice=0>
<#assign retailPrice=0>
<#assign salePrice=0>

<#if product.variantMaster>
	<#-- As a variant-master does not have its own price, we get the lowest one from its child-products -->
	<#assign lowestPrices=product.price.getLowestValidPrices(defaultPricingCtx)>

	<#assign finalPrice=product.price.getLowestFinalPrice(defaultPricingCtx)!0>
	<#assign retailPrice=lowestPrices['retail_price']!0>
	<#assign salePrice=lowestPrices['sale_price']!0>
<#else>
	<#assign prices=product.price.getValidPrices(defaultPricingCtx)>
 
	<#assign finalPrice=product.price.finalPrice!0.0>
	<#assign retailPrice=prices['retail_price']!0.0>
	<#assign salePrice=prices['sale_price']!0.0>
</#if>

<#if (!product.programme || product.price.hasValidPrice())>

	<div class="product-panel-box">

<#--
		<@cache key="product_prices_${product.id?string}">
-->
		    <#if (salePrice > 0 && retailPrice > 0)>
	            <span class="prd-retail-price prd-old-price">${retailPrice?string.currency}</span>
	            <span class="prd-sale-price">${salePrice?string.currency}</span>
		    <#elseif (salePrice > 0)>
	            <span class="prd-sale-price">${salePrice?string.currency}</span>
		    <#elseif (retailPrice > 0)>
	            <span class="prd-retail-price">${retailPrice?string.currency}</span>
		    </#if>

		<#if product.bundle>
    		<span class="prd-bundle-price"></span>
		</#if>
<#--
		    <small class="tax-info"><@message text="VAT included" lang="en" text2="Inkl. 19% MwSt" lang2="de" /></small>
		    <span class="shipping-info"><@message text="Free delivery and returns" lang="en" text2="Kostenloser Versand und Rückversand" lang2="de" /></span>
-->

  		    <span class="shipping-info"><@message text="Delivery time: 1 - 4 days" lang="en" text2="Lieferzeit: 1 – 4 Tage" lang2="de" /></span>
			<!-- ${.now} -->		
<#--
		</@cache>
-->
	
		<@add_to_cart product_id="${product.id}"/>
	</div>

</#if>

