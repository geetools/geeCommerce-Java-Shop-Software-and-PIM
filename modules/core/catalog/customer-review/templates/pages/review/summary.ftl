
<#if product??>
<div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
    <h3><@message text="Customer Reviews" lang="en" text2="Kundenrezensionen" lang2="de" /></h3>
    <span class="review-summary-name"><a href="<@uri target=product />"><@attribute src=product code="name" parent=true /></a></span>
    <span class="review-summary-name2"><a href="<@uri target=product />"><@attribute src=product code="name2" parent=true /></a></span>

    <div>
        <span class="review-summary-numreviews">${total} <@message text="Customer Reviews" lang="en" text2="Kundenrezensionen" lang2="de" /></span>
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
                        ${star} <@message text="star" lang="en" text2="Stern" lang2="de" />
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

    <div class="avg-customer-review">
        <strong><@message text="Average Customer Review" lang="en" text2="Durchschnittliche Kundenrezension" lang2="de" /></strong>
        <div class="rateit" data-rateit-value="${average}" data-rateit-ispreset="true" data-rateit-readonly="true"></div>
        <span>(${total} <@message text="customer reviews" lang="en" text2="Kundenrezensionen" lang2="de" />)</span>
        <#if !hasReview?? || !hasReview>
            <p><@message text="Share your thoughts with other customers" lang="en" text2="Sagen Sie Ihre Meinung zu diesem Artikel" lang2="de" /></p>
            <p><a class="btn-link" href="/review/new/${product.id}" id="createReview"><@message text="Write a customer review" lang="en" text2="Kundenrezension verfassen" lang2="de" /></a></p>
        </#if>
    </div>

</div>
</#if>