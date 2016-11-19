package com.geecommerce.checkout.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultCheckoutAddress extends AbstractModel implements CheckoutAddress {
    private static final long serialVersionUID = 4448714008482476475L;
    private Id id = null;
    private String salutation = null;
    private String firstName = null;
    private String lastName = null;
    private String zip = null;
    private String address1 = null;
    private String address2 = null;
    private String houseNumber = null;
    private String city = null;
    private String country = null;
    private String phone = null;
    private String email = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CheckoutAddress setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public CheckoutAddress setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public CheckoutAddress setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public CheckoutAddress setZip(String zip) {
        this.zip = zip;
        return this;
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    @Override
    public CheckoutAddress setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    @Override
    public CheckoutAddress setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public CheckoutAddress setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public CheckoutAddress setCity(String city) {
        this.city = city;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public CheckoutAddress setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public CheckoutAddress setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public CheckoutAddress setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map, String prefix) {
        super.fromMap(map);

        this.salutation = str_(map.get(prefix + Column.SALUTATION));
        this.firstName = str_(map.get(prefix + Column.FIRST_NAME));
        this.lastName = str_(map.get(prefix + Column.LAST_NAME));
        this.zip = str_(map.get(prefix + Column.ZIP));
        this.address1 = str_(map.get(prefix + Column.ADDRESS1));
        this.address2 = str_(map.get(prefix + Column.ADDRESS2));
        this.houseNumber = str_(map.get(prefix + Column.HOUSE_NUMBER));
        this.city = str_(map.get(prefix + Column.CITY));
        this.country = str_(map.get(prefix + Column.COUNTRY));
        this.phone = str_(map.get(prefix + Column.PHONE_NUMBER));
        this.email = str_(map.get(prefix + Column.EMAIL));
    }

    @Override
    public Map<String, Object> toMap(String prefix) {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(prefix + Column.SALUTATION, getSalutation());
        m.put(prefix + Column.FIRST_NAME, getFirstName());
        m.put(prefix + Column.LAST_NAME, getLastName());
        m.put(prefix + Column.ZIP, getZip());
        m.put(prefix + Column.ADDRESS1, getAddress1());
        m.put(prefix + Column.ADDRESS2, getAddress2());
        m.put(prefix + Column.HOUSE_NUMBER, getHouseNumber());
        m.put(prefix + Column.CITY, getCity());
        m.put(prefix + Column.COUNTRY, getCountry());
        m.put(prefix + Column.PHONE_NUMBER, getPhone());
        m.put(prefix + Column.EMAIL, getEmail());

        return m;
    }
}
