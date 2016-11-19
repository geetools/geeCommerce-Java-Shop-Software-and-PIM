package com.geecommerce.checkout.model;

import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface CheckoutAddress extends Model {
    public Id getId();

    public CheckoutAddress setId(Id id);

    public String getSalutation();

    public void setSalutation(String salutation);

    public String getFirstName();

    public CheckoutAddress setFirstName(String firstName);

    public String getLastName();

    public CheckoutAddress setLastName(String lastName);

    public String getZip();

    public CheckoutAddress setZip(String zip);

    public String getAddress1();

    public CheckoutAddress setAddress1(String address1);

    public String getAddress2();

    public CheckoutAddress setAddress2(String address2);

    public String getHouseNumber();

    public CheckoutAddress setHouseNumber(String houseNumber);

    public String getCity();

    public CheckoutAddress setCity(String city);

    public String getCountry();

    public CheckoutAddress setCountry(String country);

    public String getPhone();

    public CheckoutAddress setEmail(String email);

    public String getEmail();

    public CheckoutAddress setPhone(String phone);

    public void fromMap(Map<String, Object> map, String prefix);

    public Map<String, Object> toMap(String prefix);

    static final class Column {
        public static final String SALUTATION = "sal";
        public static final String FIRST_NAME = "fname";
        public static final String LAST_NAME = "lname";
        public static final String ZIP = "zip";
        public static final String ADDRESS1 = "addr1";
        public static final String ADDRESS2 = "addr2";
        public static final String HOUSE_NUMBER = "house_number";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String EMAIL = "email";
    }
}
