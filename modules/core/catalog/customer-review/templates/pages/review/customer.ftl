<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<@layout.onecolumn>

    <#if customer?? >
    <div>
        <h2><@message text="Reviews by" lang="en" text2="Bewertungen von" lang2="de" /> ${customer.forename} ${customer.surname}</h2>
    </div>
    <div>
        <#if reviews??  && reviews?has_content>
            <div class="reviewPagingWrapper">
            <#include "paging.ftl" />
            </div>
            <#list reviews as item>
                <div class="customerReview">
                    <div class="reviewHelpfulness">${item.thinkHelpfulCount}
                        <@message text="of" lang="en" text2="von" lang2="de" /> ${item.thinkHelpfulCount + item.thinkUnhelpfulCount}
                        <@message text="people found the following review helpful" lang="en" text2="fanden die folgende Rezension hilfreich" lang2="de" />
                    </div>
                    <div class="reviewProduct">
                        <@message text="Review on:" lang="en" text2="Kundenbewertung auf:" lang2="de" />
                        <strong><a href="<@url target=item.product />"> ${item.product.attr("name").val}</a></strong>
                    </div>
                    <div class="reviewHeadlineWrapper">
                        <div class="rateit" data-rateit-value="${item.rating}" data-rateit-ispreset="true"
                             data-rateit-readonly="true" style="display: inline-block;"></div>
                        <div class="reviewHeadline" style="display: inline-block;">${item.headline}</div>
                    </div>
                    <div class="reviewText">
                    ${item.reviewHtml}
                    </div>
                    <#if canEdit>
                        <div class="reviewEditActions">
                            <a class="btn"
                               href="/review/edit/${item.id}"><@message text="Edit" lang="en" text2="Bearbeiten" lang2="de" /></a>
                            <a href="/review/delete/${item.id}"
                               name="deleteReview"><@message text="Delete" lang="en" text2="Löschen" lang2="de" /></a>
                        </div>
                    </#if>
                </div>
            </#list>
            </br>
            <div class="reviewPagingWrapper">
            <#include "paging.ftl" />
            </div>
        <#else>
            <@message text="Customer has no reviews." lang="en" text2="Der Kunde hat keine Bewertungen." lang2="de" />
        </#if>
    </div>


    </#if>
</@layout.onecolumn>