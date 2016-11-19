<#if !wProduct??>
    <#assign wProduct=product >
</#if>

<div class="cms-product-review">
    <div class="summary">
        <@import uri="/review/summary/${wProduct.id}"></@import>
    </div>
</div>