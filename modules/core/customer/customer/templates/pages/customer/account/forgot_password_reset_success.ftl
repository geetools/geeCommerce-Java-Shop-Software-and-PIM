<#import "${t_layout}/1column.ftl" as layout>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@layout.onecolumn>

<div class="customer-account-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">

                <h3 class="h3">
                    <@message text="Your password has been changed. You can login now." lang="en" text2="Ihr Passwort wurde geändert. Sie können jetzt anmelden." lang2="de"/>
                </h3>

                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3">
                            <button type="submit" class="login-btn forgot-password-reset-success-btn"><i class="pull-right"></i>
                                <@message text='Login' lang="en" text2="Anmelden" lang2="de"/></button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>