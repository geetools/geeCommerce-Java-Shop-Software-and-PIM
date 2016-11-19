<#if !wProduct??>
    <#assign wProduct=product >
</#if>

<#assign finalPrice=0>
<#assign retailPrice=0>
<#assign salePrice=0>

<#if wProduct.variantMaster>
<#-- As a variant-master does not have its own price, we get the lowest one from its child-products -->
    <#assign lowestPrices=wProduct.price.getLowestValidPrices(defaultPricingCtx)>

    <#assign finalPrice=wProduct.price.getLowestFinalPrice(defaultPricingCtx)!0>
    <#assign retailPrice=lowestPrices['retail_price']!0>
    <#assign salePrice=lowestPrices['sale_price']!0>
<#else>
    <#assign prices=wProduct.price.getValidPrices(defaultPricingCtx)>

    <#assign finalPrice=wProduct.price.finalPrice!0.0>
    <#assign retailPrice=prices['retail_price']!0.0>
    <#assign salePrice=prices['sale_price']!0.0>
</#if>

<#if (!wProduct.programme || wProduct.price.hasValidPrice())>

<div class="cms-product-price">

<#--
		<@cache key="product_prices_${wProduct.id?string}">
-->
    <#if (salePrice > 0 && retailPrice > 0)>
        <span class="prd-retail-price prd-old-price">${retailPrice?string.currency}</span>
        <span class="prd-sale-price">${salePrice?string.currency}</span>
    <#elseif (salePrice > 0)>
        <span class="prd-sale-price">${salePrice?string.currency}</span>
    <#elseif (retailPrice > 0)>
        <span class="prd-retail-price">${retailPrice?string.currency}</span>
    </#if>

    <small class="tax-info"><@message text="VAT included" lang="en" text2="Inkl. 19% MwSt" lang2="de" /></small>
    <span class="shipping-info"><@message text="Free delivery and returns" lang="en" text2="Kostenloser Versand und Rückversand" lang2="de" /></span>
    <!-- ${.now} -->

<#--
		</@cache>
-->

</div>

</#if>



