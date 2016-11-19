<#import "${t_layout}/1column.ftl" as layout>

<@session />

<@layout.onecolumn>

<div class="order-summary-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row ">
                <div class="col-xs-12 col-lg-9 header">
                    <h1 class="h1"><@message text="Your Orders" lang="en" text2="Meine Bestellungen" lang2="de" /></h1>
                </div>
                <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 date-filter form-inline">
                    <#if orders??>
                        <span class="num-orders">${orders?size}</span>&nbsp;<@message text="orders placed in" lang="en" text2="Bestellungen in" lang2="de"/>
                    <#else>
                        <span class="num-orders"><@message text="No orders placed in " lang="en" text2="Keine Bestellung aufgegeben" lang2="de"/></span>
                    </#if>
                    <select id="order-filter-date" class="form-control">
                        <option value="all"> <@message text="all time" lang="en" text2="alle Zeit" lang2="de"/>
                        <#list orderCreatedDates?keys as key>
                            <option value="${key}" ${orderCreatedDates[key]}>
                                <#if key?string == 'last30'>
                                    <@message text="past 30 days" lang="en" text2="in den letzten 30 Tagen" lang2="de"/>
                                <#elseif key?string == 'months-6'>
                                    <@message text="past 6 Months" lang="en" text2="in den letzten 6 Monaten" lang2="de"/>
                                <#else>
                                    ${key?string}
                                </#if>
                            </option>
                        </#list>
                    </select>
                </div>
            </div>

            <div class="row">
                <!-- content area -->
                <div class="col-xs-12">
                    <ul class="nav nav-tabs">
                        <li class="active"><a data-toggle="tab" href="#all"><@message text="Orders" lang="en" text2="Bestellungen" lang2="de"/></a></li>
                        <li><a data-toggle="tab" href="#open"><@message text="Open Orders" lang="en" text2="Offene Bestellungen" lang2="de"/></a></li>
                        <li><a data-toggle="tab" href="#cancelled"><@message text="Cancelled Orders" lang="en" text2="Stornierte Bestellungen" lang2="de"/></a></li>
                    </ul>

                    <div class="tab-content">
                        <div id="all" class="tab-pane fade in active">
                            <h3 class="h3"><@message text="Orders" lang="en" text2="Bestellungen" lang2="de"/></h3>
                            <div class="col-xs-12 order-summary-overview">
                                <@import uri="/order-summary/orders">
                                    <@param key="view" value="all" />
                                    <@param key="orderFilterDate" value="${orderFilterDate}" />
                                </@import>
                            </div>
                        </div>
                        <div id="open" class="tab-pane fade">
                            <h3 class="h3"><@message text="Open Orders" lang="en" text2="Offene Bestellungen" lang2="de"/></h3>
                            <div class="col-xs-12 order-summary-overview">
                                <@import uri="/order-summary/orders">
                                    <@param key="view" value="open" />
                                    <@param key="orderFilterDate" value="${orderFilterDate}" />
                                </@import>
                            </div>
                        </div>
                        <div id="cancelled" class="tab-pane fade">
                            <h3 class="h3"><@message text="Cancelled Orders" lang="en" text2="Stornierte Bestellungen" lang2="de"/></h3>
                            <div class="col-xs-12 order-summary-overview">
                                <@import uri="/order-summary/orders">
                                    <@param key="view" value="cancelled" />
                                    <@param key="orderFilterDate" value="${orderFilterDate}" />
                                </@import>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

</@layout.onecolumn>