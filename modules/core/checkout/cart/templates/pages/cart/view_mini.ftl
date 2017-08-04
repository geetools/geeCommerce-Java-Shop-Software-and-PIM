<div class="mini-cart-content">

	<#if cart?? && !(cart.cartItems!)?has_content>
        <@message text="Cart is empty" lang="en" text2="Leer Warenkorb" lang2="de" />
	</#if>

	<#if cart?? && cart.cartItems?has_content>
	    <#assign cart = cart>
	    <#assign cartTotals = cart.totals>
	    
        <table class="mc-items">
            <#list cart.cartItems as item>
                <#assign product = item.product>
                <#include "product_price.ftl"/>
            
	            <tr class="mc-item">
	                <td class="mc-image"><a href="<@print src=product value='product.URI'/>"><img product="${item.productId}" src="<@catMediaURL src=product width=133 height=147 parent=true />" width="60" class="product-img" /></a></td>
	                <td class="mc-name"><b><@attribute src=product code="name" /></b><br><@attribute src=product code="name2" /></td>
	                <td class="mc-quantity">${item.quantity}x</td>
	                <td class="mc-subtotal"><@print src=finalPrice value="self" format="currency" /></td>
	            </tr>
            </#list>

            <tr class="mc-total">
                <td></td>
                <td colspan="2" class="mc-total-label">
                <@message text="Total" lang="en" text2="Gesamtsumme" lang2="de" />:
                </td>
                <td class="mc-total-val">
                    ${cartTotals.gross_grand_total?string.currency}
                </td>
            </tr>
        </table>
	</#if>
</div>
