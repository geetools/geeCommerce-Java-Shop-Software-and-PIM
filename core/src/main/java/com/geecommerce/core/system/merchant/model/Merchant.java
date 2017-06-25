package com.geecommerce.core.system.merchant.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface Merchant extends Model {
    public Id getId();

    public String getCode();

    public String getCompanyName();

    public String getCompanyAddressLine1();

    public String getCompanyAddressLine2();

    public String getCompanyCity();

    public String getCompanyState();

    public String getCompanyZipCode();

    public String getCompanyCountry();

    public String getCompanyPhone();

    public String getCompanyFax();

    public String getCompanyWebsite();

    public List<Contact> getContacts();

    public List<Store> getStores();

    public Store getStore(Id storeId);

    public Store getStore(String storeCode);

    public Store getStoreFor(RequestContext requestCtx);

    public List<View> getViews();

    public View getView(Id viewId);

    public View getViewFor(RequestContext requestCtx);

    @JsonIgnore
    public String getBaseSystemPath();

    @JsonIgnore
    public String getLogPath();

    @JsonIgnore
    public String getConfigurationPath();

    @JsonIgnore
    public String getClassesPath();

    @JsonIgnore
    public URL[] getClasspath() throws MalformedURLException;

    @JsonIgnore
    public String getAbsoluteBaseSystemPath();

    @JsonIgnore
    public String getModulesPath();

    @JsonIgnore
    public String getTemplatesPath();

    @JsonIgnore
    public String getWebPath();

    @JsonIgnore
    public String getResourcesPath();

    @JsonIgnore
    public String getCertsPath();

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String COMPANY_NAME = "co_name";
        public static final String COMPANY_ADDRESS_LINE1 = "co_addr1";
        public static final String COMPANY_ADDRESS_LINE2 = "co_addr2";
        public static final String COMPANY_CITY = "co_city";
        public static final String COMPANY_STATE = "co_state";
        public static final String COMPANY_ZIPCODE = "co_zip";
        public static final String COMPANY_COUNTRY = "co_country";
        public static final String COMPANY_PHONE = "co_phone";
        public static final String COMPANY_FAX = "co_fax";
        public static final String COMPANY_WEBSITE = "co_web";
        public static final String BASE_SYSTEM_PATH = "sys_path";
        public static final String CONTACTS = "contacts";
        public static final String STORES = "stores";
        public static final String VIEWS = "views";
    }
}
