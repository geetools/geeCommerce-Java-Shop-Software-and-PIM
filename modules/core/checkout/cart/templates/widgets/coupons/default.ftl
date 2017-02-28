<#if wCart??>
    <#assign wCouponCode = wCart.couponCode>
    <#assign hasAutocoupons = wAutoCoupons?? && wAutoCoupons?size gt 0>

    <!--Coupon code is in card and it's not an autocoupon-->
    <#if (wCouponCode?? && wCouponCode.coupon?? && !wCouponCode.coupon.auto) >
        <div class="discount-box">
            <h2><@message text="Discount" lang="en" text="Rabbat" lang="de"/></h2>

            <h3><@print src=wCouponCode value="wCouponCode.coupon.name.str" format="short-text" /></h3>

            <strong><@message text="Code" lang="en" text="Code" lang="de"/> ${wCouponCode.code}</strong>

            <a href="/coupon/remove" class="couponCodeRemove" ><i class="glyphicon glyphicon-remove" aria-hidden="true"></i></a>

            <div id="discount-description">
                <#if wCouponCode.coupon.description??>
                    <@print src=wCouponCode value="wCouponCode.coupon.description.str" />
                </#if>
            </div>
        </div>
    </#if>

    <#if (wCouponCode?? && wCouponCode.coupon?? && wCouponCode.coupon.auto) >
        <div class="discount-box">
            <h2><@message text="Discount" lang="en" text="Rabbat" lang="de"/></h2>

            <#if wAutoCoupons?size gt 1>
                <form name="switchAutoCouponForm" action="/coupon/set-autocoupon" method="get">

                    <select id="selectedAutoCoupon" name="couponId" onchange="this.form.submit()">
                        <#list wAutoCoupons as autoCoupon>
                            ${autoCoupon.id?string}
                            <#if (autoCoupon.id?string == wCouponCode.id?string)>
                                <option value="${autoCoupon.id?string}" selected="selected"> <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" /></option>
                            <#else>
                                <option value="${autoCoupon.id?string}" > <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" /></option>
                            </#if>
                        </#list>
                    </select>
                </form>
            </#if>

            <h3><@print src=wCouponCode value="wCouponCode.coupon.name.str" format="short-text" /></h3>


            <form name="discount_set_state" action="/coupon/switch-autocoupon" method="get">
                <label><input type="hidden" name="h_discount_enabled" value="true">
                    <#assign checked = "checked">
                    <#if !wCart.useAutoCoupon>
                        <#assign checked = "">
                    </#if>

                    <input type="checkbox" class="checkbox discount-checkbox" onclick="document.forms.discount_set_state.submit();" ${checked} name="useAutoCoupon" >
                    <@message text="Use action" lang="en" text2="Use action" lang2="de"/>
                </label>

            </form>

            <div id="discount-description">
                <#if wCouponCode.coupon.description??>
                        <@print src=wCouponCode value="wCouponCode.coupon.description.str" />
                    </#if>
            </div>
        </div>
    </#if>


    <#if !wCouponCode?? && hasAutocoupons>

        <h2><@message text="Discount" lang="en" text="Rabbat" lang="de"/></h2>
        <form name="switchAutoCouponForm" action="/coupon/set-autocoupon" method="get">

            <select id="selectedAutoCoupon" name="couponId" onchange="this.form.submit()">
                <option value="" > </option>
                <#list wAutoCoupons as autoCoupon>
                        <option value="${autoCoupon.id?string}" > <@print src=autoCoupon value="autoCoupon.coupon.name.str" format="short-text" /></option>
                </#list>
            </select>
        </form>
    </#if>

<!--Coupon code not entered and no autocoupons-->
    <#if !wCouponCode?? ||  hasAutocoupons>
        <div class="discount-form">
            <#if !wCouponCode?? && !hasAutocoupons>
                <h2><@message text="Coupon code" lang="en" text2="Rabattcode" lang2="de" /></h2>
            </#if>
            <p>
                <@message text="Please enter your coupon code" lang="en" text2="Bitte geben Sie Ihren Rabattcode" lang2="de" />
                <form action="/coupon/add" method="get">
                    <input class="cart-coupon-code" type="text" value="" name="couponCode">
                    <input class="cart-coupon-add" type="submit" value='<@message text="Add coupon" lang="en" text="Add coupon" lang="de" editable=false />' />
                </form>
            </p>
        </div>
    </#if>

</#if>




<#--

<#assign couponCode = actionBean.cart.couponCode!>
<#assign autoCoupon = actionBean.autoCoupon!>
<#assign showAutoCoupon = true>





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

<#if actionBean.autoCoupon?? && showAutoCoupon && actionBean.autoCoupons?size gt 1  >
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

</div>



-->












<#--
<#assign autoCoupon = actionBean.autoCoupon!>
<#assign showAutoCoupon = true>-->

<#--
<!-- Show input field for coupons if not coupon is active&ndash;&gt;

<#if !couponCode??>
    <div class="cart-coupon-form">
        <h2><@message text="Coupon code" lang="en" text2="Rabattcode" lang2="de" /></h2>
        <p>
            <@message text="Please enter your coupon code" lang="en" text2="Bitte geben Sie Ihren Rabattcode" lang2="de" />
            <form c>
                <input class="cart-coupon-code" type="text" value="" name="check_discount_code">
                <input class="cart-coupon-add" type="submit" value='<@message text="ověřit" editable=false />' />
            </form>
        </p>
    </div>
</#if>



<!-- Coupon code is activated and it's not an auto one &ndash;&gt;
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

</#if>-->
