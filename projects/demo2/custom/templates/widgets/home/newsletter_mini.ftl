<div class="newsletter-mini-widget-block">
    <strong><@message text='Newsletter Subscription' lang="en" text2='Newsletter Anmeldung' lang2="de" /></strong>

    <p><@message text="No more missing out on special offers!" lang="en" text2='Mit unserem Newsletter keine Angebote<br>mehr verpassen!' lang2="de" /></p>

    <form>
        <input type="text" name="newsletter-email_${wd_guid}" placeholder="<@message text='Your email address' lang="en" text2='Ihre E-Mail-Adresse' lang2="de" />"/>
        <button type="submit" name="newsletter-email-button_${wd_guid}" class="btn"><@message text="Go" lang="en" text2="Absenden" lang2="de" /></button>
    </form>
</div>

<script>
    $('button[name=newsletter-email-button_${wd_guid}]').on('click', function () {
        var email = $('input[name=newsletter-email_${wd_guid}]').val();
        if (isValidEmailAddress(email)) {
            subscribe(email);
        } else {
            alert('<@message text="Please provide a valid email address." lang="en" text2="Bitte geben Sie eine gültige E-Mail Adresse ein." lang2="de" />');
        }
        return false;
    });

    function isValidEmailAddress(emailAddress) {
        var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
        return pattern.test(emailAddress);
    };

    function subscribe(email) {
        $.post('/news-subscription/subscribe/', {'email': email}, function (data) {
            alert('<@message text="Thank you for your interest! We have added you to our newsletter subscription." lang="en" text2="Vielen Dank! Wir haben Sie für den Newsletter angemeldet." lang2="de" />');
            //window.location.href = "/news-subscription/bestaetigung/";
        }, "json");
    }
</script>