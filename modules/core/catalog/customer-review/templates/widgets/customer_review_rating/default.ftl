<#if productId??>
	<div class="rateit_${productId} review-rating-short">
    	<div class="rateit" data-rateit-value="${average}" data-rateit-ispreset="true" data-rateit-readonly="true"></div>
    	<a class="customer-review-link" href="/review/view/${productId}" >${total} <@message text="Customer reviews" lang="en" text2="Kundenrezensionen" lang2="de" /></a>
	</div>
</#if>