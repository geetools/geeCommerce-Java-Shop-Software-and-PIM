<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<#assign productList = actionBean.searchResult>
<#--<#assign searchPhrase = actionBean.K>-->

<#--<@cache key="product_list_${actionBean.id?string}">-->

    <@layout.onecolumn  >

    <div class="row container-category">
        <#if products??>

            <div class="col-xs-12">
                <strong> <@message text='Suchergebnisse' /> (${actionBean.totalNumResults})</strong>
                <p><@message text='für '/>  &#8222;${RequestParameters.k}&#8221;</p>

            </div>

            <div class="col-xs-12 col-sm-3 col-md-3">
                <#--<h2><@print src=productList value="productList.label.str" /></h2>-->
                <#--<hr class="pl-nav-header-line x-whl-border"/>-->
<#---->
                <#--<@product_list_navigation key="top_product_list_navigation" product_list_id="${actionBean.id?string}" />-->
                <#--<@product_list_top_filter />-->
            </div>
            <div class="col-xs-12 col-sm-9 col-md-9">

                <div class="col-xs-12 col-sm-12 col-md-4 cat-pl-pagination-top">
                    <@pagination />
                </div>

                <div class="col-xs-12 col-sm-12 col-md-8 cat-pl-selected-filters">

                    <#--<#assign isFilterActive = false>-->

                    <#--<#if actionBean.searchResult?? && actionBean.searchResult.facets??>-->
                        <#--<#list actionBean.searchResult.facets as facet>-->

                            <#--<#if (facet.entryCount > 1)>-->

                                <#--<#assign isMulti = self.isMultiFilter(facet.code) />-->

                                <#--<div class="product-list-selected-filters">-->
                                    <#--<ul<#if (facet.entryCount > 8)> class="scrollable-list"</#if>>-->
                                        <#--<#list facet.entries as entry>-->
                                            <#--<#if self.isActive(facet.code, entry.label)>-->
                                                <#--<#assign isFilterActive = true>-->
                                                <#--<li>${entry.label}<a href="${self.filterRemoveURI(facet.code, entry.label)}" class="searchFilterLink">X</a></li>-->
                                            <#--</#if>-->
                                        <#--</#list>-->
                                    <#--</ul>-->
                                <#--</div>-->
                            <#--</#if>-->
                        <#--</#list>-->
                        <#--<#if isFilterActive>-->
                            <#--<div class="pull-left"><a href="${self.filterVanillaURI()}" class="cat-pl-reset-filter-link">Auswahl aufheben</a></div>-->
                        <#--</#if>-->
                    <#--</#if>-->

                    <#--<@product_list_top_filter />-->
                </div>

                <div class="clearfix">
                </div>

                <#list products as product>

                    <@skin path="images/catalog/no_img_250.jpg" var="cat1ImageURI" />
                    <#if product.cat1ImageURI?has_content>
                        <@catMediaURL uri="${product.cat1ImageURI!no_img_path}" width=250 height=250 var="cat1ImageURI" />
                    </#if>

                    <div class="col-xs-12 col-sm-6 col-md-4 col-category">
                        <div class="cat-item-outer">
                            <div class="cat-item-inner" style="background: url(${cat1ImageURI}) center top no-repeat">
                                <div class="cat-item-photo">
                                    <a href="<@url target=product />"></a>
                                </div>
                                <div class="cat-item-title">
                                    <a href="<@url target=product />">${product.name} ${product.name2}</a>
                                </div>
                                <div class="cat-item-desc">
                                    <@attribute_exists src=product code="short_description">
		                                    <@attribute src=product code="short_description" truncate=75/>
	                                    </@attribute_exists>
                                </div>
                                <div class="cat-item-price">
                                    <#include "../product/list_price.ftl" />
                                </div>
                            </div>
                        </div>
                    </div>

                </#list>

            </div>

            <div class="col-xs-12 cat-pl-pagination-bottom">
                <@pagination />
            </div>

        <#else>
         <@message text=' Your search' />  &#8222;<b>${RequestParameters.k}</b>&#8221; <@message text=' did not match any products.' />
        </#if>
    </div>
    </@layout.onecolumn>

<#--</@cache>-->
<#--<script>-->
    <#--$(".short_description").each(function (index) {-->
        <#--var arr = $(this).text().split(", ");-->
        <#--var result = "";-->
        <#--for(var i = 0; i < arr.length; i++) {-->
            <#--result += arr[i];-->
            <#--if (i < arr.length - 1) {-->
                <#--result += "<div style='height:2px;'></div>";-->
            <#--}-->
        <#--}-->
        <#--$(this).html(result);-->
    <#--});-->
<#--</script>-->
