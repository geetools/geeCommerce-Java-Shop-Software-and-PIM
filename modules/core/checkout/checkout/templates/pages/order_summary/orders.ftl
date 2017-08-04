<div class="row" xmlns="http://www.w3.org/1999/html">
    <div class="row">
        <div class="col-xs-12">

        <#if orders?size == 0>
            <span class="no-orders"><@message text="No orders found" lang="en" text2="Keine Bestellung gefunden" lang2="de"/></span>
        </#if>

        <#list orders as order>

            <!-- header -->
            <div class="col-xs-12 header">
                <div class="row">
                    <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 header-cell">
                        <div class="col-xs-12 ">
                            <@message text="ORDER PLACED" lang="en" text2="BESTELLUNG AUFGEGEBEN" lang2="de"/>
                        </div>
                        <div class="col-xs-12">
                        ${order.createdOn?date}
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 header-cell">
                        <div class="col-xs-12">
                            <@message text="TOTAL" lang="en" text2="GESAMT" lang2="de"/>
                        </div>
                        <div class="col-xs-12">
                            <strong>${order.totalAmount?string.currency?replace(" ", "&nbsp;")}</strong>
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 header-cell">
                        <div class="col-xs-12">
                            <@message text="DISPATCH TO" lang="en" text2="VERSAND NACH" lang2="de"/>
                        </div>
                        <div class="col-xs-12 a-strong">

                                    <#if order.deliveryOrderAddress.firstName??>
                                    ${order.deliveryOrderAddress.firstName}
                                    <#else>
                                        &nbsp;
                                    </#if>
                                    <#if order.deliveryOrderAddress.lastName??>
                                    ${order.deliveryOrderAddress.lastName}
                                    <#else>
                                        &nbsp;
                                    </#if>
                        </div>
                    </div>

                    <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 header-cell">
                        <div class="col-xs-12">
                            <@message text="ORDER #" lang="en" text2="BESTELLUNG #" lang2="de"/> ${order.orderNumber}
                        </div>
                        <div class="col-xs-12">
                            <strong><a href="/order-summary/detail/${order.id}"> <@message text="Order Details" lang="en" text2="Bestelldetails" lang2="de"/></a></strong>
                        </div>
                    </div>
                </div>
            </div>

            <!-- content -->
            <div class="col-xs-12 content">
                <#list order.orderItems as item>
                    <#assign product=item.product />
                    <#include "init_prices.ftl"/>
                    <div class="row">

                        <div class="col-xs-12">
                            <div class="col-xs-12 hr"></div>
                        </div>

                        <div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 content-cell">
                            <a href="<@print src=product value='product.URI'/>">
                                <img class="product-image" product="${item.productId}" src="<@catMediaURL src=product width=133 height=147 parent=true />"/>
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
                                <span class="product-price a-red"><@message text="Total:" lang="en" text2="Gesamtpreis:" lang2="de" />
                                    <@print src=order.calculationResult.itemResults value="def row = (Map) self[new Id('${item.productId}')]; row['gross_subtotal'];" format="currency" />
                                </span>
                            </div>
                        </div>

                    </div>
                </#list>
            </div>
        </#list>

        </div>
    </div>
</div>