<div id="pl-left-nav">
	<#if navItem??>
		<#if navItem.children?has_content>
			<#assign navItems = navItem.children>
		<#else>
			<#assign navItems = navItem.parent.children>
		</#if>
		
		<#if navItems?has_content>
		    <ul>
		        <#list navItems as navItemChild>
		        	<#if navItemChild.id == navItem.id>
		            	<li class="active">${navItemChild.displayLabel}</li>
		        	<#else>
		            	<li><a href="${navItemChild.displayURI}">${navItemChild.displayLabel}</a></li>
		            </#if>
		        </#list>
		    </ul>
			<hr class="pl-nav-line x-whl-border"/>
		</#if>
	</#if>
</div>