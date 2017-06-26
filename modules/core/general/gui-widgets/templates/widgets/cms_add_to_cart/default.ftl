<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#-- 
We make sure that a session exists, so that the session-csrf-token can be generated,
which ensures that the first post of the form has a positive csrf-validation.
-->
<@session />

<#if !wProduct??>
    <#assign wProduct=product >
</#if>


<#if wProduct??>
    <#assign parentId = (wProduct.parentId.s)!"">


	<#-- Has product been marked as not saleable in admin panel? -->
	<#assign isSaleable=true>
	<#if (!wProduct.salable && !wProduct.hasSalableVariants())>
		<#assign isSaleable=false>
	</#if>
	
<!--
	id: ${wProduct.id.str}<br/>
	validForSelling: ${wProduct.validForSelling?string}<br/>
	selectOptions: ${selectOptions?string}<br/>
-->

    <#assign actionUrl = "/cart/add" />
    <#if wProduct.bundle >
        <#assign actionUrl = "/cart/add2" />
    </#if>


	<form action="${actionUrl}" method="post" name="addToCartForm_${wProduct.id}">
		<input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="${actionUrl}"/>"/>


        <input id="prd-cart-form-product-id" type="hidden" name="productId" value="${wProduct.id}" />
		<#if wProduct.bundle>

		<#else>
            <div class="prd-cart-qty">
                <label><@message text="Quantity" lang="en" text2="Menge" lang2="de" /></label>
                <select name="quantity" data-type="number">
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                    <option value="6">6</option>
                    <option value="7">7</option>
                    <option value="8">8</option>
                    <option value="9">9</option>
                    <option value="10">10</option>
                </select>
            </div>
		</#if>

        
        <div class="prd-cart-btn">
            <button type="submit" ><@message text="Add to basket" lang="en" text2="In den Warenkorb" lang2="de" /></button>
        </div>
        
    </form>
</#if>
