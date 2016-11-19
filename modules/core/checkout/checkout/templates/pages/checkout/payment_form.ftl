<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>
<@layout.onecolumn>

    <@session />

    <@f.form action="${formAction}" method="post" ssl=true class="checkout-form" novalidate="novalidate">
    <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="${formAction}"/>"/>


        <#if errorMessage??>
            <div class="checkout-error-msg">
            ${errorMessage}
            </div>
        </#if>

            <div class="row header-row">
                <div class="col-md-3 col-xs-6">
                    <h3> <@message text="Zahlarten"/></h3>
                </div>
                <div class="col-md-9 col-xs-6">
                    <button type="button" class="btn-next btn btn-default pull-right">
                        <@message text='Weiter' />
                    </button>
                </div>
            </div>

            <#list paymentMethods as paymentMethod>
                <div class="row payment-row">
                    <div class="col-sm-3 col-xs-12">
                        <div class="radio">
                            <label>
                                <input type="radio" value="${paymentMethod.code}" name="form.paymentMethodCode" required/><span></span>
                            ${paymentMethod.label}
                            </label>
                        </div>
                    </div>
                    <div class="col-sm-9 col-xs-12">
                        <div>
                            <#include "${paymentMethod.frontendFormPath}" />
                       </div>
                    </div>
                </div>
            </#list>
            <div class="row footer-row">
                <div class="col-xs-12">
                    <button type="button" class="btn-next btn btn-default pull-right"><@message text='Weiter' />
                    </button>
                </div>
            </div>


        <input type="hidden" name="redirect">
        <input type="hidden" name="paymentSelectedWarn" value="<@message text='Please select one of the payment methods.' lang="en" text2="Bitte wählen Sie eine der Zahlungsmethoden." lang2="de"/>" />

</@f.form>
</@layout.onecolumn>