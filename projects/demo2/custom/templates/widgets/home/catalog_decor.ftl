<#assign showColorIcon = false>
<#assign showCogIcon = false>
<#list product.getChildren() as variant>
    <@attribute_exists src=variant code="teppichgroesse">
        <#assign showCogIcon = true >
    </@attribute_exists>
    <#if showCogIcon >
        <#break>
    </#if>
</#list>
<#list product.getChildren() as variant>

    <@attribute_exists src=variant code="dekor_kombination">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <@attribute_exists src=variant code="jalousien_farbe">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <@attribute_exists src=variant code="rollo_farbe">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <@attribute_exists src=variant code="teppichfarbe">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <@attribute_exists src=variant code="wohndecken_farbe">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <@attribute_exists src=variant code="suchfarbe">
        <#assign showColorIcon = true>
    </@attribute_exists>
    <#if showColorIcon>
        <#break>
    </#if>
</#list>


<#if showCogIcon>
<span class="sprite cogIcon"></span>
</#if>
<#if showColorIcon>
<span class="sprite colorIcon"></span>
</#if>