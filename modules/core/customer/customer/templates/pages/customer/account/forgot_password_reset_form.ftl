<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@fession />

<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">
                <h1 class="h1 header">
                    <@message text='Change password' lang="en" text2="Passwort andern" lang2="de"/>
                </h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <#if !loggedInCustomer?? && accountForm.fpToken?? && accountForm.fpTokenIsValid == true>

                        <@f.form action="/customer/account/forgot-password-save" method="post"
                        ssl=true autocomplete="off"
                        class="form-horizontal customer-account-form"
                        fieldGroupClass="form-group row"
                        fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                        fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                        fieldClass="form-control"
                        fieldHintClass="help-block"
                        fieldErrorClass="help-block">

                            <input type="hidden" name="<@csrf.tokenname/>"
                                   value="<@csrf.tokenvalue uri="/customer/account/forgot-password-save"/>"/>
                            <@f.text type="hidden" name="accountForm.fpToken" />
                            <@f.text type="hidden" name="accountForm.fpTokenIsValid" />

                            <@message text="Password<sup>*</sup>" lang="en" text2="Passwort<sup>*</sup>" lang2="de" var="password1Label"/>
                            <@f.password name="accountForm.password1" id="password1" label="${password1Label}"/>

                            <@message text="Repeat Password<sup>*</sup>" lang="en" text2="Passwort-Wiederholung<sup>*</sup>" lang2="de" var="password2Label"/>
                            <@f.password name="accountForm.password2" id="password2" label="${password2Label}"/>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-6 col-sm-offset-6">
                                            <@f.button type="submit" name="ac-password-reset-btn" class="password-reset-btn">
                                                <i class="pull-right"></i>
                                                <@message text="Reset password" lang="en" text2="Passwort ändern" lang2="de"/></@f.button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </@f.form>
                    <#else>
                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text="We could not reset your password. Please try again." lang="en" text2="Das Passwort konnte nicht zurückgesetzt werden. Bitte versuchen Sie es erneut." lang2="de"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>

</div>

</@layout.onecolumn>
