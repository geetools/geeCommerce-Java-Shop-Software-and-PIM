<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>

<#if product??>

<div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
    <#if reviews??  && reviews?has_content>
        <#list reviews as item>
            <div class="customerReview">
                <div class="reviewHelpfulness">${item.thinkHelpfulCount}
                    <@message text="of" lang="en" text2="von" lang2="de"/> ${item.thinkHelpfulCount + item.thinkUnhelpfulCount} <@message text="people found the following review helpful" lang="en" text2="Kunden fanden die folgende Rezension hilfreich" lang2="de" /></div>
                <div class="reviewHeadlineWrapper">
                    <div class="rateit" data-rateit-value="${item.rating}" data-rateit-ispreset="true" data-rateit-readonly="true"></div>
                    <strong class="reviewHeadline">${item.headline}</strong>
                </div>
                <div class="reviewCustomer"><@message text="By" lang="en" text2="Von" lang2="de" /> ${item.customer.forename} ${item.customer.surname}
                    , ${item.createdOn?date?string.long}
                    <a class="btn-link" href="/review/customer/${item.customer.id}">(<@message text="See all customer reviews" lang="en" text2="Alle Kundenrezensionen anzeigen" lang2="de" />)</a>
                </div>
                <div class="reviewText">
                ${item.reviewHtml}
                </div>
                <#if !item.ratedByCustomer?? || !item.ratedByCustomer>
                    <div class="reviewMarkHelpfulness">
                        <span reviewId="${item.id}"><@message text="Was this review helpful to you?" lang="en" text2="War diese Rezension für Sie hilfreich?" lang2="de" />
                            <button class="btn btn-primary" reviewId="${item.id}" name="helpful"><@message text="Yes" lang="en" text2="Ja" lang2="de" /></button>
                            <button class="btn" reviewId="${item.id}" name="unhelpful"><@message text="No" lang="en" text2="Nein" lang2="de" /></button>
                        <a class="btn-link" href="/review/abuse/${item.id}"><@message text="Report abuse" lang="en" text2="Missbrauch melden" lang2="de" /></a></span>
                    </div>
                </#if>
            </div>
        </#list>
    </#if>
</div>
</#if>
