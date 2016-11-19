<#assign k=0/>
<#assign d=0/>
<#list previewCart.activeCartItems as item>
    <#assign product=item.product />
    <#include "../order_summary/init_prices.ftl"/>
<div class="row cart-item <#if k==1>grey-border</#if>">
    <#assign k=1/>
    <div class="col-xs-3 cart-item-inner">
        <img product="${item.productId}" src="<@catMediaURL uri="${product.mainImageURI!}" width=75 height=75 />"
             class="product-img"/>
    </div>
    <div class="col-xs-9 cart-item-inner">
        <div class="col-xs-12 margin-botom-20">
            <label style="margin-bottom: 0;"><strong><@attribute src=product code="name" /></strong></label><br/>
            <label><@attribute src=product code="name2" /></label>

            <div style="font-size: 14px; font-weight: normal;">
                <#if item.pickup>
                    <span class="sprite carIcon"></span>&nbsp;${item.pickupDeliveryTime}
                <#else>
                    <#include "../cart/delivery.ftl"/>
                </#if>
                <#if (!item.pickup && item.active)>
                    <span class="delivery-text">
                        <@import uri="/ams/mve/availability/${product.id?string}">
                            quantity=${item.quantity?string}
                                zip=${deliveryZip?string}
                                template=cart/availability
                        </@import>
                            </span>
                </#if>
            </div>
        </div>
        <div class="col-xs-6">
            <label>${item.quantity}</label>
        </div>
        <div class="col-xs-6 align-right">
            <label class="bold">
                <@print src=cartTotals.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['gross_subtotal'];" format="currency" />
            </label>
        </div>
    </div>
</div>
</#list>
<div class="row cart-item rudy-top">
    <div class="col-xs-7"><label><@message text='Zwischensumme' /></label></div>
    <div class="col-xs-5 align-right"><label>${cartTotals.gross_subtotal?string.currency}</label></div>

    <div class="col-xs-7"><label><@message text='Versandkosten' /></label>
        <a id="popoverOption2" href="#" rel="popover" data-placement="bottom"><i class="sprite icon-info"
                                                                                 style="margin-bottom: -2px;margin-left: 3px;"></i></a>
    </div>
    <div class="col-xs-5 align-right"><label>${cartTotals.gross_shipping_amount?string.currency}</label></div>
<#if (cartTotals.gross_discount_total > 0)>
    <div class="col-xs-7"><label><@message text='Gutschein Rabatt' /></label></div>
    <div class="col-xs-5 align-right"><label>-${cartTotals.gross_discount_total?string.currency}</label></div>
</#if>
</div>
<div class="row cart-item rudy-top">
    <div class="col-xs-7"><label class="bold"><@message text='Gesamtsumme' /></label></div>
    <div class="col-xs-5 align-right"><label class="bold">${cartTotals.gross_grand_total?string.currency}</label></div>
    <div class="col-xs-7"><label><@message text='inkl. 19% MwSt.' /></label></div>
    <div class="col-xs-5 align-right"><label>${cartTotals.grand_total_tax_amount?string.currency}</label></div>
</div>

<div class="row cart-item rudy-top margin-bottom-50">
    <div class="form-group access-data">
        <div class="checkbox">
            <label>
            <@f.checkbox name="form.agreeToTerms" value="true"/><span></span>
                <div><@message text='Ja, ich stimme den <a target="_blank" href="/agb">AGB</a> und den <a target="_blank" href="/datenschutz">Datenschutzbestimmungen</a> zu. Hier finden Sie Informationen zum <a target="_blank" href="/agb#widerrufsbelehrung">Widerrufsrecht</a>' /></div>
            </label>
        </div>
    </div>
</div>


<div class="col-xs-12 finish-button">
    <button type="submit" class="btn btn-default cart-checkout"
            style=""><i
            class="pull-right sprite icon-right-arrow-white"></i>
        <span class="account-button-label"><@message text="Jetzt kaufen "/></span>
    </button>
</div>
<div class="finish checkout-border-bootom"></div>