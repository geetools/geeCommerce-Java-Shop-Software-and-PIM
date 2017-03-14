<#if wProductPromotion?? && wProducts??>

    <div id="${widgetId}">
        <#list wProducts as product>

            <@skin path="images/catalog/no_img_250.jpg" var="cat1ImageURI" />

            <div class="col-prd-promo">
                <div class="prd-promo-item-outer">

                    <#if product.cat1ImageURI?? >
                        <#assign cat1ImageURI = product.cat1ImageURI>
                    <#else>
                        <#if product.bundle && product.mainBundleProduct?? && product.mainBundleProduct.cat1ImageURI?? >
                            <#assign cat1ImageURI = product.mainBundleProduct.cat1ImageURI>
                        </#if>
                    </#if>

                    <div class="prd-promo-item-inner" style="background: url(${cat1ImageURI!no_img_path}) center top no-repeat">
                        <div class="prd-promo-item-photo">
                            <a href="<@url target=product />"></a>
                        </div>
                        <div class="prd-promo-item-title">
                            <a href="<@url target=product />">${product.name} ${product.name2}</a>
                        </div>
                        <div class="prd-promo-item-desc">
                            <@attribute_exists src=product code="short_description">
		                                    <@attribute src=product code="short_description" truncate=75/>
	                                    </@attribute_exists>
                        </div>
                        <div class="prd-promo-item-price">
                            <#include "list_price.ftl" />
                        </div>
                    </div>
                </div>
            </div>

        </#list>
    </div>

</#if>



