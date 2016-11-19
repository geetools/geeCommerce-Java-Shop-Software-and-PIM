<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>

    <@layout.onecolumn pageModel=productList>

		<div class="row container-category">
		
			<#if products??>
			
				<div class="col-xs-12 col-sm-3 col-md-3">
					<h2><@print src=productList value="productList.label.str" /></h2>
					<hr class="pl-nav-header-line x-whl-border"/>
				
					<@product_list_navigation key="top_product_list_navigation" product_list_id="${productList.id?string}" />
					<@product_list_filter/>
				</div>
				<div class="col-xs-12 col-sm-9 col-md-9">

	                <div class="col-xs-12 col-sm-12 col-md-4 cat-pl-pagination-top">
						<@pagination />
					</div>

	                <div class="col-xs-12 col-sm-12 col-md-8 cat-pl-selected-filters">
						<@product_list_filter view="product/list_selected_filters"/>
					</div>
					
	                <div class="clearfix">
					</div>
					
					<#list products as product>
					
						<@skin path="images/catalog/no_img_250.jpg" var="cat1ImageURI" />
						
	                    <div class="col-xs-12 col-sm-6 col-md-4 col-category">
							<div class="cat-item-outer">
								<div class="cat-item-inner" style="background: url(${product.cat1ImageURI!no_img_path}) center top no-repeat">
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
								    	<#include "list_price.ftl" />
								    </div>
			                    </div>
		                    </div>
	                    </div>
	
					</#list>

				</div>
				
                <div class="col-xs-12 cat-pl-pagination-bottom">
					<@pagination />
				</div>

			</#if>

    </@layout.onecolumn>