package com.geecommerce.checkout.form;

import com.geecommerce.core.web.annotation.Form;

@Form
public class AddressForm {

    // @Check(required = true, on = {"/process-address"}, maxLength = 30)
    private String salutation = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 30)
    private String firstName = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 30)
    private String lastName = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 7)
    private String zip = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 64)
    private String address1 = null;

    private String address2 = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 30)
    private String phone = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 30)
    private String city = null;

    // @Check(required = true, on = {"/process-address"})
    private String country = null;

    // @Check(required = true, on = {"/process-address"}, maxLength = 7)
    private String houseNumber = null;

    private String email = null;

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
