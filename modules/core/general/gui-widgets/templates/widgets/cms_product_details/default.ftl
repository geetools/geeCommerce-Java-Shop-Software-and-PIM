<#if !wProduct??>
    <#assign wProduct=product >
</#if>

<#if wMode?? && wMode == "short">
    <div class="prd-details-short">
        <h1><@attribute src=wProduct code="name" /></h1>
        <strong><@attribute src=wProduct code="name2" /></strong>
    </div>
<#else>
<div class="row">
    <div id="prd-details" class="col-xs-12 col-sm-9 col-md-9 col-lg-9">
        <#--<h3><@message text="Product Description" lang="en" text2="Produktbeschreibung" lang2="de" /></h3>-->

        <div class="prd-text">
            <h2>
                <@attribute src=wProduct code="name" />
                <span><@attribute src=wProduct code="name2" /></span>
            </h2>
        </div>

        <div class="prd-text">
            <@message text="Article No." lang="en" text2="Artikel-Nr." lang2="de" />
            <span><@attribute src=wProduct code="article_number" /></span>
        </div>
        <div class="prd-text">
            <@attribute_exists src=wProduct code="short_description" parent=true>
            			<@attribute src=wProduct code="short_description" make="list" parent=true/>
        			</@attribute_exists>
        </div>

        <div class="prd-text">
            <@attribute_exists src=wProduct code="description" parent=true>
						<@attribute src=wProduct code="description" parent=true />
					</@attribute_exists>
        </div>
    </div>
</div>
</#if>



