// Configuration
// --------------------------------------------------------------------------

// What is the name of the shipping-amount attribute we want to use?
def shippingAmountAttrCode = ctx.cpStr_("pricing/shipping-amount-attribute")
// Does the shipping-amount include tax?
def shippingAmountIncludesTax = ctx.cpBool_("pricing/shipping-amount-includes-tax")

// Calculation
// --------------------------------------------------------------------------

def netShippingAmount = 0D
def grossShippingAmount = 0D

// What do we start with - with tax or without?
if(shippingAmountIncludesTax) {
	grossShippingAmount = ctx.double_(shippingAmountAttrCode)
} else {
	netShippingAmount = ctx.double_(shippingAmountAttrCode)
}

// We need to find out the subtotals in proportion to the tax-rates. 
def subtotalPerTaxRate = [:];
ctx.itemResults.each() { itemResult ->
	if(subtotalPerTaxRate[itemResult['tax_rate']] == null) {
		subtotalPerTaxRate[itemResult['tax_rate']] = 0D
	}
	
	// Add the subtotals together, grouped by the tax-rate.
	if(shippingAmountIncludesTax) {
		subtotalPerTaxRate[itemResult['tax_rate']] += itemResult['gross_subtotal'];
	} else {
		subtotalPerTaxRate[itemResult['tax_rate']] += itemResult['net_subtotal'];
	}
}

def taxRateWithHighestSubtotal = 0
def hightestSubtotal = 0

// Find out which tax-rate has the highest subtotal
subtotalPerTaxRate.each { taxRate, proportionalSubtotal ->
	if(hightestSubtotal == 0 || proportionalSubtotal > hightestSubtotal) {
		hightestSubtotal = proportionalSubtotal
		taxRateWithHighestSubtotal = taxRate
	}
}

// Add or remove tax depending on whether tax has been configured with or without it.
if(shippingAmountIncludesTax) {
	// Deduct the current tax-rate to get the net-price.
	netShippingAmount = grossShippingAmount / ((taxRateWithHighestSubtotal / 100) + 1)
} else {
	// Add the current tax-rate to get the gross-price.
	grossShippingAmount = netShippingAmount * ((taxRateWithHighestSubtotal / 100) + 1)
}

// Add values to the result object
ctx.results["net_shipping_amount"] = Math.round(netShippingAmount * 100) / 100
ctx.results["gross_shipping_amount"] = Math.round(grossShippingAmount * 100) / 100
ctx.results["shipping_tax_amount"] = ctx.results["gross_shipping_amount"] - ctx.results["net_shipping_amount"]

