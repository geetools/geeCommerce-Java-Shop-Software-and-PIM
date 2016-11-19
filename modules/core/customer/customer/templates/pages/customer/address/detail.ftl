<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>

<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row customer-address-overview">
                <h1 class="h1"><@message text='Address Details' lang="en" text2="Adressen Details" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <#if address??>
                        <!-- content area -->
                        <div class="col-xs-12 content-panel">
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
                                            <li> ${addressLine!}</li>
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
                            </div>

                            <div class="row addr-set-default">
                                <div class="col-xs-12">
                                    <#if address.defaultDeliveryAddress>
                                        <span class="addr-default-delivery"><@message text='** This address is the default delivery address.' lang="en" text2="** Diese Adresse ist die Standard-Lieferadresse." lang2="de"/></span>
                                    </#if>
                                </div>
                                <div class="col-xs-12">
                                    <#if address.defaultInvoiceAddress>
                                        <span class="addr-default-invoice"><@message text='** This address is the default invoice address.' lang="en" text2="** Diese Adresse ist die Standard-Rechnungsadresse." lang2="de"/></span>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </#if>

                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12">
                                <a href="/customer/account/orders-overview"><span><@message text='Back' lang="en" text2="Zurück" lang2="de"/></span></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</@layout.onecolumn>