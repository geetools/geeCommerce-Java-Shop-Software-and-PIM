<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>

<@session />

<#assign orderId = "">
<#if order??>
    <#assign orderId = order.id?string>
    <#assign order = order>
</#if>

<@layout.onecolumn>

<div class="order-summary-container">
    <div class="row center-block order-summary-detail">
        <div class="col-xs-12">
            <div class="row header">
                <div class="col-xs-12 ">
                    <h1 class="h1"><@message text="Order Details" lang="en" text2="Bestellung" lang2="de" /></h1>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 order-created">
                    <@message text="Ordered on" lang="en" text2="Bestellartikel" lang2="de"/>&nbsp;${order.createdOn?date}
                </div>
                <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 pull-right order-number">
                    <@message text="Ordered #" lang="en" text2="Bestellung #" lang2="de"/>&nbsp;${order.orderNumber}
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="col-xs-12 content-panel order-personal-data">
                        <div class="col-xs-12 col-sm-4 content-cell">
                            <h4 class="h4"><@message text="Delivery Address" lang="en" text2="Lieferadresse" lang2="de"/></h4>
                            <div class="row">
                                <div class="col-xs-12">
                                    <#if order.deliveryOrderAddress.firstName??>
                                        <span>${order.deliveryOrderAddress.firstName}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                    <#if order.deliveryOrderAddress.lastName??>
                                        <span>${order.deliveryOrderAddress.lastName}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                </div>
                                <div class="col-xs-12">
                                    <#if order.deliveryOrderAddress.address1??>
                                        <span>${order.deliveryOrderAddress.address1}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                    <#if order.deliveryOrderAddress.houseNumber??>
                                        <span>${order.deliveryOrderAddress.houseNumber}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                </div>
                                <div class="col-xs-12">
                                    <#if order.deliveryOrderAddress.zip??>
                                        <span>${order.deliveryOrderAddress.zip},</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                    <#if order.deliveryOrderAddress.city??>
                                        <span>${order.deliveryOrderAddress.city}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                    <#if order.deliveryOrderAddress.address2??>
                                        <span>${order.deliveryOrderAddress.address2}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                </div>
                                <div class="col-xs-12">
                                    <#if order.deliveryOrderAddress.country??>
                                        <span>${actionBean.countries[order.deliveryOrderAddress.country]}</span>
                                    <#else>
                                        <span>&nbsp;</span>
                                    </#if>
                                </div>
                            </div>
                        </div>
                        <div class="col-xs-12 col-sm-5 content-cell">
                            <h4 class="h4"><@message text="Payment Method" lang="en" text2="Zahlungsart" lang2="de"/></h4>
                            <div class="row">
                                <div class="col-xs-12">
                                ${order.orderPayment.paymentMethodLabel}
                                </div>
                            </div>
                        </div>
                        <div class="col-xs-12 col-sm-3 content-cell pull-right">
                            <h4 class="h4"><@message text="Order Summary" lang="en" text2="Bestellübersicht" lang2="de"/></h4>
                            <div class="row">
                                <div class="col-xs-12 subtotal-price">
                                    <@message text="Item(s) Subtotal:" lang="en" text2="Zwischensumme:" lang2="de"/> ${order.calculationResult.gross_subtotal?string.currency}
                                </div>
                                <div class="col-xs-12 shipping-price">
                                    <@message text="Pastage & Packing:" lang="en" text2="Versandkosten:" lang2="de"/> ${order.calculationResult.gross_shipping_amount?string.currency}
                                </div>
                                <div class="col-xs-12 net-total-price">
                                    <@message text="Total Before VAT:" lang="en" text2="Gesamt vor MwSt." lang2="de"/> ${order.calculationResult.net_grand_total?string.currency}
                                </div>
                                <div class="col-xs-12 tax-amount">
                                    <@message text="VAT:" lang="en" text2="Inkl. MwSt." lang2="de"/> ${order.calculationResult.grand_total_tax_amount?string.currency}
                                </div>
                                <div class="col-xs-12 grand-total-price">
                                    <@message text="Grand Total" lang="en" text2="Gesamtsumme" lang2="de"/> ${order.calculationResult.gross_grand_total?string.currency}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12 ">
                    <div class="col-xs-12 content-panel content">
                        <#list order.orderItems as item>
                            <#assign product=item.product />
                            <#include "init_prices.ftl"/>
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="col-xs-12 hr"></div>
                                </div>

                                <div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 content-cell">
variant: <@print src=product value='product.variant'/><br/>                       
visibleInProductList: <@print src=product value='product.visibleInProductList'/><br/>
                                    <a href="<@print src=product value='product.URI'/>">
                                        <img class="product-image" product="${item.productId}" src="<@catMediaURL uri="${product.mainImageURI!}" width=133 height=147 />"/>
                                    </a>
                                </div>

                                <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 content-cell product-description">
                                    <div class="col-xs-12">
                                        <a class="a-strong" href="<@print src=product value='product.URI'/>">
                                            <@attribute src=product code="name" />
                                    <@attribute src=product code="name2" /></a>
                                    </div>
                                    <div class="col-xs-12">
                                        <@message text="Artikelnummer:" /> <@attribute src=product code="article_number" />
                                    </div>
                                    <div class="col-xs-12">
                                        <span class="product-price"><@message text="Price" lang="en" text2="Einzelpreis:" lang2="de" />
                                            <@print src=order.calculationResult.itemResults value="def row = (Map) self[new Id('${item.productId.s}')]; row['cart_price'];" format="currency"/>
                                        </span>
                                    </div>
                                    <div class="col-xs-12">
                                        <@message text="Quantity:" lang="en" text2="Menge:" lang2="de" /> ${item.quantity}
                                    </div>
                                    <div class="col-xs-12">
                                        <span class="product-price a-red"><@message text="Total:" lang="en" text2="Gesamtsumme:" lang2="de" />
                                            <@print src=order.calculationResult.itemResults value="def row = (Map) self[new Id('${item.productId}')]; row['gross_subtotal'];" format="currency" />
                                        </span>
                                    </div>
                                </div>

                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</@layout.onecolumn>