<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row customer-address-overview">
                <h1 class="h1"><@message text='Confirm delete the address' lang="en" text2="Löschen der Adresse bestätigen" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">


                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12">
                                    <span><@message text="To permanently remove this address from the address book, click Confirm." lang="en"
                                    text2="Um diese Adresse dauerhaft aus dem Adressbuch zu entfernen, klicken Sie auf Bestätigen." lang2="de"/></span>
                            </div>
                        </div>
                    </div>


                    <@f.form action="/customer/address/delete-confirm/${id}" method="post" ssl=true>
                        <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="/customer/address/delete-confirm/${id}"/>"/>

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

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                        <button type="submit">
                                            <@message text="Confirm" lang="en" text2="Bestätigen" lang2="de"/>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </#if>

                    </@f.form>

                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-12">
                                    <a href="/customer/address/overview"><span><@message text='Back to Manage Addresses' lang="en" text2="Zurück zur Adressen verwalten" lang2="de"/></span></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</@layout.onecolumn>