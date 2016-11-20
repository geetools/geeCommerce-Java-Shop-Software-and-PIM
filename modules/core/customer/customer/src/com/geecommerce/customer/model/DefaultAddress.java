package com.geecommerce.customer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Lists;

@Model("customer_addresses")
public class DefaultAddress extends AbstractModel implements Address {
    private static final long serialVersionUID = 3053074002933339758L;
    private Id id = null;
    private Id customerId = null;
    private String salutation = null;
    private String forename = null;
    private String surname = null;
    private String company = null;
    private String telephone = null;
    private String mobile = null;
    private String fax = null;
    private List<String> addressLines = new ArrayList<>();
    private String houseNumber = null;
    private String city = null;
    private String district = null;
    private String state = null;
    private String zip = null;
    private String country = null;
    private boolean isDefaultDeliveryAddress = false;
    private boolean isDefaultInvoiceAddress = false;
    private Date createdOn = null;
    private Date modifiedOn = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Address setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Address belongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            throw new NullPointerException("The customerId cannot be null");

        this.customerId = customer.getId();
        return this;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public Address setSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    @Override
    public String getForename() {
        return forename;
    }

    @Override
    public Address setForename(String forename) {
        this.forename = forename;
        return this;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public Address setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    @Override
    public String getCompany() {
        return company;
    }

    @Override
    public Address setCompany(String company) {
        this.company = company;
        return this;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    @Override
    public Address setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public Address setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    @Override
    public String getFax() {
        return fax;
    }

    @Override
    public Address setFax(String fax) {
        this.fax = fax;
        return this;
    }

    @Override
    public List<String> getAddressLines() {
        return addressLines;
    }

    @Override
    public Address setAddressLines(List<String> addressLines) {
        this.addressLines = addressLines;
        return this;
    }

    @Override
    public Address setAddressLines(String... lines) {
        this.addressLines = Lists.newArrayList(lines);
        return this;
    }

    @Override
    public Address addAddressLine(String addressLine) {
        this.addressLines.add(addressLine);
        return this;
    }

    @Override
    public Address setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    @Override
    public String getDistrict() {
        return district;
    }

    @Override
    public Address setDistrict(String district) {
        this.district = district;
        return this;
    }

    @Override
    public Address setState(String state) {
        this.state = state;
        return this;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public Address setZip(String zip) {
        this.zip = zip;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public Address setCountry(String country) {
        this.country = country;
        return this;
    }

    public boolean isDefaultDeliveryAddress() {
        return isDefaultDeliveryAddress;
    }

    public Address markAsDefaultDeliveryAddress() {
        this.isDefaultDeliveryAddress = true;
        return this;
    }

    public Address unmarkAsDefaultDeliveryAddress() {
        this.isDefaultDeliveryAddress = false;
        return this;
    }

    public boolean isDefaultInvoiceAddress() {
        return isDefaultInvoiceAddress;
    }

    public Address markAsDefaultInvoiceAddress() {
        this.isDefaultInvoiceAddress = true;
        return this;
    }

    public Address unmarkAsDefaultInvoiceAddress() {
        this.isDefaultInvoiceAddress = false;
        return this;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.salutation = str_(map.get(Column.SALUTATION));
        this.forename = str_(map.get(Column.FORENAME));
        this.surname = str_(map.get(Column.SURNAME));
        this.company = str_(map.get(Column.COMPANY));
        this.telephone = str_(map.get(Column.TELEPHONE));
        this.mobile = str_(map.get(Column.MOBILE));
        this.fax = str_(map.get(Column.FAX));
        this.city = str_(map.get(Column.CITY));
        this.district = str_(map.get(Column.DISTRICT));
        this.state = str_(map.get(Column.STATE));
        this.zip = str_(map.get(Column.ZIP));
        this.country = str_(map.get(Column.COUNTRY));
        this.isDefaultDeliveryAddress = bool_(map.get(Column.IS_DEFAULT_DELIVERY_ADDRESS)) == null ? false
            : bool_(map.get(Column.IS_DEFAULT_DELIVERY_ADDRESS));
        this.isDefaultInvoiceAddress = bool_(map.get(Column.IS_DEFAULT_INVOICE_ADDRESS)) == null ? false
            : bool_(map.get(Column.IS_DEFAULT_INVOICE_ADDRESS));
        this.createdOn = date_(map.get(Column.CREATED_ON));
        this.modifiedOn = date_(map.get(Column.MODIFIED_ON));

        this.addressLines = list_(map.get(Column.ADDRESS_LINES));
        this.houseNumber = str_(map.get(Column.HOUSE_NUMBER));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.ID, getId());
        m.put(Column.CUSTOMER_ID, getCustomerId());
        m.put(Column.SALUTATION, getSalutation());
        m.put(Column.FORENAME, getForename());
        m.put(Column.SURNAME, getSurname());
        m.put(Column.COMPANY, getCompany());
        m.put(Column.TELEPHONE, getTelephone());
        m.put(Column.MOBILE, getMobile());
        m.put(Column.FAX, getFax());
        m.put(Column.ADDRESS_LINES, getAddressLines());
        m.put(Column.HOUSE_NUMBER, getHouseNumber());
        m.put(Column.CITY, getCity());
        m.put(Column.DISTRICT, getDistrict());
        m.put(Column.STATE, getState());
        m.put(Column.ZIP, getZip());
        m.put(Column.COUNTRY, getCountry());
        m.put(Column.IS_DEFAULT_DELIVERY_ADDRESS, isDefaultDeliveryAddress());
        m.put(Column.IS_DEFAULT_INVOICE_ADDRESS, isDefaultInvoiceAddress());
        m.put(Column.CREATED_ON, getCreatedOn());
        m.put(Column.MODIFIED_ON, getModifiedOn());

        return m;
    }

    @Override
    public String toString() {
        return "DefaultAddress [id=" + id + ", customerId=" + customerId + ", salutation=" + salutation + ", forename="
            + forename + ", surname=" + surname + ", company=" + company + ", telephone=" + telephone + ", mobile="
            + mobile + ", fax=" + fax + ", addressLines=" + addressLines + ", houseNumber=" + houseNumber
            + ", city=" + city + ", district=" + district + ", zip=" + zip + ", country=" + country
            + ", isDefaultDeliveryAddress=" + isDefaultDeliveryAddress + ", isDefaultInvoiceAddress="
            + isDefaultInvoiceAddress + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + "]";
    }
}
