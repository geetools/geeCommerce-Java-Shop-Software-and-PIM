<#if productListResult?? && productListResult.facets??>
		<button id="btn-product-list-fiter" class="btn btn-primary hidden-sm hidden-md hidden-lg" type="button" data-toggle="collapse" data-target="#product-list-fiters" aria-expanded="false" aria-controls="product-list-fiters"><@message text='Refine by' lang="en" text2='Auswahl verfeinern' lang2="de" /></button>
	
		<div id="product-list-fiters" class="collapse">

		<#list productListResult.facets as facet>

			<#if (facet.entryCount > 1)>
		
				<#assign isMulti = self.isMultiFilter(facet.code) />
				
				<div class="product-list-filter">
					<div>
						<strong>${facet.label}</strong>
						<span><a href="${self.resetURI(facet.code)}"><@message text='Cancel selection' lang="en" text2='Auswahl aufheben' lang2="de" /></a></span>
					</div>
					
					<#if (facet.entryCount > 8)>
						<div class="pl-option-filter">
	
							<div class="input-group">
	            				<input type="text" class="form-control" name="pl_filter_find_option" />
	            				<div class="input-group-btn">
	                				<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>
	            				</div>
		        			</div>
						</div>
					</#if>
					
					<ul<#if (facet.entryCount > 8)> class="scrollable-list"</#if>>
						<#list facet.entries as entry>
							<#if ((entry.count == 0 && !isMulti) || (entry.count == 0 && isMulti && !self.isActive(facet.code, entry.label)))>
								<#if (entry.nonMultiCount > 0) && isMulti && !facet.isRange()>
									<li class="off"><a href="${self.filterURI(facet.code, entry.label)}" class="searchFilterLink">${entry.label}</a></li>
								<#else>
									<li class="inactive">${entry.label}</li>
								</#if>
							<#elseif self.isActive(facet.code, entry.label)>
								<li class="on"><a href="${self.filterRemoveURI(facet.code, entry.label)}" class="searchFilterLink">${entry.label}</a></li>
							<#elseif !self.isActive(facet.code, entry.label) && (entry.nonMultiCount > 0) && !isMulti && !facet.isRange() && (self.numActive(facet.code) > 0)>
								<li class="inactive">${entry.label}</li>
							<#else>
								<#if facet.isRange()>
									<li class="off"><a href="${self.filterRangeURI(facet.code, entry.rangeFrom, entry.rangeTo)}" class="searchFilterLink">${entry.label}</a></li>
								<#else>
									<li class="off"><a href="${self.filterURI(facet.code, entry.label)}" class="searchFilterLink">${entry.label}</a></li>
								</#if>
							</#if>
						</#list>
					</ul>
				</div>
			</#if>
		</#list>
		
		</div>
		
	</#if>
