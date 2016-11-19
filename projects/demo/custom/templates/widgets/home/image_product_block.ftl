<#assign bgBoxClass = "">
<#assign priceBoxClass = "">
<#include "init_prices.ftl"/>

<#if (bgBoxClass == "") >
	<#if (salePrice > 0)>
        <#assign bgBoxClass = "item-bg-sale">
        <#assign priceBoxClass = "item-new-price">
	</#if>
</#if>

<div class="product-widget-block-common" style="background: url(<@catMediaURL uri="${product.cat1ImageURI!}" width=494 height=494 />) center top no-repeat">

    <div class="item-common-info ${bgBoxClass} <#if (bgBoxClass == "") > rudy-border</#if>">
        <div class="item-photo">
            <a href="<@url target=product />">
        </div>
        <div class="item-info-icons">
        </div>
        <div class="item-title">
            <strong><a class="stroke-text" href="<@url target=product />"><@attribute src=product code="name" /></a></strong>
            <span><a class="stroke-text" href="<@url target=product />"><@attribute src=product code="name2" /></a></span>
        </div>

    <#include "list_price_container.ftl"/>
    </div>
</div>
