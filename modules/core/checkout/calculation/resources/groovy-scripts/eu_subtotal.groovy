def netPriceSubtotal = 0
def grossPriceSubtotal = 0

ctx.itemResults.each() { itemResult ->
	netPriceSubtotal += itemResult['net_subtotal']
	grossPriceSubtotal += itemResult['gross_subtotal']
}

ctx.results["net_subtotal"] = Math.round(netPriceSubtotal * 100) / 100
ctx.results["gross_subtotal"] = Math.round(grossPriceSubtotal * 100) / 100
ctx.results["subtotal_tax_amount"] = ctx.results["gross_subtotal"] - ctx.results["net_subtotal"]
