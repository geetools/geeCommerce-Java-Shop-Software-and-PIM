
<#if isProduct || isProductContactForm || isProductList>
    <a title="<@message text="Startseite" editable=false/>" href="/"><@message text="Startseite"/></a>
<#else>
	<a title="<@message text="Startseite" editable=false/>" href="/"><@message text="Startseite"/></a>    
</#if>

<#if breadcrumbs?has_content>

	<#assign keys = breadcrumbs?keys>
	
	<#list keys as key>
		<#if key_has_next>
   			<a href="<@url path="${key}"/>" title="${breadcrumbs[key]}">${breadcrumbs[key]}</a>
   		<#else>
   			<span>${breadcrumbs[key]}</span>
   		</#if>
	</#list>
	
<#else>

	<#if (isProduct || isProductContactForm)>
	
		<#if navItems?has_content>
			<#list navItems as navItem>
	  			| <a href="${navItem.displayURI}" title="${navItem.displayLabel}">${navItem.displayLabel}</a>
	    	</#list>
		</#if>
	
		<#-- Product name for the last part of the breadcrumb navigation. -->
	    <#if (isProduct && product??)>
		    <#if childProduct??>
				| <a href="<@url target=product />" title="<@attribute src=product code="name"/>"><@attribute src=product code="name"/></a>
			<#else>
				| <span><@attribute src=product code="name"/></span>
			</#if>
		</#if>
		
		<#-- Product name for the last part of the breadcrumb navigation. -->
	    <#if (isProduct && product?? && childProduct??)>
			| <span><@attribute src=childProduct code="name"/> <@attribute src=childProduct code="name2"/></span>
		</#if>
	
	    <#if (isProductContactForm && product??)>
			| <a href="<@url target=product />"><@attribute src=product code="name"/></a>
		</#if>
		
	</#if>
	
	<#if isProductList>
	
		<#if navItems?has_content>
			<#list navItems as navItem>
				|
				<#if navItem_has_next>
	       			<a href="${navItem.displayURI}" title="${navItem.displayLabel}">${navItem.displayLabel}</a>
	       		<#else>
	       			<span>${navItem.displayLabel}</span>
	       		</#if>
	    	</#list>
		</#if>
	
	</#if>
	
</#if>
	