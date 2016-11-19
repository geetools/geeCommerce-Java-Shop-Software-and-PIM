<div id="popover" class="newsletter-widget-block">

    <div class="row">

        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

            <form>
                <div class="newsletter-widget-block-more">
                    <div class="col-xs-12">
                        <span class="newsletter-widget-block-title"><@message text="Subscribe" lang="en" text2="Anmeldung" lang2="de" /></span>
                    </div>

                    <div class="col-xs-12">
                        <div class="col-xs-4 without-left-padding without-right-padding">
                            <p><@message text="Be the first to receive our special offers via our newsletter!" lang="en" text2="Aktuellste Angebote und brandneue Aktionen direkt per Newsletter!" lang2="de" /></p>
                        </div>
                        <div class="col-xs-8">
                            <span></span>
                        </div>
                    </div>

                    <div class="col-xs-12">
                        <input type="text" name="newsletter-email_${wd_guid}" placeholder="<@message text="Your E-Mail Address" lang="en" text2="Ihre E-Mail-Adresse" lang2="de" />"/>
                    </div>

                    <div class="col-xs-12">
                        <div class="col-xs-6 without-left-padding without-right-padding">
                            <p><@message text="Abmeldung" lang="en" text2="Unsubscribe" lang2="de" />
                                <br/><@message text="at any time." lang="en" text2="jederzeit möglich." lang2="de" /></p>
                        </div>

                        <div class="col-xs-6 without-left-padding ">
                            <button type="submit" name="newsletter-email-button_${wd_guid}"
                                    class="btn"><@message text="Subscribe&nbsp;now" lang="en" text2="Jetzt&nbsp;anmelden" lang2="de" /></button>
                        </div>
                    </div>
                </div>
        </div>
        </form>
    </div>
</div>


<script>
    $('button[name=newsletter-email-button_${wd_guid}]').on('click', function () {
        var email = $('input[name=newsletter-email_${wd_guid}]').val();
        if (isValidEmailAddress(email)) {
            subscribe(email);
        } else {
            alert('<@message text="Bitte geben Sie eine gültige E-Mail Adresse ein." />');
        }

        return false;
    });

    function isValidEmailAddress(emailAddress) {
        var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
        return pattern.test(emailAddress);
    };

    function subscribe(email) {
        $.post('/news-subscription/subscribe/', {'email': email}, function (data) {
            alert('<@message text="Vielen Dank! Wir haben Sie für den Newsletter angemeldet." />');
            //window.location.href = "/news-subscription/bestaetigung/";
        }, "json");
    }
</script>