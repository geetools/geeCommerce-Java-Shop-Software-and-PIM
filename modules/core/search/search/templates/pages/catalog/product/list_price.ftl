<#assign finalPrice=0>
<#assign retailPrice=0>
<#assign specialPrice=0>
<#assign salePrice=0>

<#if product.variantMaster>

<#-- As a variant-master does not have its own price, we get the lowest one from its child-products -->
    <#assign lowestPrices=product.price.getLowestValidPrices(defaultPricingCtx)!>
    <#assign lowestPrice=product.price.getLowestFinalPriceFor(defaultPricingCtx)>
    <@get type="Product" id=lowestPrice.productId var="lowestPriceProduct" />

    <#assign finalPrice=(lowestPrice.price)!0>
    <#assign retailPrice=lowestPrices['retail_price']!0>
    <#assign specialPrice=lowestPrices['special_price']!0>
    <#assign salePrice=lowestPrices['sale_price']!0>
<#else>
    <#assign prices=product.price.getValidPrices(defaultPricingCtx)>

    <#assign finalPrice=product.price.finalPrice!0.0>
    <#assign retailPrice=prices['retail_price']!0.0>
    <#assign specialPrice=prices['special_price']!0.0>
    <#assign salePrice=prices['sale_price']!0.0>
</#if>

<#if (!product.programme || product.price.hasValidPrice())>

    <#if (salePrice > 0 && retailPrice > 0)>
    <span class="cat-item-sale-price">${salePrice?string.currency}</span>
    <span class="cat-item-retail-price cat-item-old-price">${retailPrice?string.currency}</span>
    <#elseif (salePrice > 0)>
    <span class="cat-item-sale-price">${salePrice?string.currency}</span>
    <#elseif (retailPrice > 0)>
    <span class="cat-item-retail-price">${finalPrice?string.currency}</span>
    </#if>

</#if>
