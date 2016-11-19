<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>


<table class="table cart-table final-cart">
    <thead>
    <tr>
        <th class="th-nazev tablet-image"><@message text="Artikel" /></th>
        <th class="cart-article article-desc"></th>
        <th class="th-cena-1 money-one"><@message text="Einzelpreis" /></th>
        <th class="center cart-amount"><@message text="Menge" /></th>
        <th class="th-cena-2 money-many"><@message text="Gesamt" /></th>
    </tr>
    </thead>
    <tbody>
    <#assign sale=0 />
    <#list previewCart.activeCartItems as item>
        <#assign product=item.product />
        <#include "../order_summary/init_prices.ftl"/>
    <tr>

        <td class="tablet-image">
            <img product="${item.productId}" src="<@catMediaURL uri="${product.mainImageURI!}" width=100 height=147 />"
                 class="product-img"/>
        </td>
        <td class="cart-article article-desc">
            <label style="margin-bottom: 0;"><strong><@attribute src=product code="name" /></strong></label>
            <label><@attribute src=product code="name2" /></label>
            <label class="gray"><@message text="Artikelnummer:" /> <@attribute src=product code="article_number" /></label>
            <#if (!item.pickup && item.active)>
                <div style="font-size: 14px; font-weight: normal; color: #33890c;">
                    <@import uri="/ams/mve/availability/${product.id?string}">
                        quantity=${item.quantity?string}
                        zip=${ deliveryZip?string}
                        template=cart/availability
                    </@import>
                </div>
            </#if>
            <div class="delivery-informer-container">
                <div class="delivery-informer">
                    <#if item.pickup>
                        <span class="sprite carIcon"></span>&nbsp;${item.pickupDeliveryTime}
                    <#else>
                        <#include "delivery.ftl"/>
                    </#if>
                </div>
            </div>
        </td>
        <td class="article-desc money money-one">
            <#if (specialPrice > 0 || salePrice > 0)>
                <label class="old-price">
                    <div class="pdp-old-price"><@print src=retailPrice value="self" format="currency" /></label>
                <div class="checkout-line"></div>
                </label>
                <#assign sale= sale + (retailPrice - finalPrice)*item.quantity/>
            </#if>
            <label class="bold">
                <@print src=finalPrice value="self" format="currency" />
            </label>
        </td>
        <td class="center cart-amount"><label>${item.quantity}</label></td>
        <td class="money money-many"><label
                class="bold"><@print src= cartTotals.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['gross_subtotal'];" format="currency" /></label>
        </td>
    </tr>
    </#list>


    </tbody>
    <tfoot>
    <tr>
        <td colspan="2" rowspan="2" style="width: 50%;"><a href="/cart/view">
            <h4><@message text="Artikel bearbeiten" /></h4></a></td>
        <td colspan="2" class="article-desc">
            <label><@message text='Zwischensumme' /></label>
            <label style="display: inline-block;"><@message text='Versandkosten' /></label>
            <a id="popoverOption" href="#" rel="popover" data-placement="bottom"><i class="sprite icon-info"
                                                                                    style="margin-bottom: -2px;margin-left: 3px;"></i></a>
        <#if (cartTotals.gross_discount_total > 0)>
            <label><@message text='Gutschein Rabatt' /></label>
        </#if>
        </td>
        <td class="article-desc money">
            <label>${cartTotals.gross_subtotal?string.currency}</label>
            <label>${cartTotals.gross_shipping_amount?string.currency}</label>
        <#if ( cartTotals.gross_discount_total > 0)>
            <label>-${cartTotals.gross_discount_total?string.currency}</label>
        </#if>
        </td>
    </tr>
    <tr class="bottom-border">
        <td colspan="2" class="article-desc">
            <label class="bold"><@message text='Gesamtsumme' /></label>
            <label><@message text='inkl. 19% MwSt.' /></label>
        </td>
        <td class="article-desc money">
            <label class="bold">${cartTotals.gross_grand_total?string.currency}</label>
            <label>${cartTotals.grand_total_tax_amount?string.currency}</label>
        </td>
    </tr>
    <tr class="hidden-sm">
        <td colspan="2" class="noborder">&nbsp;</td>
        <td colspan="3" class="access-data noborder">
            <div class="form-group">
                <div class="checkbox">
                    <label>
                    <@f.checkbox name="form.agreeToTerms" id="agree-to-terms" value="true"/><span></span>
                        <div><@message text='Ja, ich stimme den <a target="_blank" href="/agb">AGB</a> und den <a target="_blank" href="/datenschutz">Datenschutzbestimmungen</a> zu. Hier finden Sie Informationen zum <a target="_blank" href="/agb#widerrufsbelehrung">Widerrufsrecht</a>' /></div>
                    </label>
                </div>
            </div>
        </td>
    </tr>

    <tr class="visible-sm">
        <td colspan="5" class="access-data noborder">
            <div class="form-group">
                <div class="checkbox">
                    <label>
                    <@f.checkbox name="form.agreeToTerms" id="agree-to-terms" value="true"/><span></span>
                        <div><@message text='Ja, ich stimme den <a target="_blank" href="/agb">AGB</a> und den <a target="_blank" href="/datenschutz">Datenschutzbestimmungen</a> zu. Hier finden Sie Informationen zum <a target="_blank" href="/agb#widerrufsbelehrung">Widerrufsrecht</a>' /></div>
                    </label>
                </div>
            </div>
        </td>
    </tr>


    <tr>
        <td colspan="5" class="table-checkout-footer">
            <div class="checkout-euro">
                <i class="additional-sprite-checkout icon-red-euro-guy-ok"></i>
            </div>

        <#if (sale > 0)>
            <div class="checkout-speech-bubble final_step">
                <div class="bubble-text">
                    <@message text='Super,' /> <b>${sale}</b> <@message text='€ gespart!' />
                    </span>


                </div>
            </div>
        </#if>
            <div class="finish-button">
                <button type="submit" class="yellow-btn btn-signin right"><@message text="Jetzt kaufen "/></button>
            </div>
            <div class="checkout-border-bootom"></div>
        </td>
    </tr>
    </tfoot>
</table>

