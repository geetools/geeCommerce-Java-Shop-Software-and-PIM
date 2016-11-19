<#if productDetails??>
    <#list productDetails as productDetail>
        <#if (detailCol == 1 && productDetail.position < 500) || (detailCol == 2 && productDetail.position >= 500)>
            <#if productDetail.group>
                <#assign showGroup = false />
                <#assign hasPictoAttr = false />
                <#list productDetail.attributeValues as attributeValue>
                    <@attribute_exists src=product code="${attributeValue.code}" parent=true>
                        <#assign showGroup = true />
                        
                        <#if (attributeValue.code?starts_with("picto-") || attributeValue.code?starts_with("picto_"))>
	                        <@attribute src=product code="${attributeValue.code}" parent=true var="pictoValue" />
	                        <#if pictoValue?has_content>
		                        <#assign hasPictoAttr = true />
	                        </#if>
                        </#if>
                    </@attribute_exists>
                </#list>
                <#if showGroup>
                    <div class="row" >
                        <div class="col-xs-12"><h3 code="${productDetail.code}">${productDetail.label}</h3></div>
                        <#list productDetail.attributeValues as attributeValue>
                            <@attribute_exists src=product code="${attributeValue.code}"  parent=true>
                                <#if attributeValue.attribute.frontendOutput.toId()?string == '2'>
                                    <div class="col-xs-12 "><@attribute src=product  code="${attributeValue.code}"  parent=true/></div>
                                <#else>
                                    <div class="col-xs-12 padding0">
                                        <div class="col-xs-6">${attributeValue.label}:</div> <div class="col-xs-6"><@attribute src=product  code="${attributeValue.code}"  parent=true/><br/></div>
                                    </div>
                                </#if>
                            </@attribute_exists>
                        </#list>
                    </div>
                </#if>
            </#if>
        </#if>
    </#list>

</#if>



