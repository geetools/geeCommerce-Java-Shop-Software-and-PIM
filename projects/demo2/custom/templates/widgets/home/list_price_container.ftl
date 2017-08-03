<#if (!product.programme || product.price.hasValidPrice())>

    <#if (isAdvertised?? && isAdvertised == "J" && salePrice > 0)>
    <div class="item-old-price">${retailPrice?string["0.00"]?replace(",00", ",-")}
        <div class="line category-sprite">
        </div>
    </div>
    <div class="item-new-price category-sprite">
        <div>${salePrice?string["0.00"]?replace(",00", ",-")}</div>
    </div>
    <#elseif (salePrice > 0) || (specialPrice > 0)>
    <div class="item-old-price">${retailPrice?string["0.00"]?replace(",00", ",-")}
        <div class="line category-sprite"></div>
    </div>
    <div class="item-only-new-price category-sprite">
        <div>${salePrice?string["0.00"]?replace(",00", ",-")}</div>
    </div>
    <#elseif priceBoxClass == "item-new-price">
    <div class="item-only-new-price category-sprite">
        <div>${finalPrice?string["0.00"]?replace(",00", ",-")}</div>
    </div>
    <#else>
    <div class="item-only-new-price category-sprite">
        <div>${finalPrice?string["0.00"]?replace(",00", ",-")}</div>
    </div>
    </#if>
<#else>

</#if>
