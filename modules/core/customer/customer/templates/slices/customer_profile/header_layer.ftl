<div class="col-xs-12">
    <#include "profile_menu.ftl"/>
</div>

{{#loggedIn}}
    <div class="cp-action-btn">
        <button class="cp-logout-btn"><@message text='Logout' lang="en" text2="Ausloggen" lang2="de"/></button>
    </div>
{{/loggedIn}}

{{^loggedIn}}
    <div class="cp-action-btn">
        <button class="cp-login-btn"><@message text='Login' lang="en" text2="Anmelden" lang2="de"/></button>
    </div>
{{/loggedIn}}

