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

        <@f.haserrors>
            <@f.errors/>
        </@f.haserrors>

        <div class="col-xs-12 col-sm-3">
            <div class="col-xs-12 content-panel">
                <#include "preview-address.ftl" />
            </div>

            <div class="col-xs-12 content-panel">
                <#include "preview-payment.ftl" />
            </div>

        </div>

        <div class="col-xs-12 col-sm-9">
            <#include "div-normal-preview.ftl" />
        </div>

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