<#assign finalPrice=0>
<#assign retailPrice=0>
<#assign clubPrice=0>
<#assign specialPrice=0>
<#assign selloutPrice=0>

<#if product.variantMaster>
<#-- As a variant-master does not have its own price, we get the lowest one from its child-products -->
    <#assign lowestPrices=product.price.getLowestValidPrices(defaultPricingCtx)>

    <#assign finalPrice=product.price.getLowestFinalPrice(defaultPricingCtx)!0>
    <#assign retailPrice=lowestPrices['retail_price']!0>
    <#assign clubPrice=lowestPrices['club_price']!0>
    <#assign specialPrice=lowestPrices['special_price']!0>
    <#assign selloutPrice=lowestPrices['sellout_price']!0>
<#else>
    <#assign prices=product.price.getValidPrices(defaultPricingCtx)>

    <#assign finalPrice=product.price.finalPrice!0.0>
    <#assign retailPrice=prices['retail_price']!0.0>
    <#assign clubPrice=prices['club_price']!0.0>
    <#assign specialPrice=prices['special_price']!0.0>
    <#assign selloutPrice=prices['sellout_price']!0.0>
</#if>

<#assign finalBasePrice=retailPrice>
<#assign finalSpecialPrice=0>
<#assign percentOff=0>


<@cp_product_price product_id="${product.id}" />
<#if promotionPrice?? >
    <#if salePrice?? && promotionPrice lt salePrice || !salePrice?? || salePrice == 0>
        <#assign salePrice=promotionPrice>
    </#if>
</#if>


<#if (!product.programme || product.price.hasValidPrice())>

    <#if specialPrice?? && (specialPrice > 0)>
        <#assign finalSpecialPrice=specialPrice>
    <#elseif selloutPrice?? && (selloutPrice > 0)>
        <#assign finalSpecialPrice=selloutPrice>
    <#elseif clubPrice?? && (clubPrice > 0)>
        <#assign finalSpecialPrice=clubPrice>
    <#else>
        <#assign finalSpecialPrice=retailPrice>
    </#if>

    <#if (finalBasePrice > 0)>
        <#assign percentOff=((((finalBasePrice-finalSpecialPrice)*100)/(finalBasePrice*100))*100)?floor />
    </#if>

</#if>