<#if ilw_image??>
    <#if ilw_link??>
        <a href="${ilw_link}">
            <img class="img-responsive img-fluid" src="${ilw_image}">
        </a>
    <#else>
        <img class="img-responsive img-fluid" src="${ilw_image}">
    </#if>
</#if>