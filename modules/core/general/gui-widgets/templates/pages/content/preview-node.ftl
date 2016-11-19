<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<#assign productList = actionBean.productList>

<@layout.onecolumn>

    <div class="row container-content">
        <div id="preview-node">
            <@html html="${htmlContent}" />
        </div>
    </div>

</@layout.onecolumn>
