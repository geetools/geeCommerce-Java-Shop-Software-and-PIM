
var server_culture = "${actionBean.culture}";
var client_culture = server_culture.replace("_", "-");
var settings = new Settings({
    culture: client_culture,
    currency: client_culture,
    currencySymbol: "${actionBean.currency}"
});

