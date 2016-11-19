<#assign sale=0 />
<#assign cartItems = previewCart.activeCartItems>
<#assign cartTotals = previewCart.totals>

${previewCart.size?string}

<div class="row" style="display: inline">
    <div class="col-xs-12">

        <div class="row cart-header">
            <div class="cart-head-name col-xs-12 col-sm-6 col-lg-7">
                <h3><@message text="Item" lang="en" text2="Artikel" lang2="de" /></h3>
            </div>

            <div class="cart-head-price hidden-xs col-sm-2 col-lg-2">
                <h3><@message text="Price" lang="en" text2="Einzelpreis" lang2="de" /></h3>
            </div>

            <div class="cart-head-quantity hidden-xs col-sm-2 col-lg-1">
                <h3><@message text="Quantity" lang="en" text2="Menge" lang2="de" /></h3>
            </div>

            <div class="cart-head-total hidden-xs col-sm-2 col-lg-2">
                <h3><@message text="Total Price" lang="en" text2="Gesamtpreis" lang2="de" /></h3>
            </div>
        </div>

    <#list cartItems as item>
        <#assign product = item.product>
        <#include "product_price.ftl"/>

        <div class="row cart-item ${["odd", "even"][item_index%2]}">
            <div class="cart-item-name col-xs-12 col-sm-6 col-lg-7">
                <div class="item-image">
                    <a href="${item.productURI}"><img product="${item.productId}"
                                                      src="<@catMediaURL uri="${item.product.mainImageURI!}" width=216 height=156 />"
                                                      class="product-img"/></a>
                </div>
                <div class="item-description">
                    <a class="item-dscr-name" href="${item.productURI}"><@attribute src=product code="name" /></a>
                    <div class="item-dscr-name2"><@attribute src=product code="name2" /></div>
                    <div class="item-dscr-article"><@message text="Artikelnummer:" /> <@attribute src=product code="article_number" /></div>
                    <div class="item-dscr-price"><@print src=finalPrice value="self" format="currency" /></div>
                </div>
            </div>

            <div class="cart-item-price col-xs-4 col-sm-2 col-lg-2">
                <#if (specialPrice > 0 || salePrice > 0)>
                    <label class="old-price">
                        <div class="pdp-old-price"><@print src=retailPrice value="self" format="currency" /></div>
                    </label>
                    <div class="checkout-line"></div>
                    </label>
                    <#assign sale= sale + (retailPrice - finalPrice)*item.quantity/>
                </#if>
                <label class="bold">
                    <@print src=finalPrice value="self" format="currency" />
                </label>
            </div>

            <div class="cart-item-quantity col-xs-4 col-sm-2 col-lg-1">
            ${item.quantity}
            </div>

            <div class="cart-item-total col-xs-4 col-sm-2 col-lg-2">
                <@print src=cartTotals.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['gross_subtotal'];" format="currency" />
            </div>

        </div>
    </#list>

        <div class="row cart-summary" style="display: inline">
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

            <#if (cartTotals.gross_discount_total > 0)>
                <div class="shipping row">
                    <div class="title col-xs-6">
                        <@message text="Coupon discount" lang="en" text2="Gutschein Rabatt" lang2="de" />:
                    </div>
                    <div class="value col-xs-6 text-right">
                    ${cartTotals.gross_discount_total?string.currency}
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

                    <div class="title col-xs-6">
                    <@message text="inkl. 19% MwSt." lang="en" text2="inkl. 19% MwSt." lang2="de" />:
                    </div>
                    <div class="value col-xs-6 text-right">
                    ${cartTotals.grand_total_tax_amount?string.currency}
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-12">
                        <div class="form-group">
                            <div class="checkbox">
                                <label>
                                <@f.checkbox name="form.agreeToTerms" value="true" fieldOnly=true/><span></span>
                                    <div><@message text='Ja, ich stimme den <a target="_blank" class="btn-link" href="/agb">AGB</a> und den <a target="_blank" class="btn-link" href="/datenschutz">Datenschutzbestimmungen</a> zu. Hier finden Sie Informationen zum <a target="_blank" class="btn-link" href="/agb#widerrufsbelehrung">Widerrufsrecht</a>' /></div>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="cart-actions row">
            <div class="col-xs-12 col-md-6 pull-right">
                <button type="button" class="checkout-action-btn action-btn">
                <@message text="Buy now" lang="en" text2="Jetzt kaufen" lang2="de" />
                </button>
            </div>
        </div>

    </div>
</div>


