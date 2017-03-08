<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>

<#-- 
We make sure that a session exists, so that the session-csrf-token can be generated,
which ensures that the first post of the form has a positive csrf-validation.
-->
<@session />

<@layout.onecolumn>

<#--    <#include "coupon.ftl" />-->



    <#if cart?? && cart.cartItems?has_content>

        <@coupons/>

        <#assign cart = cart>
        <#assign cartTotals = cart.totals>

        <div class="row cart-header">
            <div class="name col-xs-12 col-sm-6 col-lg-8">
                <@message text="Item" lang="en" text2="Artikel" lang2="de" />
            </div>

            <div class="hidden-xs col-sm-2 col-lg-1">
                <@message text="Quantity" lang="en" text2="Menge" lang2="de" />
            </div>

            <div class="hidden-xs col-sm-2 col-lg-2">
                <@message text="Total Price" lang="en" text2="Gesamtpreis" lang2="de" />
            </div>

            <div class="hidden-xs col-sm-2 col-lg-1">
                <@message text="Remove" lang="en" text2="Löschen" lang2="de" />
            </div>
        </div>

        <div class="cart-items">
            <#list cart.cartItems as item>
                <#assign product = item.product>
                <#include "product_price.ftl"/>

                <div class="row cart-item ${["odd", "even"][item_index%2]}">
                    <div class="cart-item-name col-xs-11 col-sm-6 col-lg-8">
                        <div class="item-image">
                            <a href="${item.productURI}"><img product="${item.productId}" src="<@catMediaURL uri="${item.product.mainImageURI!}" width=216 height=156 />" class="product-img" /></a>
                        </div>
                        <div class="item-description">
                            <a class="item-dscr-name" href="${item.productURI}"><@attribute src=product code="name" /></a>
                            <div class="item-dscr-name2"><@attribute src=product code="name2" /></div>
                            <div class="item-dscr-article"><@message text="Artikelnummer:" /> <@attribute src=product code="article_number" /></div>
                            <div class="item-dscr-price"><@print src=finalPrice value="self" format="currency" /></div>
                        </div>
                    </div>

                    <div class="cart-item-quantity col-xs-6 col-sm-2 col-lg-1">
                        <span class="hidden-sm hidden-md hidden-lg"><@message text="Quantity" lang="en" text2="Menge" lang2="de" /></span>


                        <#if item.bundleId??>
                            <span> ${item.quantity} </span>
                        <#else>
                            <#if true>
                                <input name="quantity" productId="${item.productId}" type="number" class="recalc-btn"
                                       value="${item.quantity}" size="2">
                            <#else>
                                <select name="quantity" data-type="number">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                </select>
                            </#if>
                        </#if>
                    </div>


                    <div class="cart-item-total col-xs-4 col-sm-2 col-lg-2">
                        <@print src=cartTotals.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['gross_subtotal'];" format="currency" />
                    </div>


                    <div class="cart-item-remove hidden-xs col-sm-2 col-lg-1">
                        <#if item.bundleId??>
                            <#if bundleId?? && item.bundleId?string == bundleId>
                            <#else>
                                <a href="/cart/remove-bundle?bundleId=${item.bundleId}"><i class="glyphicon glyphicon-remove" aria-hidden="true"></i></a>
                                <#assign bundleId = item.bundleId?string>
                            </#if>
                        <#else>
                            <a href="/cart/remove?productId=${item.productId}"><i class="glyphicon glyphicon-remove" aria-hidden="true"></i></a>
                        </#if>
                    </div>

                    <div class="cart-item-remove col-xs-1 visible-xs">
                        <#if item.bundleId??>
                            <#if bundleId?? && item.bundleId?string == bundleId>
                            <#else>
                                <a href="/cart/remove-bundle?bundleId=${item.bundleId}"><i class="glyphicon glyphicon-remove" aria-hidden="false"></i></a>
                                <#assign bundleId = item.bundleId?string>
                            </#if>
                        <#else>
                            <a href="/cart/remove?productId=${item.productId}"><i class="glyphicon glyphicon-remove" aria-hidden="false"></i></a>
                        </#if>

                    </div>

                </div>
            </#list>
        </div>

        <div class="row cart-summary">
            <div class="col-xs-12 col-md-6 pull-right">
                <div class="subtotal row">
                    <div class="title col-xs-6">
                        <@message text="Total" lang="en" text2="Zwischensumme" lang2="de" />:
                    </div>
                    <div class="value col-xs-6 text-right">
                        ${cartTotals.gross_subtotal?string.currency}
                    </div>
                </div>

                <div class="shipping row">
                    <div class="title col-xs-6">
                        <@message text="Shipping Cost" lang="en" text2="Versandkosten" lang2="de" />:
                    </div>
                    <div class="value col-xs-6 text-right">
                        ${cartTotals.gross_shipping_amount?string.currency}
                    </div>
                </div>
                <#if (cartTotals["gross_discount_total"] > 0)>
                    <div class="discount row">
                        <div class="title col-xs-6">
                            <@message text="Discount" lang="en" text2="Discount" lang2="de" />:
                        </div>
                        <div class="value col-xs-6 text-right">
                            -${cartTotals.gross_discount_total?string.currency}
                        </div>
                    </div>
                </#if>
                <div class="total row">
                    <div class="title col-xs-6">
                        <@message text="Total" lang="en" text2="Gesamtsumme" lang2="de" />:
                    </div>
                    <div class="value col-xs-6 text-right">
                        ${cartTotals.gross_grand_total?string.currency}
                    </div>
                </div>
            </div>
        </div>
    </#if>

<div class="cart-actions row">
    <div class="col-xs-12 col-md-6 pull-right">

        <#if cart?? && cart.last??>
            <div class="col-sm-6 col-xs-12 cart-action-back">
                <button type="submit" onclick="location.href='${cart.last.productURI}';" class="action-btn">
                    <@message text="Back to shopping" lang="en" text2="weiter shoppen" lang2="de" />
                </button>
            </div>
        </#if>

        <#if (cart?? && cart.cartItems?? && cart.cartItems?size > 0)>

            <div class="col-sm-6 col-xs-12 cart-action-checkout">
                <button type="submit" onclick="location.href='${checkoutAction}';" class="action-btn">
                    <@message text="Checkout" lang="en" text2="zur Kasse" lang2="de" />
                </button>
            </div>
        </#if>
    </div>
</div>




<#--	<#assign cartTotals = cartTotals>

	<div id="cart-breadcrumbs" class="noprint">
		<a title="<@message text="Homepage" lang="en" text2="Startseite" lang2="de" editable=false/>" href="/"><@message text="Homepage" lang="en" text2="Startseite" lang2="de" /></a>
		<@message text="Shopping Cart" lang="en" text2="Warenkorb" lang2="de" />
	</div>






<#if cart?? && cart.cartItems?has_content>
	<#assign cartTotals = cartTotals>

		<h1><@message text="Shopping Cart" lang="en" text2="Warenkorb" lang2="de" /></h1>

        <form action="${checkoutAction}">
            <button type="submit" class="btn btn-default cart-checkout" >
                <i class="pull-right sprite icon-right-arrow-white"></i>
                <span class="account-button-label"><@message text='zur Kasse' /></span>
            </button>
        </form>

		<table class="cart-table">
			<tr>
				<th></th>
				<th></th>
				<th><@message text="Quantity" lang="en" text2="Menge" lang2="de" /></th>
				<th><@message text="Total Price" lang="en" text2="Gesamtpreis" lang2="de" /></th>
			</tr>
			<#list cart.cartItems as item>
				<tr>
					<td><a href="${item.productURI}"><img product="${item.productId}" src="<@catMediaURL uri="${item.product.mainImageURI!}" width=108 height=78 />" class="product-img" /></a></td>
					<td><a href="${item.productURI}">${item.productName}</a></td>
					<td>${item.quantity}</td>
					<td><@print src=cartTotals.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['gross_subtotal'];" format="currency" /></td>
				</tr>
			</#list>

			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
				<td><@message text="Total" lang="en" text2="Zwischensumme" lang2="de" />:</td>
				<td>${cartTotals.gross_subtotal?string.currency}</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td><@message text="Shipping Cost" lang="en" text2="Versandkosten" lang2="de" />:</td>
				<td>${cartTotals.gross_shipping_amount?string.currency}</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td><@message text="Total" lang="en" text2="Gesamtsumme" lang2="de" />:</td>
				<td>${cartTotals.gross_grand_total?string.currency}</td>
			</tr>
			
		</table>
<#else>
		<@message text="Your shopping cart is empty." lang="en" text2="Ihr Warenkorb ist leer." lang2="de" />
</#if>

-->
</@layout.onecolumn>

