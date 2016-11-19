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
                <h1 class="h1"><@message text='Account Settings' lang="en" text2="Kundenkonto Einstellungen" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6 col-lg-offset-2 col-md-offset-2">

                    <@f.form action="/customer/account/process-edit" method="post" autocomplete="off" class="form-horizontal customer-account-form"
                    fieldGroupClass="form-group row"
                    fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                    fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                    fieldClass="form-control"
                    fieldHintClass="help-block"
                    fieldErrorClass="help-block">

                        <@f.haserrors>
                            <@message text="Please fix the errors!" lang="en" text2="Bitte korrigieren Sie die Fehler!" lang2="de"/>
                            <@f.errors/>
                        </@f.haserrors>

                        <input type="hidden" name="<@csrf.tokenname/>"
                               value="<@csrf.tokenvalue uri="/customer/account/process-edit"/>"/>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-edit-title"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <h3 class="h3"><@message text='Login data' lang="en" text2="Zugangsdaten" lang2="de"/>
                                            <h3>
                                    </label>
                                </div>
                            </div>
                        </div>

                        <@message text='Email address' lang="en" text2="E-mail Adresse" lang2="de" var="emailLabel"/>
                        <@f.text name="accountForm.email" id="ac-email" label="${emailLabel}"/>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-6"></div>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <@f.button type="button" name="ac-password-change-btn" id="ac-password-change-btn">
                                            <@message text='Change password' lang="en" text2="Passwort andern" lang2="de"/>
                                        </@f.button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group hidden ac-change-password-group">
                            <div class="row">
                                <div class="col-xs-12 ">
                                    <label for="ac-input-password-1"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Password' lang="en" text2="Passwort" lang2="de"/>
                                    </label>

                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <@f.password name="accountForm.password1" class="form-control" id="ac-input-password-1" fieldOnly=true></@f.password>
                                    </div>

                                </div>
                            </div>
                        </div>

                        <div class="form-group hidden ac-change-password-group">
                            <div class="row">
                                <div class="col-xs-12 ">
                                    <label for="ac-input-Password-2"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Repeat password' lang="en" text2="Passwort-Wiederholung" lang2="de"/>
                                    </label>

                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <@f.password name="accountForm.password2" class="form-control" id="ac-input-password-2" fieldOnly=true></@f.password>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group hidden ac-change-password-group">
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
                                    <label for="ac-customer-num"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Customer #' lang="en" text2="Kundennummer #" lang2="de"/>
                                    </label>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <#assign cNumber="" />
                                        <#if accountForm.customerNumber??>
                                            <span class="control-label pull-left"
                                                  id="ac-customer-num">${accountForm.customerNumber}</span>
                                        <#else>
                                            <span>&nbsp;</span>
                                        </#if>
                                        <@f.text name="accountForm.customerNumber" type="hidden" value="${accountForm.customerNumber}" class="form-control" fieldOnly=true/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="email-notification"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"></label>

                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <div class="checkbox">
                                            <label>
                                                <@f.checkbox name="accountForm.emailNotification" value="true" id="email-notification" fieldOnly=true/>
                                            <@message text='I want to receive e-mail notifications' lang="en" text2="Ich möchte E-Mail Benachrichtigungen" lang2="de"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-contact-info"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <h3 class="h3"><@message text='Contact information' lang="en" text2="Kontaktinformationen" lang2="de"/></h3>
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-salutation"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Salutation' lang="en" text2="Anrede" lang2="de"/>
                                    </label>

                                    <div class="col-xs-6 col-sm-3 col-md-3 col-lg-3">
                                        <div id="ac-salutation">
                                            <@f.select name="accountForm.salutation" class="form-control" fieldOnly=true>
                                            <@f.option value="Mr"><@message text='Mr' lang="en" text2="Herr" lang2="de"/></@f.option>
                                            <@f.option value="Mrs"><@message text='Mrs' lang="en" text2="Frau" lang2="de"/></@f.option>
                                        </@f.select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <@message text='Title' lang="en" text2="Title" lang2="de" var="titleLabel"/>
                        <@f.text name="accountForm.title" id="ac-title" label="${titleLabel}"/>

                        <@message text='Forename' lang="en" text2="Vorname" lang2="de" var="vornameLabel"/>
                        <@f.text name="accountForm.forename" label="${vornameLabel}" id="ac-forename"/>

                        <@message text='Surname' lang="en" text2="Nachname" lang2="de" var="surnameLabel"/>
                        <@f.text name="accountForm.surname" id="surname" label="${surnameLabel}"/>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-phone-code"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Phone nummber' lang="en" text2="Vorwahl, Telefonnr" lang2="de"/>
                                    </label>

                                    <div class="col-xs-4 col-sm-2 col-md-2 col-lg-2">
                                        <@f.text name="accountForm.phoneCode" class="form-control" id="ac-phone-code" maxlength="5" fieldOnly=true/>
                                    </div>

                                    <div class="col-xs-8 col-sm-4 col-md-4 col-lg-4">
                                        <@f.text name="accountForm.phone" class="form-control" id="ac-phone-number" fieldOnly=true/>
                                    </div>

                                    <div class="col-xs-12 col-sm-offset-6">
                                        <@message text='Phone code' lang="en" text2="Vorwahl" lang2="de" var="phoneCodeLabel"/>
                                        <@message text='Nummber' lang="en" text2="Telefonnr" lang2="de" var="phoneLabel"/>
                                        <@f.error name="accountForm.phoneCode" label="${phoneCodeLabel}" />
                                        <@f.error name="accountForm.phone" label="${phoneLabel}"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-invoice-addr"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <h3 class="h3"><@message text='Billing address' lang="en" text2="Rechnungsadresse" lang2="de"/></h3>
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-invoice-addr-firm"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text='Company' lang="en" text2="Firma" lang2="de"/>
                                    </label>

                                    <div class="col-xs-4 col-sm-2 col-md-2 col-lg-2">
                                        <@f.text name="accountForm.invoiceAddrFirm" class="form-control" id="ac-invoice-addr-firm" fieldOnly=true/>
                                    </div>
                                    <div class="col-xs-8 col-sm-4 col-md-4 col-lg-4">
                                        <@f.text name="accountForm.invoiceAddrUst" class="form-control" id="ac-invoice-addr-ust" fieldOnly=true />
                                    <#--placeholder="<@message text='give here your VAT ID. No. etc.' lang='en' text2='geben Sie hier bitte Ihre UST-Ident. Nr. ein' lang2='de'/>"-->
                                    </div>
                                </div>
                            </div>
                        </div>


                        <@message text='Street' lang="en" text2="Straße" lang2="de" var="streetLabel"/>
                        <@f.text name="accountForm.invoiceAddrStreet" id="ac-invoice-addr-street" label="${streetLabel}"/>

                        <@message text='House number' lang="en" text2="Hausnummer" lang2="de" var="housenumLabel"/>
                        <@f.text name="accountForm.invoiceAddrHouseNum" id="ac-invoice-addr-house-num" label="${housenumLabel}"/>

                        <@message text='Zip code' lang="en" text2="Postleitzahl" lang2="de" var="zipLabel"/>
                        <@f.text name="accountForm.invoiceAddrZipCode" id="ac-invoice-addr-zipcode" label="${zipLabel}"/>

                        <@message text='City' lang="en" text2="Ort" lang2="de" var="cityLabel"/>
                        <@f.text name="accountForm.invoiceAddrCity" class="form-control" id="ac-invoice-addr-city" label="${cityLabel}"/>

                        <div class="form-group">
                            <div class="col-xs-6 col-sm-offset-6">
                                <@message text='Required fields' lang="en" text2="Pflichtfelder" lang2="de"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-6"></div>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <@f.button name="ac.save" type="submit">
                                            <@message text="Save" lang="en" text2="Speichern" lang2="de"/>
                                        </@f.button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </@f.form>

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