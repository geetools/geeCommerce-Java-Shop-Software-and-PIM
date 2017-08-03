<#assign deliveryType = true />

<#assign product = product/>
<#if (product.variantMaster && lowestPriceProduct??)>
    <#assign product = lowestPriceProduct />
</#if>

<@when src=product script="shipping/type/is_delivery" >
    <span class="sprite deliveryIcon"></span>
    <#assign deliveryType = false />
</@when>
<#if deliveryType>
    <@when src=product script="shipping/type/is_bulky" >
        <span class="sprite bulkyIcon"></span>
        <#assign deliveryType = false />
    </@when>
</#if>
<#if deliveryType>
    <@when src=product script="shipping/type/is_package" >
        <span class="sprite packageIcon"></span>
        <#assign deliveryType = false />
    </@when>
</#if>
<#if deliveryType>
    <span class="sprite deliveryIcon"></span>
</#if>