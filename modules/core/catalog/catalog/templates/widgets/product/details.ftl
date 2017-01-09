<#if productDetails??>
    <#assign col = 1 />
    <#list productDetails?chunk(productDetails?size / 2 + 1 ) as row>
        <#if col == 2>
            <#assign col2=row />
        </#if>

        <#if col == 1>
            <#assign col1=row />
            <#assign col = 2 />
        </#if>
    </#list>
</#if>


<div class="row">
    <div class="col-xs-12">
        <#assign detailCol = 1 />
        <#include "detail_col.ftl" />
    </div>
<!--    
    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
        <#assign detailCol = 2 />
        <#include "detail_col.ftl" />
    </div>
-->    
</div>

