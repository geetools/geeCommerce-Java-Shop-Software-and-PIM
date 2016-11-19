<#assign isFilterActive = false>

<#if productListResult?? && productListResult.facets??>
	<#list productListResult.facets as facet>
	
		<#if (facet.entryCount > 1)>
	
			<#assign isMulti = self.isMultiFilter(facet.code) />
			
			<div class="product-list-selected-filters">
				<ul<#if (facet.entryCount > 8)> class="scrollable-list"</#if>>
					<#list facet.entries as entry>
						<#if self.isActive(facet.code, entry.label)>
							<#assign isFilterActive = true>
							<li>${entry.label}<a href="${self.filterRemoveURI(facet.code, entry.label)}" class="searchFilterLink">X</a></li>
						</#if>
					</#list>
				</ul>
			</div>
		</#if>
	</#list>
	<#if isFilterActive>
		<div class="pull-left"><a href="${self.filterVanillaURI()}" class="cat-pl-reset-filter-link"><@message text='Cancel selection' lang="en" text2='Auswahl aufheben' lang2="de" /></a></div>
	</#if>
</#if>