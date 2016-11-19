<#import "${t_layout}/1column.ftl" as layout>

<@session />

<@layout.onecolumn>

<div class="customer-account-container centeredContainer">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row customer-account-overview">
                <h1 class="h1"><@message text="My Account" lang="en" text2="Mein Konto" lang2="de" /></h1>

                <!-- menu column -->
                <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 menu-panel">

                    <ul class="a-nostyle">
                        <li>
                            <a href="/customer/account/orders-overview"><@message text="My Orders" lang="en" text2="Meine Bestellungen" lang2="de" /></a>
                        </li>
                        <li>
                            <a href="/customer/account/edit"><@message text="My Personal Data" lang="en" text2="Meine persönlichen Daten" lang2="de" />
                        </li>
                        <li>
                            <a href="/customer/address/overview"><@message text="My Addresses" lang="en" text2="Meine Anschriften" lang2="de" />
                        </li>
                        <li>
                            <a href="/customer/account/logout" class="btn"><@message text='Logout' lang="en" text2="Ausloggen" lang2="de"/></a>
                        </li>
                    </ul>
                </div>

                <!-- content area -->
                <div class="col-xs-12 col-sm-9 col-md-9 col-lg-9">

                    <div class="row">
                        <div class="col-xs-12 content-panel">
                            <#include "overview_orders_panel.ftl"/>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-xs-12 content-panel">
                            <#include "overview_settings_panel.ftl"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>