<#if !wProduct??>
    <#assign wProduct=product >
</#if>

<#if !wAttributeCode??>
    <#assign wAttributeCode=attribute_code >
</#if>

<div class="prd-text">

<@attribute_exists src=wProduct code="${wAttributeCode}"  parent=true>
    <@attribute src=wProduct  code="${wAttributeCode}"  parent=true />
</@attribute_exists>
</div>


