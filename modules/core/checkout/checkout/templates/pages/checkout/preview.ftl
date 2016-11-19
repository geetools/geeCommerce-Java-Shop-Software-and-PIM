<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>
<@layout.onecolumn>

    <@session />


<div class="row breadcrumb-panel breadcrumb-cart-panel header-row">
    <div class="col-xs-12">
        <h3 class="attention red"><@message text="Bitte überprüfen und bestätigen Sie Ihre Bestellung" /></h3>
    </div>
</div>
<div class="row checkout-preview">

    <@f.form method="post" action="${formAction}" class="checkout-form">
        <input type="hidden" name="<@csrf.tokenname/>"
               value="<@csrf.tokenvalue uri="${formAction}"/>"/>
        <input type="hidden" id="page_is_dirty" name="page_is_dirty" value="0"/>

        <div class="col-xs-12 col-sm-3">
            <div class="col-xs-12 content-panel">
                <#include "preview-address.ftl" />
            </div>

            <div class="col-xs-12 content-panel">
                <#include "preview-payment.ftl" />
            </div>

            <#--<#if pickupStore?? >-->
                <#--<div class="col-xs-12">-->
                    <#--<div class="row">-->
                        <#--<div class="col-sm-12 col-md-6"><h3><@message text="Abholfiliale" /></h3></div>-->
                        <#--<div class="col-sm-12 col-md-6"><a href="/cart/view"><h4><@message text="Bearbeiten" /></h4></a>-->
                        <#--</div>-->
                    <#--</div>-->
                    <#--<div class="row">-->
                        <#--<div class="col-xs-12">-->
                            <#--<#include "preview-pickup-store.ftl" />-->
                        <#--</div>-->
                    <#--</div>-->
                <#--</div>-->
            <#--</#if>-->

        </div>

        <div class="col-xs-12 col-sm-9">
            <#include "div-normal-preview.ftl" />
        </div>

        <#--<div class="row">-->
            <#--<div class="col-xs-12">-->
                <#--<div class="form-group">-->
                    <#--<div class="checkbox">-->
                        <#--<label>-->
                            <#--<@f.checkbox name="form.agreeToTerms" value="true" fieldOnly=true/><span></span>-->
                            <#--<div><@message text='Ja, ich stimme den <a target="_blank" class="btn-link" href="/agb">AGB</a> und den <a target="_blank" class="btn-link" href="/datenschutz">Datenschutzbestimmungen</a> zu. Hier finden Sie Informationen zum <a target="_blank" class="btn-link" href="/agb#widerrufsbelehrung">Widerrufsrecht</a>' /></div>-->
                        <#--</label>-->
                    <#--</div>-->
                <#--</div>-->
            <#--</div>-->
        <#--</div>-->

        <#--<div class="cart-actions row">-->
            <#--<div class="col-xs-12 col-md-6 pull-right">-->
                <#--<button type="submit" class="action-btn" disabled>-->
                    <#--<@message text="Buy now" lang="en" text2="Jetzt kaufen" lang2="de" />-->
                <#--</button>-->
            <#--</div>-->
        <#--</div>-->


        <div id="popoverContent" style="display: none;">
            <table>
                <#list cart.deliveryEstimationOptions as option>
                    <#if option.shippingPackage.type?string! != "PICKUP" >
                        <tr>
                            <td style="width: 250px;">
                                <label>
                                    <#if option.shippingPackage.type?string! == 'PACKAGE'>
                                        <@message text="Paketversand" />
                                    </#if>
                                <#if option.shippingPackage.type?string! == 'BULKY'>
                                    <@message text="Sperrgut" />
                                </#if>
                                <#if option.shippingPackage.type?string! == 'DELIVERY'>
                                    <@message text="Spedition" />
                                </#if>
                                </label>
                            </td>
                            <td style="width: 100px;">
                                <#if option.rate?string != "0" >
                        ${option.rate?string.currency}
                    </#if>
                            </td>
                        </tr>
                    </#if>
                </#list>

                <tr class="shipping-tooltip-border">
                    <td style="border-top: 1px solid #ddd">
                        <@message text='Versandkosten' />
                    </td>
                    <td style="border-top: 1px solid #ddd">
                    ${cartTotals.gross_shipping_amount?string.currency}
                    </td>
                </tr>
            </table>
        </div>

        <input type="hidden" name="redirect"/>
    </@f.form>

    <input type="hidden" name="agreeToTermsWarn" value="<@message text='Bitte stimmen Sie unseren AGB und Datenschutzbestimmungen zu!'/>" />

</div>

</@layout.onecolumn>