package com.geecommerce.customer.form;

import com.geemvc.validation.annotation.Check;


public class AddressForm {

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String salutation = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String forename = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String surname = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String zip = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String street = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String city = null;

    private String state = null;

    private String country = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String houseNumber = null;

    private String district = null;

    @Check(required = true, on = {"/add", "/edit-confirm/{id}"})
    private String phone = null;

    private String mobile = null;

    private String fax = null;

    private String company = null;

    private Boolean defaultInvoiceAddress;

    private Boolean defaultDeliveryAddress;

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Boolean getDefaultInvoiceAddress() {
        return defaultInvoiceAddress;
    }

    public void setDefaultInvoiceAddress(Boolean defaultInvoiceAddress) {
        this.defaultInvoiceAddress = defaultInvoiceAddress;
    }

    public Boolean getDefaultDeliveryAddress() {
        return defaultDeliveryAddress;
    }

    public void setDefaultDeliveryAddress(Boolean defaultDeliveryAddress) {
        this.defaultDeliveryAddress = defaultDeliveryAddress;
    }
}
