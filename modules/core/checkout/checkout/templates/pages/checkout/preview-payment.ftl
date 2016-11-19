<div class="row header-row">
    <div class="col-sm-12 col-md-6"><h3><@message text="Zahlart" /></h3></div>
    <div class="col-sm-12 col-md-6"><a class="btn"
                                       href="/checkout/demo/payment"><@message text="Bearbeiten"/></a>
    </div>
</div>
<div class="row">
    <div class="col-xs-12">
        <p>
        ${previewCheckout.paymentMethodName}
        </p>

        <p>
        ${cartTotals.gross_grand_total?string.currency}
        </p>
    </div>
</div>



