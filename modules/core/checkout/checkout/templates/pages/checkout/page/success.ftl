<#setting locale="de_DE">
<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<@layout.onecolumn>

    <@session />

<div class="row">
    <div class="col-xs-12 success-div">


        <div class="row">
            <div class="col-xs-12 success-speech-bubble">
                <div class="success-speech-bubble-block">
                    <div class="hidden-xs hidden-sm"><@message text='Vielen Dank für Ihre Bestellung' /></div>
                    <div class="hidden-lg hidden-md">
                        <h1><@message text='Vielen Dank' /></h1>
                        <@message text='für Ihre Bestellung' />
                    </div>
                </div>
            </div>
        </div>


    <#--<div class="success-div-text">-->

    <#--</div>-->

        <div class="row">
            <div class="col-xs-12 success-div-text">
                <@message text='Ihre Bestellung wird nun bearbeitet.' /><br/>
                <@message text='Wir bedanken uns für Ihr Vertrauen.' /><br/><br/>

                <div class="row">
                    <div class="col-xs-12">
                        <a class="btn-link"
                                 href="mailto:info@commerceboard.com"><@message text='info@commerceboard.com'/></a>
                    </div>

                    <div class="col-xs-12 success-return-div">
                        <a class="btn-link" href="/home" class="success-return">
                            <i class="pull-left sprite icon-left-arrow-black"></i>
                            <div>zurück zum Onlineshop</div>
                        </a>
                    </div>
                </div>

            </div>
        </div>

    </div>

</div>

</@layout.onecolumn>