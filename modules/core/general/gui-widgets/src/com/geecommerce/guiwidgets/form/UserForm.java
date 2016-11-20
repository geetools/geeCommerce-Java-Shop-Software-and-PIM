package com.geecommerce.guiwidgets.form;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.web.annotation.Field;
import com.geecommerce.core.web.annotation.Form;

@Form
public class UserForm {
    @Field(cutAt = 40)
    private String email = null;
    @Field(cutAt = 30)
    private String firstName = null;
    @Field(cutAt = 30)
    private String lastName = null;
    @Field(cutAt = 7)
    private String zip = null;
    @Field(cutAt = 30)
    private String address = null;
    @Field(cutAt = 30)
    private String city = null;

    private String gift = null;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("email", email);
        map.put("gift", gift);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("address", address);
        map.put("city", city);
        map.put("zip", zip);

        return map;
    }
}
