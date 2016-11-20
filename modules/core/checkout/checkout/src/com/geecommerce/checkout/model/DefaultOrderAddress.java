package com.geecommerce.checkout.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.checkout.enums.AddressType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("sale_order_address")
@XmlRootElement(name = "address")
public class DefaultOrderAddress extends AbstractModel implements OrderAddress {
    private static final long serialVersionUID = 3253751420629087609L;
    private Id id = null;
    private Id orderId = null;
    private String firstName = null;
    private String lastName = null;
    private String houseNumber = null;
    private String zip = null;
    private String address1 = null;
    private String address2 = null;
    private String district = null;
    private String city = null;
    private String country = null;
    private AddressType type = null;
    private String salutation = null;
    private String phone = null;
    private String mobile = null;
    private String fax = null;
    private String email = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public OrderAddress setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getOrderId() {
        return orderId;
    }

    @Override
    public OrderAddress setOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public OrderAddress setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public OrderAddress setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public OrderAddress setZip(String zip) {
        this.zip = zip;
        return this;
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    @Override
    public OrderAddress setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    @Override
    public OrderAddress setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    @Override
    public OrderAddress setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public OrderAddress setDistrict(String district) {
        this.district = district;
        return this;
    }

    @Override
    public String getDistrict() {
        return district;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public OrderAddress setCity(String city) {
        this.city = city;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public OrderAddress setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public AddressType getAddressType() {
        return type;
    }

    @Override
    public OrderAddress setAddressType(AddressType addressType) {
        this.type = addressType;
        return this;
    }

    @Override
    public OrderAddress belongsTo(Order order) {
        this.orderId = order.getId();
        return this;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public OrderAddress setSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public OrderAddress setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public OrderAddress setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String getFax() {
        return fax;
    }

    @Override
    public OrderAddress setFax(String fax) {
        this.fax = fax;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public OrderAddress setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.orderId = id_(map.get(Column.ORDER_ID));
        this.firstName = str_(map.get(Column.FIRST_NAME));
        this.lastName = str_(map.get(Column.LAST_NAME));
        this.zip = str_(map.get(Column.ZIP));
        this.houseNumber = str_(map.get(Column.HOUSE_NUMBER));
        this.address1 = str_(map.get(Column.ADDRESS1));
        this.address2 = str_(map.get(Column.ADDRESS2));
        this.city = str_(map.get(Column.CITY));
        this.district = str_(map.get(Column.DISTRICT));
        this.country = str_(map.get(Column.COUNTRY));
        this.type = AddressType.fromId(int_(map.get(Column.TYPE)));

        this.salutation = str_(map.get(Column.SALUTATION));
        this.phone = str_(map.get(Column.PHONE));
        this.mobile = str_(map.get(Column.MOBILE));
        this.fax = str_(map.get(Column.FAX));
        this.email = str_(map.get(Column.EMAIL));

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.ORDER_ID, getOrderId());
        m.put(Column.FIRST_NAME, getFirstName());
        m.put(Column.LAST_NAME, getLastName());
        m.put(Column.ZIP, getZip());
        m.put(Column.HOUSE_NUMBER, getHouseNumber());
        m.put(Column.ADDRESS1, getAddress1());
        m.put(Column.ADDRESS2, getAddress2());
        m.put(Column.CITY, getCity());
        m.put(Column.DISTRICT, getDistrict());
        m.put(Column.COUNTRY, getCountry());
        m.put(Column.TYPE, getAddressType() == null ? null : getAddressType().toId());

        m.put(Column.SALUTATION, getSalutation());
        m.put(Column.PHONE, getPhone());
        m.put(Column.MOBILE, getMobile());
        m.put(Column.FAX, getFax());
        m.put(Column.EMAIL, getEmail());
        return m;
    }

    @Override
    public String toString() {
        return "DefaultOrderAddress [id=" + id + ", orderId=" + orderId + ", firstName=" + firstName + ", lastName="
            + lastName + ", zip=" + zip + ", address1=" + address1 + ", address2=" + address2 + ", city=" + city
            + ", country=" + country + ", type=" + type + "]";
    }
}
