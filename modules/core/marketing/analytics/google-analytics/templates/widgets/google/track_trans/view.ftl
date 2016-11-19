<#if (order?? && order.invoiceOrderAddress??)>
	<#assign invoiceAddress = order.invoiceOrderAddress />

	<#assign trans_id = order.orderNumber />
	
	<#if shipment.carrierCode == "instore_pickup">
		<#assign retail_store = order.shipment.optionName?js_string?replace("\\'", "\'") />
	<#else>
		<#assign retail_store = "" />
	</#if>
	
	<#assign item_total = order.calculationResult.gross_subtotal?c />
	<#assign vat = 0 />
	<#assign postage = order.calculationResult.gross_shipping_amount?c />
	<#assign city = invoiceAddress.city?js_string?replace("\\'", "\'") />
	<#assign region = invoiceAddress.city?js_string?replace("\\'", "\'") />
	<#assign country = "Ceska Republika" />

<script type="text/javascript">
		_gaq.push(['_addTrans',	"${trans_id}",	"${retail_store}",	"${item_total}",	"${vat}",	"${postage}",	"${city}",	"${region}", "${country}"]);

	<#list order.orderItems as item>
		<@print src=order.calculationResult.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['cart_price'];" var="item_price"/>
		<#assign article_number = item.articleNumber />
		<#assign name = item.name />
		<#assign price = (item.getProductPrice()?c) />
		<#assign qty = item.quantity?c />
		_gaq.push(['_addItem',	"${trans_id}",	"${article_number}",	"${name}",	"<@product_navitem_name src=item.product />",	"${item_price?c}",	"${qty}"]);	
	</#list>
		_gaq.push(['_trackTrans']);
</script>

</#if>