<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>


<@f.form action="/catalog/product/process-attr-bean-test">

    <@f.haserrors>
        <@message text="Please fix the errors!" lang="en" text2="Bitte korrigieren Sie die Fehler!" lang2="de"/>
        <@f.errors/>
    </@f.haserrors>

	<@f.text name="product.attr:ean" label="EAN"/>

	<@f.select name="product.attr:include_in_feed" label="Include in Feed">
		<@f.option value="">Select</@f.option>
		<@f.option value="true">Yes</@f.option>
		<@f.option value="false">No</@f.option>
	</@f.select>

	<@f.select name="product.attr:color" label="Color">
		<@f.option value="">Select</@f.option>
		<@f.option value="11738420900610100">Blue</@f.option>
		<@f.option value="11738420901310100">Red</@f.option>
		<@f.option value="11738420901410100">Yellow</@f.option>
	</@f.select>

	<@f.text name="product.attr:price_per_kg" label="Price per KG"/>

	<@f.submit name="submit" value="Submit" />



</@f.form>