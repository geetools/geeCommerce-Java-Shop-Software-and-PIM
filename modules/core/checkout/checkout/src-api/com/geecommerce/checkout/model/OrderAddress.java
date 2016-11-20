package com.geecommerce.checkout.model;

import com.geecommerce.checkout.enums.AddressType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface OrderAddress extends Model {
    public Id getId();

    public OrderAddress setId(Id id);

    public Id getOrderId();

    public OrderAddress setOrderId(Id orderId);

    public String getFirstName();

    public OrderAddress setFirstName(String firstName);

    public String getLastName();

    public OrderAddress setLastName(String lastName);

    public String getZip();

    public OrderAddress setZip(String zip);

    public String getAddress1();

    public OrderAddress setAddress1(String address1);

    public String getAddress2();

    public OrderAddress setAddress2(String address2);

    public OrderAddress setHouseNumber(String houseNumber);

    public String getHouseNumber();

    public OrderAddress setDistrict(String district);

    public String getDistrict();

    public String getCity();

    public OrderAddress setCity(String city);

    public String getCountry();

    public OrderAddress setCountry(String country);

    public AddressType getAddressType();

    public OrderAddress setAddressType(AddressType addressType);

    public OrderAddress belongsTo(Order order);

    public String getSalutation();

    public OrderAddress setSalutation(String salutation);

    public String getPhone();

    public OrderAddress setPhone(String phone);

    public String getMobile();

    public OrderAddress setMobile(String mobile);

    public String getFax();

    public OrderAddress setFax(String fax);

    public String getEmail();

    public OrderAddress setEmail(String email);

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String ZIP = "zip";
        public static final String HOUSE_NUMBER = "house_number";
        public static final String ADDRESS1 = "address1";
        public static final String ADDRESS2 = "address2";
        public static final String DISTRICT = "district";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String TYPE = "type";
        public static final String PHONE = "phone";
        public static final String MOBILE = "mobile";
        public static final String FAX = "fax";
        public static final String SALUTATION = "salutation";
        public static final String EMAIL = "email";
    }
}
