<#if ipw_product??>
    <#if ipw_image??>
        <#assign ipwImageURI = ifw_image>
    <#else>
        <@skin path="images/catalog/no_img_250.jpg" var="ipwImageURI" />
        <#if ipw_product.cat1ImageURI?has_content>
            <#assign ipwImageURI = ipw_product.mainImage.webZoomPath>
      <#--      <@catMediaURL uri="${ipw_product.cat1ImageURI!no_img_path}" width=250 height=250 var="ipwImageURI" />-->
        </#if>
    </#if>
    <div class="ipw-item-outer default">
        <div class="ipw-item-inner" ><#--style="background: url(${ipwImageURI}) center top no-repeat"-->
            <div class="ipw-item-photo">
                <a href="<@url target=ipw_product />">
                    <img class="img-responsive img-fluid"src="${ipwImageURI}">
                </a>
            </div>
            <div class="ipw-item-title">
                <a href="<@url target=ipw_product />">${ipw_product.name} ${ipw_product.name2} </a>
            </div>
            <div class="ipw-item-desc">
                <@attribute_exists src=ipw_product code="description">
                    <@attribute src=ipw_product code="description" truncate=75/>
                </@attribute_exists>
            </div>
<#--            <div class="ipw-item-price">
                <#include "list_price.ftl" />
            </div>-->
        </div>
    </div>

</#if>


