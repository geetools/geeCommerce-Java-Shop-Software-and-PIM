package com.geecommerce.customer.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.interceptor.annotation.Interceptable;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

@Model("customers")
@XmlRootElement(name = "customer")
public class DefaultCustomer extends AbstractModel implements Customer {
    private static final long serialVersionUID = -6407783328833601202L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private String id2 = null;

    @Column(Col.CUSTOMER_NUMBER)
    private String customerNumber = null;

    @Column(GlobalColumn.MERCHANT_ID)
    protected Set<Id> merchantIds = null;

    @Column(GlobalColumn.STORE_ID)
    protected Set<Id> storeIds = null;

    @Column(GlobalColumn.REQUEST_CONTEXT_ID)
    private Set<Id> requestContextIds = null;

    @Column(Col.CUSTOMER_GROUP_IDS)
    private List<Id> customerGroupIds = null;

    @Column(Col.SALUTATION)
    private String salutation;

    @Column(Col.DEGREE)
    private String degree;

    @Column(Col.FORENAME)
    private String forename;

    @Column(Col.SURNAME)
    private String surname;

    @Column(Col.EMAIL)
    private String email;

    @Column(Col.PHONE)
    private String phone;

    @Column(Col.PHONE_CODE)
    private String phoneCode;

    @Column(Col.MOBILE)
    private String mobile;

    @Column(Col.FAX)
    private String fax;

    @Column(Col.LANGUAGE)
    private String language;

    @Column(Col.NEWSLETTER_ENABLED)
    private Boolean newsletterEnabled;

    @Column(Col.COMPANY)
    private String company;

    @Column(Col.COMPANY_TAX_IDENTIFICATION_NUMBER)
    private String companyTaxIdNumber;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Customer setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId2() {
        return id2;
    }

    @Override
    public Customer setId2(String id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public String getCustomerNumber() {
        return customerNumber;
    }

    @Override
    public Customer setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
        return this;
    }

    @Override
    public List<Id> getCustomerGroupIds() {
        return customerGroupIds;
    }

    @Override
    public Customer addCustomerGroup(CustomerGroup customerGroup) {
        if (customerGroup == null || customerGroup.getId() == null)
            throw new IllegalStateException("CustomerGroup cannot be null");

        if (customerGroupIds == null)
            customerGroupIds = new ArrayList<>();

        if (!customerGroupIds.contains(customerGroup.getId()))
            customerGroupIds.add(customerGroup.getId());

        return this;
    }

    @Override
    public boolean isInCustomerGroup(CustomerGroup customerGroup) {
        if (customerGroupIds == null)
            return false;

        return customerGroupIds.contains(customerGroup.getId());
    }

    @Override
    public Set<Id> getMerchantIds() {
        return merchantIds;
    }

    @Override
    public Customer addTo(Merchant merchant) {
        if (merchant == null || merchant.getId() == null)
            throw new IllegalStateException("Merchant cannot be null");

        if (merchantIds == null)
            merchantIds = new HashSet<>();

        merchantIds.add(merchant.getId());
        return this;
    }

    @Override
    public boolean isIn(Merchant merchant) {
        if (merchant == null || merchant.getId() == null)
            return false;

        if (merchantIds == null || merchantIds.isEmpty())
            return false;

        return merchantIds.contains(merchant.getId());
    }

    @Override
    public Set<Id> getStoreIds() {
        return storeIds;
    }

    @Override
    public Customer addTo(Store store) {
        if (store == null || store.getId() == null)
            throw new IllegalStateException("Store cannot be null");

        if (storeIds == null)
            storeIds = new HashSet<>();

        storeIds.add(store.getId());
        return this;
    }

    @Override
    public boolean isIn(Store store) {
        if (store == null || store.getId() == null)
            return false;

        if (storeIds == null || storeIds.isEmpty())
            return false;

        return storeIds.contains(store.getId());
    }

    @Override
    public Set<Id> getRequestContextIds() {
        return requestContextIds;
    }

    @Override
    public Customer addTo(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            throw new IllegalStateException("RequestContext cannot be null");

        if (requestContextIds == null)
            requestContextIds = new HashSet<>();

        requestContextIds.add(requestContext.getId());
        return this;
    }

    @Override
    public boolean isIn(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            return false;

        if (requestContextIds == null || requestContextIds.isEmpty())
            return false;

        return requestContextIds.contains(requestContext.getId());
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public Customer setSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    @Override
    public String getDegree() {
        return degree;
    }

    @Override
    public Customer setDegree(String degree) {
        this.degree = degree;
        return this;
    }

    @Override
    public String getForename() {
        return forename;
    }

    @Override
    @Interceptable
    public Customer setForename(String forename) {
        this.forename = forename;
        return this;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public Customer setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public Customer setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String getPhoneCode() {
        return phoneCode;
    }

    @Override
    public Customer setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
        return this;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public Customer setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String getFax() {
        return fax;
    }

    @Override
    public Customer setFax(String fax) {
        this.fax = fax;
        return this;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Customer setLanguage(String language) {
        this.language = language;
        return this;
    }

    @Override
    public Boolean isNewsletterEnabled() {
        return newsletterEnabled;
    }

    @Override
    public Customer setNewsletterEnabled(Boolean newsletterEnabled) {
        this.newsletterEnabled = newsletterEnabled;
        return this;
    }

    @Override
    public String getCompany() {
        return company;
    }

    @Override
    public Customer setCompany(String company) {
        this.company = company;
        return this;
    }

    @Override
    public String getCompanyTaxIdNumber() {
        return companyTaxIdNumber;
    }

    @Override
    public Customer setCompanyTaxIdNumber(String companyTaxIdNumber) {
        this.companyTaxIdNumber = companyTaxIdNumber;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.id2 = str_(map.get(Col.ID2));
        this.customerNumber = str_(map.get(Col.CUSTOMER_NUMBER));
        this.merchantIds = idSet_(map.get(GlobalColumn.MERCHANT_ID));
        this.storeIds = idSet_(map.get(GlobalColumn.STORE_ID));
        this.requestContextIds = idSet_(map.get(GlobalColumn.REQUEST_CONTEXT_ID));
        this.customerGroupIds = idList_(map.get(Col.CUSTOMER_GROUP_IDS));
        this.salutation = str_(map.get(Col.SALUTATION));
        this.degree = str_(map.get(Col.DEGREE));
        this.forename = str_(map.get(Col.FORENAME));
        this.surname = str_(map.get(Col.SURNAME));
        this.email = str_(map.get(Col.EMAIL));
        this.phone = str_(map.get(Col.PHONE));
        this.phoneCode = str_(map.get(Col.PHONE_CODE));
        this.mobile = str_(map.get(Col.MOBILE));
        this.fax = str_(map.get(Col.FAX));
        this.language = str_(map.get(Col.LANGUAGE));
        this.company = str_(map.get(Col.COMPANY));
        this.companyTaxIdNumber = str_(map.get(Col.COMPANY_TAX_IDENTIFICATION_NUMBER));
        this.newsletterEnabled = bool_(map.get(Col.NEWSLETTER_ENABLED));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Col.ID, getId());
        m.put(Col.ID2, getId2());
        m.put(Col.CUSTOMER_NUMBER, getCustomerNumber());
        m.put(GlobalColumn.MERCHANT_ID, getMerchantIds());
        m.put(GlobalColumn.STORE_ID, getStoreIds());
        m.put(GlobalColumn.REQUEST_CONTEXT_ID, getRequestContextIds());
        m.put(Col.CUSTOMER_GROUP_IDS, getCustomerGroupIds());
        m.put(Col.SALUTATION, getSalutation());
        m.put(Col.DEGREE, getDegree());
        m.put(Col.FORENAME, getForename());
        m.put(Col.SURNAME, getSurname());
        m.put(Col.EMAIL, getEmail());
        m.put(Col.PHONE, getPhone());
        m.put(Col.PHONE_CODE, getPhoneCode());
        m.put(Col.MOBILE, getMobile());
        m.put(Col.FAX, getFax());
        m.put(Col.LANGUAGE, getLanguage());
        m.put(Col.COMPANY, getCompany());
        m.put(Col.COMPANY_TAX_IDENTIFICATION_NUMBER, getCompanyTaxIdNumber());
        m.put(Col.NEWSLETTER_ENABLED, isNewsletterEnabled());

        return m;
    }
}
