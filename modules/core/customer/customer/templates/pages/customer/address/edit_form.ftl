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
                <h1 class="h1"><@message text='Change Your Address' lang="en" text2="Ihre Adresse ändern" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <@f.form action="/customer/address/edit-confirm/${id}" method="post" ssl=true class="form-horizontal"
                    fieldGroupClass="form-group row"
                    fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                    fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                    fieldClass="form-control"
                    fieldHintClass="help-block"
                    fieldErrorClass="help-block">

                        <input type="hidden" name="<@csrf.tokenname/>"
                               value="<@csrf.tokenvalue uri="/customer/address/edit-confirm/${id}"/>"/>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <label for="ac-addr-salutation"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                        <@message text="Salutation" lang="en" text2="Anrede" lang2="de"/>
                                    </label>

                                    <div class="col-xs-6 col-sm-3 col-md-3 col-lg-3">
                                        <div id="ac-addr-salutation">
                                            <@f.select name="addressForm.salutation" class="form-control" fieldOnly=true>
                                            <@f.option value="Mr"><@message text='Mr' lang="en" text2="Herr" lang2="de"/></@f.option>
                                            <@f.option value="Mrs"><@message text='Mrs' lang="en" text2="Frau" lang2="de"/></@f.option>
                                        </@f.select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <@message text="Forename" lang="en" text2="Vorname" lang2="de" var="forenameLabel"/>
                        <@f.text name="addressForm.forename" label="${forenameLabel}" id="ac-addr-forename"/>

                        <@message text="Surname" lang="en" text2="Nachname" lang2="de" var="surnameLabel"/>
                        <@f.text name="addressForm.surname" id="ac-addr-surname" label="${surnameLabel}"/>

                        <@message text="Company" lang="en" text2="Firma" lang2="de" var="companyLabel"/>
                        <@f.text name="addressForm.company" id="ac-addr-company" label="${companyLabel}"/>

                        <@message text="Phone nummber" lang="en" text2="Telefonnummer" lang2="de" var="phoneLabel"/>
                        <@f.text name="addressForm.phone" id="ac-addr-phone-number" label="${phoneLabel}"/>

                        <@message text="Fax" lang="en" text2="Fax" lang2="de" var="faxLabel"/>
                        <@f.text name="addressForm.fax" id="ac-addr-fax" label="${faxLabel}"/>

                        <@message text="Street" lang="en" text2="Straße" lang2="de" var="streetLabel"/>
                        <@f.text name="addressForm.street" id="ac-addr-street" label="${streetLabel}"/>

                        <@message text="House number" lang="en" text2="Hausnummer" lang2="de" var="housenumLabel"/>
                        <@f.text name="addressForm.houseNumber" id="ac-addr-housenum" label="${housenumLabel}"/>

                        <@message text="City" lang="en" text2="Ort" lang2="de" var="cityLabel"/>
                        <@f.text name="addressForm.city" id="ac-addr-city" label="${cityLabel}"/>

                        <@message text="State/Province (optional)" lang="en" text2="Bundesland/Kanton (optional)" lang2="de" var="stateLabel"/>
                        <@f.text name="addressForm.state" id="ac-addr-state" label="${stateLabel}"/>

                        <@message text="Zip" lang="en" text2="Postleitzahl" lang2="de" var="zipLabel"/>
                        <@f.text name="addressForm.zip" id="ac-addr-zip" label="${zipLabel}"/>


                        <@message text='Country' lang="en" text2="Land" lang2="de" var="countryLabel"/>
                        <@f.select name="addressForm.country" class="form-control" id="ac-addr-country" label="${countryLabel}">
                            <#list countries?keys as key>
                                <@f.option value="${key}">${countries[key]}</@f.option>
                            </#list>
                        </@f.select>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12 ">
                                    <label for="ac-addr-def-invoice"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"></label>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <div class="checkbox">
                                            <label>
                                                <@f.checkbox name="addressForm.defaultInvoiceAddress" value="true" group="def-address-type" id="ac-addr-def-invoice" fieldOnly=true/>
                                                <@message text="Set address as default invoice address" lang="en" text2="Set-Adresse als Standard-Rechnungsadresse" lang2="de"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12 ">
                                    <label for="ac-addr-def-delivery"
                                           class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"></label>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <div class="checkbox">
                                            <label>
                                                <@f.checkbox name="addressForm.defaultDeliveryAddress" value="true" group="def-address-type" id="ac-addr-def-invoice" fieldOnly=true/>
                                                <@message text='Set address as default delivery address' lang="en" text2="Set-Adresse als Standard-Lieferadresse" lang2="de"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-6"></div>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <button type="submit">
                                            <@message text="Save and continue" lang="en" text2="Speichern und weiter" lang2="de"/>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </@f.form>

                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="col-xs-12 col-sm-offset-6">
                                    <a class="btn-link" href="/customer/address/overview"><span><@message text='Back to Manage Addresses' lang="en" text2="Zurück zur Adressen verwalten" lang2="de"/></span></a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>