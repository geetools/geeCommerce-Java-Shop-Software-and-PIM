
<#if isProduct || isProductContactForm || isProductList>
    <a title="<@message text="Homepage" lang="en" text2="Startseite" lang2="de" editable=false/>" href="/"><@message text="Homepage" lang="en" text2="Startseite" lang2="de"/></a>
<#else>
	<a title="<@message text="Homepage" lang="en" text2="Startseite" lang2="de" editable=false/>" href="/"><@message text="Homepage" lang="en" text2="Startseite" lang2="de"/></a>    
</#if>

<div class="container breadcrumbs hidden-phone hidden-phone-h">
    <div class="row">
        <div class="col-xs-12 hidden-phone hidden-phone-h">
            <ul class="nav navbar-nav">
                <li><a href="#" target="_blank"><@message text="Homepage" lang="en" text2="Startseite" lang2="de" editable=false/></a>|</li>

<#if breadcrumbs?has_content>

	<#assign keys = breadcrumbs?keys>
	
	<#list keys as key>
		<#if key_has_next>
   			<li><a href="<@url path="${key}"/>" title="${breadcrumbs[key]}">${breadcrumbs[key]}</a>|</li>
   		<#else>
   			<li>${breadcrumbs[key]}|</li>
   		</#if>
	</#list>
	
</#if>    

            </ul>
        </div>
    </div>
</div>
