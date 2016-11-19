<#import "${t_layout}/1column.ftl" as layout>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>
<@layout.onecolumn>

    <@f.errors />

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">
                <h1 class="h1 header"><@message text='Reset your Password' lang="en" text2="Setzen Sie Ihr Passwort" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <#if (forgotPasswordConfirm??)>
                        <div class="customer-password-confirm-message"><@message text="Please check your mailbox. Instructions on how to reset your password have been sent to your email address." lang="en" text2="Bitte prüfen Sie Ihr Postfach. Sie bekommen von uns in Kürze eine E-Mail mit der Sie Ihr Passwort zurücksetzen können." lang2="de" />
                            <br/>
                        </div>
                        <br/>
                    <#else>
                        <@f.form action="/customer/account/forgot-password-confirm" method="post" class="form-horizontal customer-account-form"
                            fieldGroupClass="form-group row"
                            fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                            fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                            fieldClass="form-control"
                            fieldHintClass="help-block"
                            fieldErrorClass="help-block">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-offset-6">
                                            <span><@message text='If you have forgotten your password you may reset it here. Please enter your username in the field below so that we can send you a password-reset link to the email address you specified.' lang="en" text2="If you have forgotten your password you may reset it here. Please enter your username in the field below so that we can send you a password-reset link to the email address you specified." lang2="de"/></span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <#if !useEmail>
                                <@message text='Username' lang="en" text2="Benutzername" lang2="de" var="fieldLabel" />
                            <#else>
                                <@message text='Your email address' lang="en" text2="Ihre E-Mail-Adresse" lang2="de" var="fieldLabel" />
                            </#if>

                            <@f.text name="username" id="username" label="${fieldLabel}"/>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-6 col-sm-offset-6">
                                            <@f.button type="submit" name="ac-forgot-password-submit-btn" class="login-btn">
                                                <@message text='Continue' lang="en" text2="Fortsetzen" lang2="de"/>
                                            </@f.button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </@f.form>
                    </#if>

                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12 col-sm-offset-6">
                                <a class="btn-link"
                                   href="/customer/account/overview"><span><@message text='Back to Account Overview' lang="en" text2="Zurück zur main Konto" lang2="de"/></span></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>