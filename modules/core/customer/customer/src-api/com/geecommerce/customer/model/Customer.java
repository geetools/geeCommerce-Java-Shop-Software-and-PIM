package com.geecommerce.customer.model;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.interceptor.annotation.Interceptable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface Customer extends Model {
    public Id getId();

    public Customer setId(Id id);

    public String getId2();

    public Customer setId2(String id2);

    public String getCustomerNumber();

    public Customer setCustomerNumber(String customerNumber);

    public List<Id> getCustomerGroupIds();

    public Customer addCustomerGroup(CustomerGroup customerGroup);

    public boolean isInCustomerGroup(CustomerGroup customerGroup);

    public Set<Id> getMerchantIds();

    public Customer addTo(Merchant merchant);

    public boolean isIn(Merchant merchant);

    public Set<Id> getStoreIds();

    public Customer addTo(Store store);

    public boolean isIn(Store store);

    public Set<Id> getRequestContextIds();

    public Customer addTo(RequestContext requestContext);

    public boolean isIn(RequestContext requestContext);

    public String getSalutation();

    public Customer setSalutation(String salutation);

    public String getDegree();

    public Customer setDegree(String degree);

    public String getForename();

    @Interceptable
    public Customer setForename(String forename);

    public String getSurname();

    public Customer setSurname(String surname);

    public String getEmail();

    public Customer setEmail(String email);

    public String getPhone();

    public Customer setPhone(String phoneCode);

    public String getPhoneCode();

    public Customer setPhoneCode(String phoneCode);

    public String getMobile();

    public Customer setMobile(String mobile);

    public String getFax();

    public Customer setFax(String fax);

    public String getLanguage();

    public Customer setLanguage(String language);

    public Boolean isNewsletterEnabled();

    public Customer setNewsletterEnabled(Boolean newsletterEnabled);

    public String getCompany();

    public Customer setCompany(String company);

    public String getCompanyTaxIdNumber();

    public Customer setCompanyTaxIdNumber(String companyTaxIdNumber);

    static final class Col {
	public static final String ID = "_id";
	public static final String ID2 = "id2";
	public static final String CUSTOMER_NUMBER = "cust_no";
	public static final String CUSTOMER_GROUP_IDS = "cust_grp_ids";
	public static final String SALUTATION = "sal";
	public static final String DEGREE = "degree";
	public static final String FORENAME = "forename";
	public static final String SURNAME = "surname";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String PHONE_CODE = "phone_code";
	public static final String MOBILE = "mobile";
	public static final String FAX = "fax";
	public static final String LANGUAGE = "lang";
	public static final String NEWSLETTER_ENABLED = "newsletter_enabled";
	public static final String COMPANY = "company";
	public static final String COMPANY_TAX_IDENTIFICATION_NUMBER = "company_tax_ids_number";
    }
}
