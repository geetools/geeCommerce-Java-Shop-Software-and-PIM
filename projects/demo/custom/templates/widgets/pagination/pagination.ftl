<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>

<div class="page-list">
    <div class="pagelisting">

        <div class="pagingPages">
        <#assign abNumPages = actionBean.numPages>
        <#assign abP = actionBean.page>
        <#if abNumPages gt 1>
            <#if abP gt 1 >
                <a href="${self.filterBaseVanillaURI()}" class="pagination-left glyphicon glyphicon-chevron-left">
                    <@s.param name="limit" value="${actionBean.limit}"/>
                    <@s.param name="page" value="${abP - 1}"/>
                </a>
            <#else>
                <span class="pagination-left"></span>
            </#if>

            <#if abNumPages gt 7>
                <#if abP lte 4>
                    <#list 1..abNumPages as page>
                        <#if page gt 5>
                            <#break>
                        </#if>
                        <#if abP == page>
                            <strong class="active">${page}</strong>
                        <#else>
                            <@s.link href="${self.filterBaseVanillaURI()}">${page}
                                <@s.param name="limit" value="${actionBean.limit}"/>
                                <@s.param name="page" value="${page}"/>
                            </@s.link>
                        </#if>
                    </#list>
                    ...
                    <@s.link  href="${self.filterBaseVanillaURI()}">${abNumPages}
                        <@s.param name="limit" value="${actionBean.limit}"/>
                        <@s.param name="page" value="${abNumPages}"/>
                    </@s.link>
                <#elseif abP + 3 gte abNumPages>
                    <@s.link href="${self.filterBaseVanillaURI()}">${1}
                        <@s.param name="limit" value="${actionBean.limit}"/>
                        <@s.param name="page" value="${1}"/>
                    </@s.link>...
                    <#list (abNumPages - 4)..abNumPages as page>
                        <#if abP == page>
                            <b >${page}</b>
                        <#else>
                            <@s.link href="${self.filterBaseVanillaURI()}">${page}
                                <@s.param name="limit" value="${actionBean.limit}"/>
                                <@s.param name="page" value="${page}"/>
                            </@s.link>
                        </#if>
                    </#list>
                <#else>
                    <@s.link href="${self.filterBaseVanillaURI()}">${1}
                        <@s.param name="limit" value="${actionBean.limit}"/>
                        <@s.param name="page" value="${1}"/>
                    </@s.link>...
                    <#list (abP - 2)..(abP + 2) as page>
                        <#if abP == page>
                            <b>${page}</b>
                        <#else>
                            <@s.link href="${self.filterBaseVanillaURI()}">${page}
                                <@s.param name="limit" value="${actionBean.limit}"/>
                                <@s.param name="page" value="${page}"/>
                            </@s.link>
                        </#if>
                    </#list>
                    ...
                    <@s.link href="${self.filterBaseVanillaURI()}">${abNumPages}
                        <@s.param name="limit" value="${actionBean.limit}"/>
                        <@s.param name="page" value="${abNumPages}"/>
                    </@s.link>
                </#if>
            <#else>
                <#list 1..abNumPages as page>
                    <#if abP == page>
                        <b>${page}</b>
                    <#else>
                        <@s.link href="${self.filterBaseVanillaURI()}">${page}
                            <@s.param name="limit" value="${actionBean.limit}"/>
                            <@s.param name="page" value="${page}"/>
                        </@s.link>
                    </#if>
                </#list>
            </#if>
            <#if abP lt abNumPages >
                <a href="${self.filterBaseVanillaURI()}" class="pagination-right glyphicon glyphicon-chevron-right">
                    <@s.param name="limit" value="${actionBean.limit}"/>
                    <@s.param name="page" value="${abP + 1}"/>
                </a>
            <#else>
                <span class="pagination-right"></span>
            </#if>
        </#if>
        </div>
        <!-- pagingPages -->

    </div>
    <!-- pagelisting -->
</div><!-- page-list -->