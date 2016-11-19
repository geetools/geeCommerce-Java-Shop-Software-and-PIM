<#assign s=JspTaglibs["/WEB-INF/taglibs/stripes.tld"]>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>

<#assign couponCode = actionBean.cart.couponCode!>
<#assign autoCoupon = actionBean.autoCoupon!>
<#assign showAutoCoupon = true>

<!-- Show input field for coupons if not coupon is active-->
<#if couponCode?? && !autoCoupon??>
    <div class="cart-coupon-form">
        <h2><@message text="Coupon code" lang="en" text2="Rabattcode" lang2="de" /></h2>
        <p>
            <@message text="Please enter your coupon code" lang="en" text2="Bitte geben Sie Ihren Rabattcode" lang2="de" />
            <input class="cart-coupon-code" type="text" value="" name="check_discount_code">
            <input class="cart-coupon-add" type="submit" value='<@message text="ověřit" editable=false />' />
        </p>
    </div>
</#if>






<!-- Coupon code is activated and it's not an auto one -->
<#if (couponCode?? && couponCode.coupon?? && !couponCode.coupon.auto)>
    <div class="cart-co">
        <#assign showAutoCoupon = false>
        <h2><@message text="Slevová akce" /></h2>

        <h3><@print src=couponCode value="couponCode.coupon.name.str" format="short-text" /></h3>

        <strong><@message text="Kód akce:" /> ${couponCode.code}</strong>
        <#if !actionBean.autoCoupon?? >
            <a href="#" class="cart-coupon-remove" ><@message text="odstranit"/></a>
        </#if>
        <div id="discount-description">
            <#if couponCode.coupon.description??>
                            <@print src=couponCode value="couponCode.coupon.description.str" />
                        </#if>
        </div>

        <#if actionBean.autoCoupon?? && actionBean.autoCoupons?size == 1 >
            <label style="color: #E76D1F;">
                <input type="checkbox" class="checkbox couponCodeRemove" name="useAutoCoupon" >
                <@message text="Nechci již využít svůj slevový kód" /> ${couponCode.code} <@message text=", chci raději využít aktuální akci" /> <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" />
            </label>
        </#if>

        <#if actionBean.autoCoupon?? && actionBean.autoCoupons?size gt 1 >
            <label style="color: #E76D1F;">
                <input type="checkbox" class="checkbox couponCodeRemove" name="useAutoCoupon" >
                <@message text="Nechci již využít svůj slevový kód" /> ${couponCode.code}
            </label>
        </#if>
    </div>
</#if>

<#if actionBean.autoCoupon?? && showAutoCoupon && actionBean.autoCoupons?size == 1  >
<div id="basket-active-discount-box">

    <h2><@message text="Slevová akce" /></h2>

    <h3><@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" /></h3>

    <form name="discount_set_state" action="/cart/switch-auto-coupon" method="post">
        <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="/cart/switch-auto-coupon"/>"/>
        <label><input type="hidden" name="h_discount_enabled" value="true">
            <#assign checked = "checked">
            <#if !actionBean.cart.useAutoCoupon>
                <#assign checked = "">
            </#if>

            <input type="checkbox" class="checkbox" onclick="document.forms.discount_set_state.submit();" ${checked} name="useAutoCoupon" >
            <@message text="Chci využít akci" />
        </label>

    </form>

    <div id="discount-description">
        <#if autoCoupon.coupon.description??>
						<@print src=autoCoupon value="autoCoupon.coupon.description.str" />
					</#if>
    </div>

    <label style="color: #E76D1F;">
        <input type="checkbox" class="checkbox couponCodeEnter" name="couponCodeEnter" >
        <@message text="Chci využít svůj slevový kód a nechci využít aktuální akci" /> <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" />
    </label>

</div>

<div id="basket-discount-form" style="display: none">
    <h2><@message text="Slevový kód" /></h2>
    <p>
        <@message text="Zadejte prosím slevový kód" />
        <input class="input-1 couponCode" type="text" value="" name="check_discount_code">
        <input class="button-81 couponCodeAdd" type="submit" value='<@message text="ověřit" editable=false />'>
    </p>
</div>

</#if>

<#if actionBean.autoCoupon?? && showAutoCoupon && (actionBean.autoCoupons?size > 1)  >
<div id="basket-active-discount-box">

    <h2><@message text="Slevová akce" /></h2>

    <form name="switchAutoCouponForm" action="/cart/set-auto-coupon" method="get">
        <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="/cart/set-auto-coupon"/>"/>
        <select id="selectedAutoCoupon" name="selectedAutoCoupon" onchange="this.form.submit()">
            <#list actionBean.autoCoupons as autoCouponOption>
                <option value="${autoCouponOption.id?string}"<#if (autoCouponOption?string == autoCoupon.id?string)> selected="selected"</#if>><@print src=autoCouponOption value="autoCouponOption.coupon.name.str" format="short-text" /></option>
            </#list>
        </select>
    </form>

    <h3><@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" /></h3>

    <form name="useAutoCouponForm" action="/cart/switch-auto-coupon" method="post">
        <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="/cart/switch-auto-coupon"/>"/>
        <label><input type="hidden" name="h_discount_enabled" value="true">
            <#assign checked = "checked">
            <#if !actionBean.cart.useAutoCoupon>
                <#assign checked = "">
            </#if>

            <input type="checkbox" class="checkbox" onclick="document.forms.useAutoCouponForm.submit();" ${checked} name="useAutoCoupon" >
            <@message text="Chci využít akci" />
        </label>

    </form>

    <div id="discount-description">
        <#if autoCoupon.coupon.description??>
						<@print src=autoCoupon value="autoCoupon.coupon.description.str" />
					</#if>
    </div>

    <label style="color: #E76D1F;">
        <input type="checkbox" class="checkbox couponCodeEnter" name="couponCodeEnter" >
        <@message text="Chci využít svůj slevový kód a nechci využít aktuální akci" /> <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" />
    </label>

</div>

<div id="basket-discount-form" style="display: none">
    <h2><@message text="Slevový kód" /></h2>
    <p>
        <@message text="Zadejte prosím slevový kód" />
        <input class="input-1 couponCode" type="text" value="" name="check_discount_code">
        <input class="button-81 couponCodeAdd" type="submit" value='<@message text="ověřit" editable=false />'>
    </p>
</div>

</#if>