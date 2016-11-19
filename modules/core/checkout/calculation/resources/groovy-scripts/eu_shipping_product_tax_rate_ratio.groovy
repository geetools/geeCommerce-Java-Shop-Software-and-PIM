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

// Now we calculate the net or gross shipping amount depending on what we started with.
subtotalPerTaxRate.each { taxRate, proportionalSubtotal ->
	if(shippingAmountIncludesTax) {
		// Work out the proportion of this subtotal in comparison to the whole subtotal.
		def percentRatio = Math.round((proportionalSubtotal / ctx.results['gross_subtotal']) * 100);
		// Use the percent-ratio to calculate the same proportion for the shipping-amount.
		def grossProportionalShippingAmount = grossShippingAmount * (percentRatio / 100)
		// Deduct the current tax-rate to get the net-price.
		def netProportionalShippingAmount = grossProportionalShippingAmount / ((taxRate / 100) + 1)
		
		// Add all the proportional net-shipping-amounts together.
		netShippingAmount += netProportionalShippingAmount
	} else {
		// Work out the proportion of this subtotal in comparison to the whole subtotal.
		def percentRatio = Math.round((proportionalSubtotal / ctx.results['net_subtotal']) * 100);
		// Use the percent-ratio to calculate the same proportion for the shipping-amount.
		def netProportionalShippingAmount = netShippingAmount * (percentRatio / 100)
		// Add the current tax-rate to get the gross-price.
		def grossProportionalShippingAmount = netProportionalShippingAmount * ((taxRate / 100) + 1)
		
		// Add all the proportional gross-shipping-amounts together.
		grossShippingAmount += grossProportionalShippingAmount
	}
}

// Add values to the result object
ctx.results["net_shipping_amount"] = Math.round(netShippingAmount * 100) / 100
ctx.results["gross_shipping_amount"] = Math.round(grossShippingAmount * 100) / 100
ctx.results["shipping_tax_amount"] = ctx.results["gross_shipping_amount"] - ctx.results["net_shipping_amount"]

