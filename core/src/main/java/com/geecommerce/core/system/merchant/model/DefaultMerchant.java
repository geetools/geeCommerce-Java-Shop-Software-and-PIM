package com.geecommerce.core.system.merchant.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.Char;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.utils.Filenames;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("merchants")
public class DefaultMerchant extends AbstractModel implements Merchant {
    private static final long serialVersionUID = -5203070253321362987L;

    private Id id = null;

    private String code = null;

    private String companyName = null;

    private String companyAddressLine1 = null;

    private String companyAddressLine2 = null;

    private String companyCity = null;

    private String companyState = null;

    private String companyZipCode = null;

    private String companyCountry = null;

    private String companyPhone = null;

    private String companyFax = null;

    private String companyWebsite = null;

    private List<Contact> contacts = new ArrayList<>();

    private List<Store> stores = new ArrayList<>();

    private List<View> views = new ArrayList<>();

    private String baseSystemPath = null;

    // Lazy loaded
    private String absoluteBaseSystemPath = null;

    public DefaultMerchant() {
        super();
    }

    public DefaultMerchant(Id id, String code, String baseSystemPath) {
        super();
        this.id = id;
        this.code = code;
        this.baseSystemPath = baseSystemPath;
    }

    public DefaultMerchant(Id id, String code, String companyName, String companyAddressLine1,
        String companyAddressLine2, String companyCity, String companyState, String companyZipCode,
        String companyCountry, String companyPhone, String companyFax, String companyWebsite,
        List<Contact> contacts, List<Store> stores, List<View> views, String baseSystemPath) {
        super();
        this.id = id;
        this.code = code;
        this.companyName = companyName;
        this.companyAddressLine1 = companyAddressLine1;
        this.companyAddressLine2 = companyAddressLine2;
        this.companyCity = companyCity;
        this.companyState = companyState;
        this.companyZipCode = companyZipCode;
        this.companyCountry = companyCountry;
        this.companyPhone = companyPhone;
        this.companyFax = companyFax;
        this.companyWebsite = companyWebsite;
        this.contacts = contacts;
        this.stores = stores;
        this.views = views;
        this.baseSystemPath = baseSystemPath;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getCompanyAddressLine1() {
        return companyAddressLine1;
    }

    @Override
    public String getCompanyAddressLine2() {
        return companyAddressLine2;
    }

    @Override
    public String getCompanyCity() {
        return companyCity;
    }

    @Override
    public String getCompanyState() {
        return companyState;
    }

    @Override
    public String getCompanyZipCode() {
        return companyZipCode;
    }

    @Override
    public String getCompanyCountry() {
        return companyCountry;
    }

    @Override
    public String getCompanyPhone() {
        return companyPhone;
    }

    @Override
    public String getCompanyFax() {
        return companyFax;
    }

    @Override
    public String getCompanyWebsite() {
        return companyWebsite;
    }

    @JsonIgnore
    @Override
    public String getBaseSystemPath() {
        return this.baseSystemPath;
    }

    @JsonIgnore
    @Override
    public String getAbsoluteBaseSystemPath() {
        
        if (absoluteBaseSystemPath == null) {
            String appProjectsPath = app.systemConfig().val(SystemConfig.APPLICATION_PROJECTS_PATH);
            
            File f = new File(appProjectsPath, getBaseSystemPath());
            
            if (f.exists()) {
                this.absoluteBaseSystemPath = f.getAbsolutePath();
            } else {
                f = new File(getBaseSystemPath());

                if (f.exists()) {
                    this.absoluteBaseSystemPath = f.getAbsolutePath();
                }
            }
        }
        
        return this.absoluteBaseSystemPath;
    }

    @Override
    public List<Contact> getContacts() {
        return contacts;
    }

    @Override
    public List<Store> getStores() {
        return stores;
    }

    @Override
    public Store getStore(Id storeId) {
        if (storeId == null)
            return null;

        if (stores != null) {
            for (Store store : stores) {
                if (store.getId().equals(storeId)) {
                    return store;
                }
            }
        }

        return null;
    }

    @Override
    public Store getStore(String storeCode) {
        if (storeCode == null)
            return null;

        if (stores != null) {
            for (Store store : stores) {
                if (store.getCode().equals(storeCode)) {
                    return store;
                }
            }
        }

        return null;
    }

    @Override
    public Store getStoreFor(RequestContext requestCtx) {
        if (requestCtx == null)
            return null;

        if (stores != null) {
            for (Store store : stores) {
                if (store.getId().equals(requestCtx.getStoreId())) {
                    return store;
                }
            }
        }

        return null;
    }

    @Override
    public List<View> getViews() {
        return views;
    }

    @Override
    public View getView(Id viewId) {
        if (viewId == null)
            return null;

        if (views != null) {
            for (View view : views) {
                if (view.getId().equals(viewId)) {
                    return view;
                }
            }
        }

        return null;
    }

    @Override
    public View getViewFor(RequestContext requestCtx) {
        if (requestCtx == null)
            return null;

        if (views != null) {
            for (View view : views) {
                if (view.getId().equals(requestCtx.getViewId())) {
                    return view;
                }
            }
        }

        return null;
    }

    @JsonIgnore
    @Override
    public String getConfigurationPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("conf").toString();
    }

    @JsonIgnore
    @Override
    public String getLogPath() {
        RequestContext requestCtx = app.context().getRequestContext();
        Store store = getStoreFor(requestCtx);

        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("log")
            .append(File.separatorChar).append(Char.UNDERSCORE)
            .append(Filenames.ensureSafeName(store.getCode() != null ? store.getCode() : store.getName(), true))
            .toString();
    }

    @JsonIgnore
    @Override
    public String getClassesPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("custom")
            .append(File.separatorChar).append("classes").toString();
    }

    @JsonIgnore
    @Override
    public URL[] getClasspath() throws MalformedURLException {
        return new URL[] { new File(getClassesPath()).toURI().toURL() };
    }

    @JsonIgnore
    @Override
    public String getTemplatesPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("custom")
            .append(File.separatorChar).append("templates").toString();
    }

    @JsonIgnore
    @Override
    public String getWebPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("web").toString();
    }

    @JsonIgnore
    @Override
    public String getResourcesPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("resources").toString();
    }

    @JsonIgnore
    @Override
    public String getCertsPath() {
        return new StringBuilder(getResourcesPath()).append(File.separatorChar).append("certs").toString();
    }

    @JsonIgnore
    @Override
    public String getModulesPath() {
        return new StringBuilder(getAbsoluteBaseSystemPath()).append(File.separatorChar).append("modules").toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        this.id = id_(map.get(Column.ID));
        this.code = str_(map.get(Column.CODE));
        this.companyName = str_(map.get(Column.COMPANY_NAME));
        this.companyAddressLine1 = str_(map.get(Column.COMPANY_ADDRESS_LINE1));
        this.companyAddressLine2 = str_(map.get(Column.COMPANY_ADDRESS_LINE2));
        this.companyCity = str_(map.get(Column.COMPANY_CITY));
        this.companyState = str_(map.get(Column.COMPANY_STATE));
        this.companyZipCode = str_(map.get(Column.COMPANY_ZIPCODE));
        this.companyCountry = str_(map.get(Column.COMPANY_COUNTRY));
        this.companyPhone = str_(map.get(Column.COMPANY_PHONE));
        this.companyFax = str_(map.get(Column.COMPANY_FAX));
        this.companyWebsite = str_(map.get(Column.COMPANY_WEBSITE));
        this.baseSystemPath = str_(map.get(Column.BASE_SYSTEM_PATH));

        this.stores = new ArrayList<>();

        List<Map> storeList = (List<Map>) map.get(Column.STORES);

        if (storeList != null && !storeList.isEmpty()) {
            for (Map storeMap : storeList) {
                Store store = app.model(Store.class);
                store.fromMap(storeMap);
                store.belongsTo(this);

                this.stores.add(store);
            }
        }

        this.views = new ArrayList<>();

        List<Map> viewList = (List<Map>) map.get(Column.VIEWS);

        if (viewList != null && !viewList.isEmpty()) {
            for (Map viewMap : viewList) {
                View view = app.model(View.class);
                view.fromMap(viewMap);
                view.belongsTo(this);

                this.views.add(view);
            }
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        if (getId() != null) {
            map.put(Column.ID, getId());
        }

        map.put(Column.COMPANY_NAME, getCompanyName());
        map.put(Column.CODE, getCode());
        map.put(Column.COMPANY_ADDRESS_LINE1, getCompanyAddressLine1());
        map.put(Column.COMPANY_ADDRESS_LINE2, getCompanyAddressLine2());
        map.put(Column.COMPANY_CITY, getCompanyCity());
        map.put(Column.COMPANY_STATE, getCompanyState());
        map.put(Column.COMPANY_ZIPCODE, getCompanyZipCode());
        map.put(Column.COMPANY_COUNTRY, getCompanyCountry());
        map.put(Column.COMPANY_PHONE, getCompanyPhone());
        map.put(Column.COMPANY_FAX, getCompanyFax());
        map.put(Column.COMPANY_WEBSITE, getCompanyWebsite());
        map.put(Column.BASE_SYSTEM_PATH, getBaseSystemPath());

        List<Map<String, Object>> storeMaps = new ArrayList<>();

        for (Store store : this.getStores()) {
            storeMaps.add(store.toMap());
        }

        map.put(Column.STORES, storeMaps);

        List<Map<String, Object>> viewMaps = new ArrayList<>();

        for (View view : this.getViews()) {
            viewMaps.add(view.toMap());
        }

        map.put(Column.VIEWS, viewMaps);

        List<Map<String, Object>> contactMaps = new ArrayList<>();

        for (Contact contact : this.getContacts()) {
            contactMaps.add(contact.toMap());
        }

        map.put(Column.CONTACTS, contactMaps);

        return map;
    }
}
