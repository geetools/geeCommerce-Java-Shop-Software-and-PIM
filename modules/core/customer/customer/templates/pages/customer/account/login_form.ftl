<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@session />

<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">
                <h1 class="h1"><@message text='Log in' lang="en" text2="Anmeldung" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
                    <#if customerLoggedIn?? && customerLoggedIn>
                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                    <span>${loggedInCustomer.forename} ${loggedInCustomer.surname}
                                        <@message text=', you are already logged in.' lang="en" text2=", you are already logged in." lang2="de"/></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-6"></div>
                                    <div class="col-xs-6">
                                        <button class="logout-btn"><@message text='Logout' lang="en" text2="Ausloggen" lang2="de"/></button>
                                    </div>
                                </div>
                            </div>
                        </div>

                    <#else>


                        <@f.form action="/customer/account/process-login" method="post" ssl=true  class="form-horizontal customer-account-form"
                        fieldGroupClass="form-group row"
                        fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                        fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                        fieldClass="form-control"
                        fieldHintClass="help-block"
                        fieldErrorClass="help-block">

                            <input type="hidden" name="<@csrf.tokenname/>"
                                   value="<@csrf.tokenvalue uri="/customer/account/process-login"/>"/>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-offset-6">
                                            <span><@message text='Your login data' lang="en" text2="Ihre Anmeldendaten" lang2="de"/></span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <@message text='Your email address' lang="en" text2="Ihre E-Mail-Adresse" lang2="de" var="usernameLabel"/>
                            <@f.text name="accountForm.username" id="ac-username" label="${usernameLabel}"></@f.text>

                            <@message text='Your password' lang="en" text2="Ihr Passwort" lang2="de" var="passwordLabel"/>
                            <@f.password name="accountForm.password" id="ac-password" label="${passwordLabel}"></@f.password>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12 ">
                                        <label for="ac-show-password"
                                               class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"></label>
                                        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                            <div class="checkbox">
                                                <label>
                                                    <input type="checkbox" name="showPassword" id="ac-show-password"/>
                                                    <@message text='Show password' lang="en" text2="Passwort anzeigen" lang2="de"/>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-offset-6">
                                            <a href="/customer/account/forgot-password"><span><@message text='Forgot your password?' lang="en" text2="Passwort vergessen?" lang2="de"/></span></a>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-6 col-sm-offset-6">
                                            <@f.button type="submit" name="ac.login" class="login-btn"><i
                                                    class="pull-right"></i>
                                                <@message text='Login' lang="en" text2="Anmelden" lang2="de"/>
                                            </@f.button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-12 col-sm-6 col-sm-offset-6">
                                            <@f.button type="button" name="ac.register" class="registr-btn"><i
                                                    class="pull-right"></i>
                                                <@message text='New customer? Register now' lang="en" text2="Neukunde? Jetz registrieren" lang2="de"/>
                                            </@f.button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </@f.form>
                    </#if>
                </div>
            </div>
        </div>
    </div>


</div>


</@layout.onecolumn>