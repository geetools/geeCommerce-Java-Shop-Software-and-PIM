<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>

<@layout.onecolumn title=title metaDescription=metaDescription metaRobots=metaRobots metaKeywords=metaKeywords>
    <div class="home-container">
        <@content id="${contentId?string}"/>
    </div>

</@layout.onecolumn>