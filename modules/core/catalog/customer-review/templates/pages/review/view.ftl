<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<@layout.onecolumn>

    <#if product??>

    <div class="row">
        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
            <h2><@message text="Customer Reviews" lang="en" text2="Kundenrezensionen" lang2="de" /></h2>
            <h3><a href="<@url target=product />">${product.attr("name").val}</a></h3>
            <div style="width: 400px;float: left;">
                <h2>${total} <@message text="Reviews" lang="en" text2="Kundenrezensionen" lang2="de" /></h2>
                <table class="stars-rating-summary">
                    <#list 0..4 as ind>
                        <#assign star=5-ind>
                        <#assign cnt=stars[4-ind]>
                        <#if total == 0>
                            <#assign percent=0>
                        <#else>
                            <#assign percent= cnt/total*100>
                        </#if>

                        <tr>
                            <td>
                                <#if star &gt; 1>
                            ${star} <@message text="stars" lang="en" text2="Sterne" lang2="de" />
                        <#else>
                                ${star} <@message text="star" lang="en" text2="Star" lang2="de" />
                                </#if>
                            </td>
                            <td class="stars-rating-percentage">
                                <div class="stars-rating-percentage-filled" style="width:${percent}%"></div>
                            </td>
                            <td>
                                (${cnt})
                            </td>
                        </tr>
                    </#list>
                </table>
            </div>
        </div>

        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
            <h2><@message text="Average Customer Rating" lang="en" text2="Durchschnittliche Bewertung" lang2="de"/></h2>
            <div class="rateit" data-rateit-value="${average}" data-rateit-ispreset="true" data-rateit-readonly="true"
                 style="display: inline-block;"></div>
            <div style="display: inline-block;">
                (${total} <@message text="customer reviews" lang="en" text2="Kundenrezension" lang2="de" />)
            </div>
            <#if !hasReview?? || !hasReview>
                <div><@message text="Share your thoughts with other customers" lang="en" text2="Sagen Sie Ihre Meinung mit anderen Kunden" lang2="de"/></div>
                <div>
                    <button class="btn" id="createReview"
                            productId="${product.id}"><@message text="Create your own review" lang="en" text2="Kundenrezension verfassen" lang2="de"/></button>
                </div>
            </#if>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12">
            <#if reviews??  &&  reviews?has_content>
                <div class="reviewPagingWrapper">
                    <div style="display: inline-block;">
                        <#include "paging.ftl" />
                    </div>
                    <div>
                        <a class="btn-link"
                           href="${pagingUri}?order=helpful"><@message text="Most Helpful First" lang="en" text2="Die meisten Reichste zuerst" lang2="de"/></a>
                        <span>|</span>
                        <a class="btn-link"
                           href="${pagingUri}?order=newest"><@message text="Newest First" lang="en" text2="Das neuste zuerst" lang2="de" /></a>
                    </div>
                </div>
                <#list reviews as item>
                    <div class="customerReview">
                        <div class="reviewHelpfulness">${item.thinkHelpfulCount}
                            of ${item.thinkHelpfulCount + item.thinkUnhelpfulCount} people found the following review
                            helpful
                        </div>
                        <div class="reviewHeadlineWrapper">
                            <div class="rateit" data-rateit-value="${item.rating}" data-rateit-ispreset="true"
                                 data-rateit-readonly="true" style="display: inline-block;"></div>
                            <div class="reviewHeadline" style="display: inline-block;">${item.headline}</div>
                        </div>
                        <div class="reviewCustomer">By ${item.customer.forename} ${item.customer.surname} <a
                                class="btn-link"
                                href="/review/customer/${item.customer.id}">(<@message text="See all reviews" lang="en" text2="Alle Rezensionen" lang2="de"/>)</a>
                        </div>
                        <div class="reviewText">
                        ${item.reviewHtml}
                        </div>
                        <#if !item.ratedByCustomer?? || !item.ratedByCustomer>
                            <div class="reviewMarkHelpfulness">
                                <div class="reviewMarkHelpfulnessHeader">
                                    <@message text="Help other customers find the most helpful reviews" lang="en" text2="Helfen Sie anderen Kunden finden die hilfreichsten Rezensionen" lang2="de"/></div>
                        <span reviewId="${item.id}"><@message text="Was this review helpful to you?" lang="en" text2="War diese Rezension hilfreich für Sie?" lang2="de" />
                            <button class="btn" reviewId="${item.id}"
                                    name="helpful"><@message text="Yes" lang="en" text2="Ja" lang2="de" /></button>
                            <button class="btn" reviewId="${item.id}"
                                    name="unhelpful"><@message text="No" lang="en" text2="Nein" lang2="de" /></button>

                            <span style="padding-left: 5px; padding-right: 5px;font-weight: bold;"> or </span>
                            <a class="btn-link"
                               href="/review/abuse/${item.id}"><@message text="Report abuse" lang="en" text2="Missbrauch melden" lang2="de" /></a>
                        </span>
                            </div>
                        </#if>
                    </div>
                </#list>
                <div class="reviewPagingWrapper">
                    <#include "paging.ftl" />
                </div>
            </#if>
        </div>
    </div>
    </#if>

<input id="review-view-create" type="hidden"
       value='<@message text="You should be logged in to create review" lang="en" text2="Sie sollten angemeldet sein Rezension zu erstellen" lang2="de"/>'/>
<input id="review-view-mark" type="hidden"
       value='<@message text="You should be logged in to mark review" lang="en" text2="Sie sollten angemeldet sein Rezension zu markieren" lang2="de"/>'/>
<input id="review-view-mark-thanks" type="hidden"
       value='<@message text="Thank you for your opinion" lang="en" text2="Vielen Dank für Ihre Meinung" lang2="de"/>'/>


</@layout.onecolumn>