<#if wText??>
    <#if wLink?? && wStyle??>
    <div style="${wStyle}">
        <a href="${wLink}"  >${wText}</a>
    </div>
    </#if>

    <#if wLink?? && !wStyle??>
        <a href="${wLink}"  >${wText}</a>
    </#if>
</#if>