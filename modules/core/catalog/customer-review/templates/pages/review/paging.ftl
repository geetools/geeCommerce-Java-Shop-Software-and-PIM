<div class="pagingContainer">
    <div class="pagingInfo">
    <@message text="Reviews" lang="en" text2="Kundenrezensionen" lang2="de" /> ${pagingContext.resultsFrom}
        - ${pagingContext.resultsTo}
    <@message text="of" lang="en" text2="von" lang2="de" /> ${pagingContext.totalNumResults}
    </div>

    <div class="pagingPages">
        <span><@message text="Pages" lang="en" text2="Seiten" lang2="de" />:</span>
        <div class="row">
            <div class="col-xs-12">
            <#list 1..pagingContext.numPages as p>
                <#if pagingContext.page == p>
                    <div class="pageNumber">${p}</div>
                <#else>
                    <div class="pageNumber"><a class="btn-link"
                                               href="${pagingContext.pagingUri}?limit=${pagingContext.limit}&page=${p}">${p}</a>
                    </div>
                </#if>
            </#list>
            </div>
        </div>
    </div>
</div>

