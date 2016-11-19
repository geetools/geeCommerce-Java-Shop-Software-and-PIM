<#if !wProduct??>
    <#assign wProduct=product >
</#if>

<div class="cms-product-review">
    <div class="reviews">
        <@import uri="/review/product-view/${wProduct.id}"></@import>
    </div>
</div>
