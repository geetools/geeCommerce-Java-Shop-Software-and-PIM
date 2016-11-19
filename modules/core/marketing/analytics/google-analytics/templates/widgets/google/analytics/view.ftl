<#if accountId?? && domain??>
<script type="text/javascript">

    // Set to the same value as the web property used on the site
    var gaProperty = '${accountId}';

    // Disable tracking if the opt-out cookie exists.
    var disableStr = 'ga-disable-' + gaProperty;
    if (document.cookie.indexOf(disableStr + '=true') > -1) {
        window[disableStr] = true;
    }

    // Opt-out function
    function gaOptout() {
//        window.alert("gaOutput method has been started.");
        document.cookie = disableStr + '=true; expires=Thu, 31 Dec 2099 23:59:59 UTC; path=/';
        window[disableStr] = true;
    }

	_ga.create('${accountId}', <#if !domain?contains('localhost')>'${domain}'<#else>{
		'cookieDomain' : 'none'
	}</#if>);
	_gaq.push(['_trackPageview']);
</script>
</#if>