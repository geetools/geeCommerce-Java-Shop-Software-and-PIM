package com.geecommerce.core.system.merchant.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.ContactRole;
import com.geecommerce.core.system.merchant.ContactType;
import com.geecommerce.core.type.Id;

public interface Contact extends Model {
    public Id getId();

    public Contact setId(Id id);

    public String getForename();

    public Contact setForename(String forename);

    public String getSurname();

    public Contact setSurname(String surname);

    public String getJobTitle();

    public Contact setJobTitle(String jobTitle);

    public String getEmail();

    public Contact setEmail(String email);

    public ContactType getType();

    public Contact setType(ContactType type);

    public ContactRole getRole();

    public Contact setRole(ContactRole role);

    public String getHomePhone();

    public Contact setHomePhone(String homePhone);

    public String getMobilePhone();

    public Contact setMobilePhone(String mobilePhone);

    public String getWorkPhone();

    public Contact setWorkPhone(String workPhone);

    public List<Store> getStores();

    public Contact setStores(List<Store> stores);

    static final class Column {
        public static final String ID = "_id";
        public static final String FORENAME = "forename";
        public static final String SURNAME = "surname";
        public static final String JOB_TITLE = "job_title";
        public static final String EMAIL = "email";
        public static final String TYPE = "type";
        public static final String ROLE = "role";
        public static final String HOME_PHONE = "home_phone";
        public static final String MOBILE_PHONE = "mobile_phone";
        public static final String WORK_PHONE = "work_phone";
        public static final String STORES = "stores";
    }
}
