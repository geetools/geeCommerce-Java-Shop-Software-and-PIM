<#import "${t_layout}/1column.ftl" as layout>
<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row customer-address-overview">
                <h1 class="h1"><@message text='Manage Addresses' lang="en" text2="Adressen verwalten" lang2="de"/></h1>

                <div class="col-xs-12 col-sm-6 col-md-6 col-lg-8">

                    <div class="row">
                        <div class="form-group">
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4">
                                        <a href="/customer/address/new"
                                           class="btn btn-info"><@message text="Add new address" lang="en" text2="Eine neue Adresse eingeben" lang2="de"/></a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-xs-12">
                            <#if addresses??>
                                <#list addresses as address>
                                    <!-- content area -->
                                    <#if address.defaultDeliveryAddress>
                                    <div class="col-xs-12 content-panel default">
                                    <#elseif address.defaultInvoiceAddress>
                                    <div class="col-xs-12 content-panel default">
                                    <#else>
                                    <div class="col-xs-12 content-panel">
                                    </#if>

                                    <div class="row">
                                        <div class="col-xs-12 col-lg-10 panel-cell">
                                            <ul class="a-nostyle">
                                                <li>
                                                    <strong>
                                                        <#if address.forename??>
                                                        ${address.salutation}.&nbsp;
                                                        </#if>
                                                        <#if address.forename??>
                                                        ${address.forename}&nbsp;
                                                        </#if>
                                                        <#if address.surname??>
                                                        ${address.surname}
                                                        </#if>
                                                    </strong>
                                                </li>
                                                <#if address.company??>
                                                    <li>${address.company}</li>
                                                </#if>
                                                <#list address.addressLines as addressLine>
                                                    <li> <#if addressLine?has_content >
                                                        ${addressLine} ${address.houseNumber}
                                                    </#if></li>
                                                </#list>
                                                <li>${address.zip} ${address.city}</li>
                                                <#if address.state??>
                                                    <li>${address.state??}</li>
                                                </#if>
                                                <#if address.country??>
                                                    <li>${actionBean.countries[address.country]}</li>
                                                </#if>
                                                <#if address.telephone??>
                                                    <li><@message text="Telephone:" lang="en" text2="Telefon:" lang2="de"/>&nbsp;${address.telephone}</li>
                                                </#if>
                                                <#if address.fax??>
                                                    <li><@message text="Fax:" lang="en" text2="Fax:" lang2="de"/>&nbsp;${address.fax}</li>
                                                </#if>
                                            </ul>
                                        </div>

                                        <div class="col-xs-12 col-lg-2">
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-xs-12">
                                                        <a href="/customer/address/edit/${address.id}"
                                                           class="btn addr-modify-btn"><@message text="Edit" lang="en" text2="Ändern" lang2="de"/></a>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="row">
                                                    <div class="col-xs-12">
                                                        <a href="/customer/address/delete/${address.id}"
                                                           class="btn addr-modify-btn"><@message text="Delete" lang="en" text2="Löschen" lang2="de"/></a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row addr-set-default">
                                        <div class="col-xs-12">
                                            <#if address.defaultDeliveryAddress>
                                                <span class="addr-default-delivery"><@message text='** This address is the default delivery address.' lang="en" text2="** Diese Adresse ist die Standard-Lieferadresse." lang2="de"/></span>
                                            <#else>
                                                <a href="/customer/address/default-delivery/${address.id}"><@message text='Set address as default delivery address' lang="en" text2="Set-Adresse als Standard-Lieferadresse" lang2="de"/></a>
                                            </#if>
                                        </div>
                                        <div class="col-xs-12">
                                            <#if address.defaultInvoiceAddress>
                                                <span class="addr-default-invoice"><@message text='** This address is the default invoice address.' lang="en" text2="** Diese Adresse ist die Standard-Rechnungsadresse." lang2="de"/></span>
                                            <#else>
                                                <a href="/customer/address/default-invoice/${address.id}"><@message text="Set address as default invoice address" lang="en" text2="Set-Adresse als Standard-Rechnungsadresse" lang2="de"/></a>
                                            </#if>
                                        </div>
                                    </div>

                                </div>
                                </#list>
                            </#if>
                        </div>
                        </div>
                            <div class="row">
                                <div class="form-group">
                                    <div class="row">
                                        <div class="col-xs-12">
                                            <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4">
                                                <a href="/customer/account/overview"
                                                   class="btn btn-info"><@message text="Back to Account" lang="en" text2="Zurück zu Konto" lang2="de"/></a>
                                            </div>
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