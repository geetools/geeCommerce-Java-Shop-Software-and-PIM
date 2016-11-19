{{#customer}}
    <div id="cp-title" class="cp-title"><strong>{{customer.salutation}}.&nbsp;{{customer.forename}}&nbsp;{{customer.surname}}</strong></div>
{{/customer}}

{{^customer}}
    <span class="cp-title"><strong><@message text='Customer' lang="en" text2="Benutzer" lang2="de"/></strong></span>
{{/customer}}


