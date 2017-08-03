<#assign finalPrice=0>
<#assign retailPrice=0>
<#assign specialPrice=0>
<#assign salePrice=0>

<#if product.variantMaster>
	<#-- As a variant-master does not have its own price, we get the lowest one from its child-products -->
	<#assign lowestPrices=product.price.getLowestValidPrices(defaultPricingCtx)!>

	<#assign finalPrice=product.price.getLowestFinalPrice(defaultPricingCtx)!0>
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
<#--<script type="application/javascript">-->
    <#--if('${product.specialPrice > 0}' || '${product.salePrice > 0}')){-->
        <#--$(".special-ptice-sticker").show();-->
        <#--$(".special-ptice-sticker .sale label").text('${retailPrice}' - '${finalPrice}');-->

    <#--} else {-->
        <#--$(".special-ptice-sticker").hide();-->
    <#--}-->
<#--</script>-->
