// Configuration
// --------------------------------------------------------------------------

// What is the name of the shipping-amount attribute we want to use?
def shippingAmountAttrCode = ctx.cpStr_("pricing/shipping-amount-attribute")
// Does the shipping-amount include tax?
def shippingAmountIncludesTax = ctx.cpBool_("pricing/shipping-amount-includes-tax")
// Name of the attribute that contains the net-shipping-amount.
def netShippingAmountAttrCode = ctx.cpStr_("pricing/net-shipping-amount-attribute")
// Name of the attribute that contains the gross-shipping-amount.
def grossShippingAmountAttrCode = ctx.cpStr_("pricing/gross-shipping-amount-attribute")
// Allow calculation of tax? Possible values: always, never, if-not-exists.
def calculateTax = ctx.cpStr_("pricing/calculate-tax")
// Flatrate shipping tax-rate.
def shippingTaxRate = ctx.cpDouble_("pricing/shipping-tax-rate")


// Calculation
// --------------------------------------------------------------------------

def netShippingAmount = 0
def grossShippingAmount = 0

// Do not calculate tax if set in configuration to never do so. Both net and gross shipping-amounts must exist in database if this is the case!
if(calculateTax == 'never') {
	netShippingAmount = ctx.double_(netShippingAmountAttrCode)
	grossShippingAmount = ctx.double_(grossShippingAmountAttrCode)
} else {
	if(shippingAmountIncludesTax) {
		// Use stored net-shipping-amount if it exists and configuration tells us to do so
		if(ctx.double_(netShippingAmountAttrCode) != null && calculateTax == 'if-not-exists') {
			netShippingAmount = ctx.double_(netShippingAmountAttrCode)
		} else {
			// Calculate net-shipping-amount from gross-value
			netShippingAmount = Math.round((ctx.double_(shippingAmountAttrCode) * 100) / ((shippingTaxRate / 100) + 1)) / 100
		}

		// Just use the shipping-amount that we already have as it already includes tax
		grossShippingAmount = ctx.double_(shippingAmountAttrCode)
	} else {
		// Just use the shipping-amount that we already have as it already includes tax
		netShippingAmount = ctx.double_(shippingAmountAttrCode)
	
		// Use stored gross-shipping-amount if it exists and configuration tells us to do so
		if(ctx.double_(grossShippingAmountAttrCode) != null && calculateTax == 'if-not-exists') {
			grossShippingAmount = ctx.double_(grossShippingAmountAttrCode)
		} else {
			// Calculate gross-price from net-price
			grossShippingAmount = Math.round((ctx.double_(shippingAmountAttrCode) * 100) * ((shippingTaxRate / 100) + 1)) / 100
		}
	}
}

ctx.results["net_shipping_amount"] = netShippingAmount
ctx.results["gross_shipping_amount"] = grossShippingAmount
ctx.results["shipping_tax_amount"] = ctx.results["gross_shipping_amount"] - ctx.results["net_shipping_amount"]

