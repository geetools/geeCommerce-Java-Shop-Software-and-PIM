package com.geecommerce.customer.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface Address extends Model {
    public Address belongsTo(Customer customer);

    public Date getCreatedOn();

    public Date getModifiedOn();

    public Address setCountry(String country);

    public String getCountry();

    public Address setZip(String zip);

    public String getZip();

    public Address setDistrict(String district);

    public String getDistrict();

    public Address setState(String state);

    public String getState();

    public Address setCity(String city);

    public String getCity();

    public Address setAddressLines(List<String> addressLines);

    public List<String> getAddressLines();

    public Address setAddressLines(String... lines);

    public Address addAddressLine(String addressLine);

    public Address setHouseNumber(String houseNumber);

    public String getHouseNumber();

    public Address setFax(String fax);

    public String getFax();

    public Address setTelephone(String telephone);

    public String getTelephone();

    public Address setMobile(String mobile);

    public String getMobile();

    public Address setCompany(String company);

    public String getCompany();

    public Address setSurname(String surname);

    public String getSurname();

    public Address setForename(String forename);

    public String getForename();

    public Address setSalutation(String salutation);

    public String getSalutation();

    public Id getCustomerId();

    public Address setId(Id id);

    public Id getId();

    public boolean isDefaultDeliveryAddress();

    public Address markAsDefaultDeliveryAddress();

    public Address unmarkAsDefaultDeliveryAddress();

    public boolean isDefaultInvoiceAddress();

    public Address markAsDefaultInvoiceAddress();

    public Address unmarkAsDefaultInvoiceAddress();

    static final class Column {
        public static final String ID = "_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String SALUTATION = "salut";
        public static final String FORENAME = "forename";
        public static final String SURNAME = "surname";
        public static final String COMPANY = "company";
        public static final String TELEPHONE = "tel";
        public static final String MOBILE = "mobile";
        public static final String FAX = "fax";
        public static final String ADDRESS_LINES = "addr_lines";
        public static final String HOUSE_NUMBER = "house_number";
        public static final String CITY = "city";
        public static final String DISTRICT = "district";
        public static final String STATE = "state";
        public static final String ZIP = "zip";
        public static final String COUNTRY = "country";
        public static final String IS_DEFAULT_DELIVERY_ADDRESS = "def_del_addr";
        public static final String IS_DEFAULT_INVOICE_ADDRESS = "def_inv_addr";
        public static final String CREATED_ON = "cr_on";
        public static final String MODIFIED_ON = "mod_on";
    }
}
