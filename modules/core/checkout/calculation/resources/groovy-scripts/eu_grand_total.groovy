
ctx.results["net_grand_total"] = ctx.results["net_subtotal"] + ctx.results["net_shipping_amount"]
ctx.results["gross_grand_total"] = ctx.results["gross_subtotal"] + ctx.results["gross_shipping_amount"]
ctx.results["grand_total_tax_amount"] = ctx.results["gross_grand_total"] - ctx.results["net_grand_total"]
