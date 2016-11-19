<#import "${t_layout}/1column.ftl" as layout>
<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<@layout.onecolumn>

    <@session />

<div class="row start-checkout">
    <div class="col-xs-12">
        <div class="login-panel">
            <div class="page-header">
                <h3><@message text="Ich habe bereits ein"/> <@message text="Kundenkonto"/></h3>
            </div>


            <form class="form-horizontal">
                <div class="form-group">
                    <label for="input-email"
                           class="col-sm-4 col-xs-12 control-label"><@message text="E-mail Adresse"/></label>

                    <div class="col-sm-8 col-xs-12">
                        <input type="email" class="form-control" id="input-email" placeholder="Email">
                    </div>
                </div>
                <div class="form-group">
                    <label for="input-password"
                           class="col-sm-4 col-xs-12 control-label"><@message text="Passwort"/></label>

                    <div class="col-sm-8 col-xs-12">
                        <input type="password" class="form-control" id="input-password" placeholder="Passwort">
                    </div>
                </div>
                <div class="form-group">
                        <div class="col-xs-12 col-sm-offset-4 col-sm-8">
                            <button type="submit" class="login-btn"><@message text="Anmelden"/></button>
                        </div>
                </div>

                <div class="form-group">
                        <div class="col-xs-12 col-sm-offset-4 col-sm-8">
                            <button type="submit" class="registr-btn"><@message text="Neukunde? Jetz registrieren"/></button>
                        </div>


                </div>

                <div class="form-group">
                        <div class="col-xs-12 col-sm-offset-4 col-sm-8">
                            <button type="button" class="login-btn"
                                    onclick="window.location.href='/checkout/address'"><@message text='Ich bin neu hier / Gast' />
                            </button>
                    </div>


                </div>

                <input type="hidden" name="redirect">
            </form>
    </div>
    </div>
</div>
</@layout.onecolumn>