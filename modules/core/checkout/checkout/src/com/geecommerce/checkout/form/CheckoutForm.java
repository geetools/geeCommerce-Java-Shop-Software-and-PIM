package com.geecommerce.checkout.form;

import com.geecommerce.core.web.annotation.Field;
import com.geecommerce.core.web.annotation.Form;
import com.geemvc.validation.annotation.Check;

import javax.validation.Valid;

@Form
public class CheckoutForm {

    private AddressForm delivery = null;

    @Check(required = true, param = {"form.invoice.firstName"}, maxLength = 30)
    @Check(required = true, param = {"form.invoice.lastName"}, maxLength = 30)
    @Check(required = true, param = {"form.invoice.address1"}, maxLength = 64)
    @Check(required = true, param = {"form.invoice.houseNumber"}, maxLength = 30)
    @Check(required = true, param = {"form.invoice.zip"}, maxLength = 7)
    @Check(required = true, param = {"form.invoice.houseNumber"}, maxLength = 7)
    @Check(required = true, param = {"form.invoice.phone"}, maxLength = 30)
    @Check(required = true, param = {"form.invoice.city"}, maxLength = 30)
    private AddressForm invoice = null;

    @Check(required = true, param = {"form.email"}, maxLength = 64)
    private String email = null;

    private String paymentMethodCode = null;
    private String carrierCode = null;
    // private String additional
    private Boolean isCustomInvoice = null;
    private Boolean isCustomDelivery = null;

    @Field(cutAt = 3000)
    private String note = null;
    private String store = null;

    @Check(required = true, param = {"form.agreeToTerms"})
    private boolean agreeToTerms = false;

    public Boolean getCustomInvoice() {
        if (isCustomInvoice == null)
            return false;
        return isCustomInvoice;
    }

    public void setCustomInvoice(Boolean customInvoice) {
        isCustomInvoice = customInvoice;
    }

    public Boolean getCustomDelivery() {
        if (isCustomDelivery == null)
            return false;
        return isCustomDelivery;
    }

    public void setCustomDelivery(Boolean customDelivery) {
        isCustomDelivery = customDelivery;
    }

    public AddressForm getDelivery() {
        if (delivery == null) {
            delivery = new AddressForm();
        }
        return delivery;
    }

    public void setDelivery(AddressForm delivery) {
        this.delivery = delivery;
    }

    public AddressForm getInvoice() {
        if (invoice == null) {
            invoice = new AddressForm();
        }
        return invoice;
    }

    public void setInvoice(AddressForm invoice) {
        this.invoice = invoice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public boolean isAgreeToTerms() {
        return agreeToTerms;
    }

    public void setAgreeToTerms(boolean agreeToTerms) {
        this.agreeToTerms = agreeToTerms;
    }

    @Override
    public String toString() {
        return "CheckoutForm{" + "delivery=" + delivery + ", invoice=" + invoice + ", email='" + email + '\'' + ", paymentMethodCode='" + paymentMethodCode + '\'' + ", carrierCode='" + carrierCode + '\'' + ", isCustomInvoice=" + isCustomInvoice
                + '}';
    }
}
