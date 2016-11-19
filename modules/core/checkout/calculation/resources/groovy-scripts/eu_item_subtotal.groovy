// Configuration
// --------------------------------------------------------------------------

// What is the name of the base-calculation-price attribute?
def baseCalculationPriceAttrCode = ctx.cpStr_("pricing/base-calculation-price-attribute")
// Does the base-calculation-price include tax?
def baseCalculationPriceIncludesTax = ctx.cpBool_("pricing/base-calculation-price-includes-tax")
// Name of the attribute that contains the net-price.
def netPriceAttrCode = ctx.cpStr_("pricing/net-price-attribute")
// Name of the attribute that contains the gross-price.
def grossPriceAttrCode = ctx.cpStr_("pricing/gross-price-attribute")
// Allow calculation of tax? Possible values: always, never, if-not-exists.
def calculateTax = ctx.cpStr_("pricing/calculate-tax")


// Calculation
// --------------------------------------------------------------------------

def i = 0

// Iterate through all items and calculate the row subtotal.
ctx.items.each() { item ->
	def netPriceSubtotal = 0
	def grossPriceSubtotal = 0

	// Do not calculate tax if set in configuration to never do so. Both net and gross prices must exist in database if this is the case!
	if(calculateTax == 'never') {
		netPriceSubtotal = Math.round(((item[netPriceAttrCode] * 100) * item['qty'])) / 100
		grossPriceSubtotal = Math.round(((item[grossPriceAttrCode] * 100) * item['qty'])) / 100
	} else {
		def netUnitPrice = 0
		def grossUnitPrice = 0

		// Does the base-calculation-price contain tax?
		if(baseCalculationPriceIncludesTax) {
			// Use stored net-price if it exists and configuration tells us to do so
			if(item[netPriceAttrCode] != null && item[netPriceAttrCode] > 0 && calculateTax == 'if-not-exists') {
				netUnitPrice = item[netPriceAttrCode]
			} else {
				// Calculate net-price from gross-price
				netUnitPrice = Math.round((item[baseCalculationPriceAttrCode] * 100) / ((item['tax_rate'] / 100) + 1)) / 100
			}

			// Just use the gross-price that we already have because the base-calculation-price already includes tax
			grossUnitPrice = item[baseCalculationPriceAttrCode]
		} else {
			// Just use the net-price that we already have because the base-calculation-price is aready without tax
			netUnitPrice = item[baseCalculationPriceAttrCode]

			// Use stored gross-price if it exists and configuration tells us to do so
			if(item[grossPriceAttrCode] != null && item[grossPriceAttrCode] > 0 && calculateTax == 'if-not-exists') {
				grossUnitPrice = item[grossPriceAttrCode]
			} else {
				// Calculate gross-price from net-price
				grossUnitPrice = Math.round((item[baseCalculationPriceAttrCode] * 100) * ((item['tax_rate'] / 100) + 1)) / 100
			}
		}

		// Now that we have the net and gross unit prices, we can calculate the subtotals.
		netPriceSubtotal = Math.round(((netUnitPrice * 100) * item['qty'])) / 100
		grossPriceSubtotal = Math.round(((grossUnitPrice * 100) * item['qty'])) / 100
	}

	// Add results-map to items list.
	ctx.itemResults[i++] = [article_id : item['article_id'] , net_subtotal : netPriceSubtotal, gross_subtotal : grossPriceSubtotal, subtotal_tax_ammount : grossPriceSubtotal-netPriceSubtotal, tax_rate : item['tax_rate']]
}

