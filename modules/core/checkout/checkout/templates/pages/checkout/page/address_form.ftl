<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@layout.onecolumn>
    <#assign customerAddress = actionBean.customerAddress />
    <@session />


<div class="row checkout-address">
    <div class="col-xs-12">
        <div class="row">
            <div class="col-sm-12 col-md-6 col-md-offset-3">
                <h3 class="h3">
                    <@message text="Ihre Rechnungsadresse"/>
                </h3>

                <@f.form action="${formAction}" method="post" ssl=true class="form-horizontal  checkout-form"
                fieldGroupClass="form-group row"
                fieldLabelClass="col-xs-3 control-label without-right-padding"
                fieldWrapperClass="col-xs-12 col-sm-6"
                fieldClass="form-control"
                fieldHintClass="help-block"
                fieldErrorClass="help-block">

                    <input type="hidden" name="<@csrf.tokenname/>"
                           value="<@csrf.tokenvalue uri="${formAction}"/>"/>

                    <div class="form-group">
                        <label for="salutation"
                               class="col-xs-3 control-label without-right-padding"><@message text="Anrede"/>
                        </label>

                        <div class="col-xs-6 col-sm-3">
                            <@f.select name="form.invoice.salutation" id="salutation" class="form-control" fieldOnly=true>
                                <@f.option value="Mr"><@message text="Herr"/></@f.option>
                                <@f.option value="Mrs"><@message text="Frau"/></@f.option>
                            </@f.select>
                        </div>
                    </div>

                    <@message text="Forename" lang="en" text2="Vorname" lang2="de" var="forenameLabel"/>
                    <@f.text maxlength="30" name="form.invoice.firstName" class="form-control capitalize filter-special" id="first_name" label="${forenameLabel}"/>

                    <@message text2="Nachname" lang2="de" text="Surname" lang="en" var="surnameLabel"/>
                    <@f.text maxlength="30" name="form.invoice.lastName" class="form-control capitalize filter-special" id="surname" label="${surnameLabel}"/>

                    <@message text2="E-mail-Adresse" lang2="de" text="Email Address" lang="en" var="emailLabel"/>
                    <@f.text maxlength="40" name="form.email" type="email" class="form-control" id="inputEmail" label="${emailLabel}"/>

                    <div class="form-group">
                        <label for="address"
                               class="col-xs-12 col-sm-3 control-label without-right-padding"><@message text="Straße, Hausnummer"/>
                        </label>

                        <div class="col-xs-12 col-sm-6">
                            <div class="col-xs-9 without-left-padding">
                                <@f.text maxlength="25" name="form.invoice.address1" class="form-control capitalize filter-special" id="street" fieldOnly=true/>
                            </div>
                            <div class="col-xs-3 without-left-padding without-right-padding">
                                <@f.text maxlength="4" name="form.invoice.houseNumber" class="form-control filter-special" id="house_number" fieldOnly=true/>
                            </div>

                            <@message text="Straße" var="streetLabel"/>
                            <@f.error name="form.invoice.address1" label="${streetLabel}"/>
                            <@message text="Hausnummer" var="housenumLabel"/>
                            <@f.error name="form.invoice.houseNumber" label="${housenumLabel}"/>

                        </div>
                    </div>


                    <@message text2="Vorwahl, Telefonnr." lang2="de" text="Phone number" lang="en" var="phoneLabel"/>
                    <@f.text name="form.invoice.phone" class="form-control filter-special" id="phone_number" label="${phoneLabel}"/>

                    <@message text2="Adresszusatz" lang2="de" text="Additional Address" lang="en" var="additionalLabel"/>
                    <@f.text maxlength="30" name="form.invoice.address2" class="form-control capitalize filter-special" id="full_address" label="${additionalLabel}"/>


                    <div class="form-group">
                        <label for="zip"
                               class="col-xs-12 col-sm-3 control-label without-right-padding zip-label"><@message text="PLZ, Ort"/>
                        </label>

                        <div class="col-xs-12 col-sm-6">
                            <div class="col-xs-5 without-left-padding">
                                <@f.text maxlength="7" name="form.invoice.zip" class="form-control filter-special" id="zip" fieldOnly=true/>
                            </div>
                            <div class="col-xs-7 without-left-padding without-right-padding">
                                <@f.text maxlength="30" name="form.invoice.city" class="form-control capitalize filter-special" id="city" fieldOnly=true/>
                            </div>

                            <@message text="PLZ" var="zipLabel"/>
                            <@f.error name="form.invoice.zip" label="${zipLabel}"/>
                            <@message text="Ort" var="cityLabel"/>
                            <@f.error name="form.invoice.city" label="${cityLabel}"/>

                        </div>
                    </div>


                    <@message text='Country' lang="en" text2="Land" lang2="de" var="countryLabel"/>
                    <@f.select name="form.invoice.country" class="form-control" id="country" label="${countryLabel}">
                        <#list countries?keys as key>
                            <@f.option value="${key}">${countries[key]}</@f.option>
                        </#list>
                    </@f.select>

                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-8 col-xs-12 delivery-box-switcher-wrapper">
                            <div class="checkbox">
                                <label>
                                    <@f.checkbox id="cbShowDelivery" name="form.customDelivery"
                                       value="true" class="delivery-box-switcher" fieldOnly=true/>
                                     <@message text="Abweichende Lieferadresse"/>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group delivery-address">
                        <label for="salutation"
                               class="col-sm-3 control-label without-right-padding"><@message text="Anrede"/>
                        </label>

                        <div class="col-xs-6 col-sm-3">
                            <@f.select name="form.delivery.salutation" id="del_salutation" class="form-control" fieldOnly=true>
                                    <@f.option value="Mr"><@message text="Herr"/></@f.option>
                                    <@f.option value="Mrs"><@message text="Frau"/></@f.option>
                                </@f.select>
                        </div>
                    </div>

                    <div class="delivery-address">
                        <@message text="Forename" lang="en" text2="Vorname" lang2="de" var="forenameLabel"/>
                        <@f.text maxlength="30" name="form.delivery.firstName" class="form-control capitalize filter-special" id="del_first_name" label="${forenameLabel}"/>

                        <@message text2="Nachname" lang2="de" text="Surname" lang="en" var="surnameLabel"/>
                        <@f.text maxlength="30" name="form.delivery.lastName" class="form-control capitalize filter-special" id="del_surname" label="${surnameLabel}"/>
                    </div>

                    <div class="form-group delivery-address">
                        <label for="del_address"
                               class="col-xs-12 col-sm-3 control-label without-right-padding two-line"><@message text="Straße, Hausnummer"/>
                        </label>

                        <div class="col-xs-12 col-sm-6">
                            <div class="col-xs-9 without-left-padding">
                                <@f.text maxlength="25" name="form.delivery.address1" class="form-control capitalize filter-special" id="street" fieldOnly=true/>
                            </div>
                            <div class="col-xs-3 without-left-padding without-right-padding">
                                <@f.text maxlength="4" name="form.delivery.houseNumber" class="form-control filter-special" id="house_number" fieldOnly=true/>
                            </div>
                            <@message text="Straße" var="streetLabel"/>
                            <@f.error name="form.delivery.address1" label="${streetLabel}"/>
                            <@message text="Hausnummer" var="housenumLabel"/>
                            <@f.error name="form.delivery.houseNumber" label="${housenumLabel}"/>

                        </div>
                    </div>

                    <div class="delivery-address">
                        <@message text2="Vorwahl, Telefonnr." lang2="de" text="Phone number" lang="en" var="phoneLabel"/>
                        <@f.text name="form.delivery.phone" class="form-control filter-special" id="del_phone_number" label="${phoneLabel}"/>

                        <@message text2="Adresszusatz" lang2="de" text="Additional Address" lang="en" var="additionalLabel"/>
                        <@f.text maxlength="30" name="form.delivery.address2" class="form-control capitalize filter-special" id="del_full_address" label="${additionalLabel}"/>
                    </div>

                    <div class="form-group delivery-address">
                        <label for="del_zip"
                               class="col-xs-12 col-sm-3 control-label without-right-padding"><@message text="PLZ, Ort"/>
                        </label>

                        <div class="col-xs-12 col-sm-6">
                            <div class="col-xs-5 without-left-padding">
                                <@f.text maxlength="7" name="form.delivery.zip" class="form-control filter-special" id="zip" fieldOnly=true/>
                            </div>
                            <div class="col-xs-7 without-left-padding without-right-padding">
                                <@f.text maxlength="30" name="form.delivery.city" class="form-control capitalize filter-special" id="city" fieldOnly=true/>
                            </div>

                            <@message text="PLZ" var="zipLabel"/>
                            <@f.error name="form.delivery.zip" label="${zipLabel}"/>
                            <@message text="Ort" var="cityLabel"/>
                            <@f.error name="form.delivery.city" label="${cityLabel}"/>
                        </div>
                    </div>

                    <div class="delivery-address">
                        <@message text='Country' lang="en" text2="Land" lang2="de" var="countryLabel"/>
                    <@f.select name="form.delivery.country" class="form-control" id="del_country" label="${countryLabel}">
                        <#list countries?keys as key>
                            <@f.option value="${key}">${countries[key]}</@f.option>
                        </#list>
                    </@f.select>
                    </div>

                    <div class="form-group">
                        <div class="col-xs-6 col-xs-offset-3">
                            <button type="submit"><@message text='Weiter' />
                            </button>
                        </div>
                    </div>

                    <input type="hidden" name="redirect">
                </@f.form>
            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>