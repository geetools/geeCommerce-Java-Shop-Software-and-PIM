<div class="row header-row">
    <div class="col-sm-12 col-md-6"><h3><@message text="Adresse" /></h3></div>
    <div class="col-sm-12 col-md-6"><a class="btn" href="/checkout/demo/address"><@message text="Bearbeiten" /></a>
    </div>
</div>
<div class="row">
    <div class="col-xs-12 gray">
    <@message text="Rechnungs- und Lieferadresse" />
    </div>
</div>
<div class="row">
    <div class="col-xs-12">
        <p>
        <#if previewCheckout.salutation??>
            <strong><@message text="${previewCheckout.salutation}"/></strong><br/>
        </#if>
        <#if previewCheckout.firstName?? || previewCheckout.lastName??>
            <strong>${previewCheckout.firstName} ${previewCheckout.lastName}</strong><br/>
        </#if>

        <#assign invoiceAddress = previewCheckout.invoiceAddress />
        <#if invoiceAddress.address2?has_content>${invoiceAddress.address2}<br/></#if>
        ${invoiceAddress.address1} ${invoiceAddress.houseNumber}<br/>
        ${invoiceAddress.zip} ${invoiceAddress.city}<br/>
        ${countries[invoiceAddress.country]}
        </p>

    <#if previewCheckout.deliveryAddress??>
        <span class="gray">
            <@message text="Lieferadresse" />
            </span>
        <p>
            <#assign deliveryAddress = previewCheckout.deliveryAddress />

            <strong><@message text="${deliveryAddress.salutation}"/> ${deliveryAddress.firstName} ${deliveryAddress.lastName}</strong><br/>
            <#if deliveryAddress.address2?has_content>${deliveryAddress.address2}<br/></#if>
        ${deliveryAddress.address1} ${deliveryAddress.houseNumber}<br/>
        ${deliveryAddress.zip} ${deliveryAddress.city}<br/>
        ${countries[deliveryAddress.country]}
        </p>
    </#if>

    </div>
</div>


