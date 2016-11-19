function Settings(properties){
    if(properties)
        this.properties = properties;
}

Settings.prototype.properties = {
    culture : "en-US",
    currency: "en-US",
    currencySymbol: "$"
}
