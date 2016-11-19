<#if ipw_product??>
    <#assign product=ipw_product>
    <div id="popover" class="product-widget-block">
        <#include "image_product_block.ftl"/>
        <div id="popover" class="product-widget-block-more">
            <#include "image_product_block.ftl"/>

            <div class="product-widget-block-additional">
                <@attribute_exists src=product code="short_description">
                    <div class="item-color short-description" >
                        <@attribute src=product code="short_description" make="list"/>
                    </div>
                </@attribute_exists>

            </div>
        </div>
    </div>
</#if>