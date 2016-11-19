package com.geecommerce.core.system.merchant.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.system.merchant.ContactRole;
import com.geecommerce.core.system.merchant.ContactType;
import com.geecommerce.core.type.Id;

public class DefaultContact extends AbstractModel implements Contact {
    private static final long serialVersionUID = -7951090227926618960L;

    private Id id = null;

    private String forename = null;

    private String surname = null;

    private String jobTitle = null;

    private String email = null;

    private ContactType type = null;

    private ContactRole role = null;

    private String homePhone = null;

    private String mobilePhone = null;

    private String workPhone = null;

    private List<Store> stores = new ArrayList<>();

    public Id getId() {
	return id;
    }

    public Contact setId(Id id) {
	this.id = id;
	return this;
    }

    public String getForename() {
	return forename;
    }

    public Contact setForename(String forename) {
	this.forename = forename;
	return this;
    }

    public String getSurname() {
	return surname;
    }

    public Contact setSurname(String surname) {
	this.surname = surname;
	return this;
    }

    public String getJobTitle() {
	return jobTitle;
    }

    public Contact setJobTitle(String jobTitle) {
	this.jobTitle = jobTitle;
	return this;
    }

    public String getEmail() {
	return email;
    }

    public Contact setEmail(String email) {
	this.email = email;
	return this;
    }

    public ContactType getType() {
	return type;
    }

    public Contact setType(ContactType type) {
	this.type = type;
	return this;
    }

    public ContactRole getRole() {
	return role;
    }

    public Contact setRole(ContactRole role) {
	this.role = role;
	return this;
    }

    public String getHomePhone() {
	return homePhone;
    }

    public Contact setHomePhone(String homePhone) {
	this.homePhone = homePhone;
	return this;
    }

    public String getMobilePhone() {
	return mobilePhone;
    }

    public Contact setMobilePhone(String mobilePhone) {
	this.mobilePhone = mobilePhone;
	return this;
    }

    public String getWorkPhone() {
	return workPhone;
    }

    public Contact setWorkPhone(String workPhone) {
	this.workPhone = workPhone;
	return this;
    }

    public List<Store> getStores() {
	return stores;
    }

    public Contact setStores(List<Store> stores) {
	this.stores = stores;
	return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void fromMap(Map<String, Object> map) {
	this.id = id_(map.get(Column.ID));
	this.forename = str_(map.get(Column.FORENAME));
	this.surname = str_(map.get(Column.SURNAME));
	this.jobTitle = str_(map.get(Column.JOB_TITLE));
	this.email = str_(map.get(Column.EMAIL));
	this.type = ContactType.fromId(int_(map.get(Column.TYPE)));
	this.role = ContactRole.fromId(int_(map.get(Column.ROLE)));
	this.homePhone = str_(map.get(Column.HOME_PHONE));
	this.mobilePhone = str_(map.get(Column.MOBILE_PHONE));
	this.workPhone = str_(map.get(Column.WORK_PHONE));

	this.stores = new ArrayList<>();

	Map storeMap = (Map) map.get(Column.STORES);

	if (storeMap != null && !storeMap.isEmpty()) {
	    // TODO
	}
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = new LinkedHashMap<String, Object>();

	map.put(Column.ID, getId());
	map.put(Column.FORENAME, getForename());
	map.put(Column.SURNAME, getSurname());
	map.put(Column.JOB_TITLE, getJobTitle());
	map.put(Column.EMAIL, getEmail());
	map.put(Column.TYPE, getType().toId());
	map.put(Column.ROLE, getRole().toId());
	map.put(Column.HOME_PHONE, getHomePhone());
	map.put(Column.MOBILE_PHONE, getMobilePhone());
	map.put(Column.WORK_PHONE, getWorkPhone());

	List<Id> storeIds = new ArrayList<>();

	for (Store store : stores) {
	    storeIds.add(store.getId());
	}

	if (storeIds.size() > 0) {
	    map.put(Column.STORES, storeIds);
	}

	return map;
    }
}
