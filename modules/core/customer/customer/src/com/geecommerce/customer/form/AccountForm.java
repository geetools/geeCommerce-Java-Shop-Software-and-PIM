package com.geecommerce.customer.form;

import com.geemvc.validation.annotation.Check;
import com.geemvc.validation.annotation.On;
import com.geemvc.validation.annotation.Required;

public class AccountForm {
    /* Form fields for creating account */

    @Required
    @On({ "/customer/account/add" })
    private String forename = null;

    @Required
    @On({ "/customer/account/add" })
    private String surname = null;

    @Required
    @On({ "/customer/account/add" })
    private String email = null;

    @Check(required = true, minLength = 8, on = { "/customer/account/add", "/forgot-password-save" })
    private String password1 = null;

    @Check(required = true, is = "js: accountForm.password1 == accountForm.password2", on = { "/customer/account/add",
        "/forgot-password-save" })
    private String password2 = null;

    private String username = null;

    @Required
    @On({ "/process-login" })
    private String password = null;

    private String title = null;

    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String phoneCode = null;
    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String phone = null;

    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String salutation = null;

    private String invoiceAddrFirm = null;
    private String invoiceAddrUst = null;

    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String invoiceAddrStreet = null;
    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String invoiceAddrHouseNum = null;
    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String invoiceAddrZipCode = null;
    @Check(required = true, on = { "/customer/account/add", "/customer/account/process-edit" })
    private String invoiceAddrCity;

    private String shippingAddrStreet = null;
    private String shippingAddrHouseNum = null;
    private String shippingAddrZipCode = null;
    private String shippingAddrCity;

    private String customerNumber = null;
    private boolean emailNotification;

    private String fpToken = null;

    private boolean fpTokenIsValid = false;

    private String postLoginRedirect = null;

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getInvoiceAddrFirm() {
        return invoiceAddrFirm;
    }

    public void setInvoiceAddrFirm(String invoiceAddrFirm) {
        this.invoiceAddrFirm = invoiceAddrFirm;
    }

    public String getInvoiceAddrUst() {
        return invoiceAddrUst;
    }

    public void setInvoiceAddrUst(String invoiceAddrUst) {
        this.invoiceAddrUst = invoiceAddrUst;
    }

    public String getInvoiceAddrStreet() {
        return invoiceAddrStreet;
    }

    public void setInvoiceAddrStreet(String invoiceAddrStreet) {
        this.invoiceAddrStreet = invoiceAddrStreet;
    }

    public String getInvoiceAddrHouseNum() {
        return invoiceAddrHouseNum;
    }

    public void setInvoiceAddrHouseNum(String invoiceAddrHouseNum) {
        this.invoiceAddrHouseNum = invoiceAddrHouseNum;
    }

    public String getInvoiceAddrZipCode() {
        return invoiceAddrZipCode;
    }

    public void setInvoiceAddrZipCode(String invoiceAddrZipCode) {
        this.invoiceAddrZipCode = invoiceAddrZipCode;
    }

    public String getInvoiceAddrCity() {
        return invoiceAddrCity;
    }

    public void setInvoiceAddrCity(String invoiceAddrCity) {
        this.invoiceAddrCity = invoiceAddrCity;
    }

    public String getShippingAddrStreet() {
        return shippingAddrStreet;
    }

    public void setShippingAddrStreet(String shippingAddrStreet) {
        this.shippingAddrStreet = shippingAddrStreet;
    }

    public String getShippingAddrHouseNum() {
        return shippingAddrHouseNum;
    }

    public void setShippingAddrHouseNum(String shippingAddrHouseNum) {
        this.shippingAddrHouseNum = shippingAddrHouseNum;
    }

    public String getShippingAddrZipCode() {
        return shippingAddrZipCode;
    }

    public void setShippingAddrZipCode(String shippingAddrZipCode) {
        this.shippingAddrZipCode = shippingAddrZipCode;
    }

    public String getShippingAddrCity() {
        return shippingAddrCity;
    }

    public void setShippingAddrCity(String shippingAddrCity) {
        this.shippingAddrCity = shippingAddrCity;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public String getFpToken() {
        return fpToken;
    }

    public void setFpToken(String fpToken) {
        this.fpToken = fpToken;
    }

    public boolean isFpTokenIsValid() {
        return fpTokenIsValid;
    }

    public void setFpTokenIsValid(boolean fpTokenIsValid) {
        this.fpTokenIsValid = fpTokenIsValid;
    }

    public String getPostLoginRedirect() {
        return postLoginRedirect;
    }

    public void setPostLoginRedirect(String postLoginRedirect) {
        this.postLoginRedirect = postLoginRedirect;
    }

}
