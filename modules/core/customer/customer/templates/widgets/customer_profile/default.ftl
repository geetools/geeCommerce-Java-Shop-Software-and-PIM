<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>

<@session />

<a class="customer-profile-link hidden-xs hidden-xs-down" id="popoverCustomerProfile" rel="popover" tabindex="0" role="button"
   data-toggle="popover" data-placement="bottom">
    <i class="glyphicon glyphicon-user fa fa-user" aria-hidden="true"></i>
    <small><@message text="User" lang="en" text2="Benutzer" lang2="de" /></small>
</a>

<a class="customer-profile-link-mobile hidden-sm hidden-md hidden-lg hidden-sm-up" href="/customer/account/overview">
    <i class="glyphicon glyphicon-user fa fa-user" aria-hidden="true"></i>
    <small><@message text="User" lang="en" text2="Benutzer" lang2="de" /></small>
</a>

<div id="popoverCustomerProfile_content" class="customer-profile-popover">
    <div id="customer-profile-content" class="customer-profile-content">
        <!-- Filled dynamically via JavaScript -->
    </div>
</div>

<div id="popoverCustomerProfileHeader_content" hidden>
    <div class="row">
        <div class="col-xs-12">
            <div id="customer-profile-title" class="customer-profile-title col-xs-6 without-left-padding">
                <!-- Filled dynamically via JavaScript -->
            </div>
            <div class="col-xs-6 without-right-padding">
                <span class="close">&times;</span>
            </div>
        </div>
   </div>
</div>



