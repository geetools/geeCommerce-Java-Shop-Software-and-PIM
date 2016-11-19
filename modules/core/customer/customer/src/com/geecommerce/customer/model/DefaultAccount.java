package com.geecommerce.customer.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.Strings;

@Model("customer_accounts")
public class DefaultAccount extends AbstractModel implements Account {
    private static final long serialVersionUID = -408095746853544272L;
    private Id id = null;
    private Id customerId = null;
    protected Set<Id> merchantIds = null;
    protected Set<Id> storeIds = null;
    private Set<Id> requestContextIds = null;
    private Map<String, String> externalIdentifiers = new HashMap<>();
    private String username;
    private byte[] password;
    private byte[] salt;
    private Boolean enabled = null;
    private String forgotPasswordToken = null;
    private String forgotPasswordSalt = null;
    private Date forgotPasswordOn = null;
    private Date lastLoggedIn = null;
    private Boolean requirePasswordChange = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public Account setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getCustomerId() {
	return customerId;
    }

    @Override
    public Account belongsTo(Customer customer) {
	if (customer == null || customer.getId() == null)
	    throw new NullPointerException("The customerId cannot be null");

	this.customerId = customer.getId();
	return this;
    }

    @Override
    public Set<Id> getMerchantIds() {
	return merchantIds;
    }

    @Override
    public Account addTo(Merchant merchant) {
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
    public Account addTo(Store store) {
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
    public Account addTo(RequestContext requestContext) {
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
    public boolean hasExternalIdentifier(String key, String value) {
	if (externalIdentifiers == null)
	    return false;

	if (externalIdentifiers.get(key) != null && externalIdentifiers.get(key).equals(value)) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public Account addExternalIdentifier(String key, String value) {
	externalIdentifiers.put(key, value);
	return this;
    }

    private final Map<String, String> getExternalIdentifiers() {
	return externalIdentifiers;
    }

    @Override
    public String getUsername() {
	return username;
    }

    @Override
    public Account setUsername(String username) {
	this.username = username;
	return this;
    }

    @Override
    public byte[] getPassword() {
	return password;
    }

    @Override
    public Account setPassword(byte[] password) {
	this.password = password;
	return this;
    }

    @Override
    public byte[] getSalt() {
	return salt;
    }

    @Override
    public Account setSalt(byte[] salt) {
	this.salt = salt;
	return this;
    }

    @Override
    public Boolean isEnabled() {
	return enabled == null ? false : enabled;
    }

    @Override
    public Account enableAccount() {
	this.enabled = true;
	return this;
    }

    @Override
    public Account disableAccount() {
	this.enabled = false;
	return this;
    }

    @Override
    public String getForgotPasswordToken() {
	return forgotPasswordToken;
    }

    private final String getForgotPasswordSalt() {
	return forgotPasswordSalt;
    }

    @Override
    public Date getForgotPasswordOn() {
	return forgotPasswordOn;
    }

    @Override
    public Account createForgotPasswordToken() {
	this.forgotPasswordOn = DateTimes.newDate();
	this.forgotPasswordSalt = RandomStringUtils.randomAlphanumeric(8);
	this.forgotPasswordToken = Strings.toMD5(String.valueOf(id) + String.valueOf(forgotPasswordOn.getTime()) + forgotPasswordSalt);

	return this;
    }

    @Override
    public Account removeForgotPasswordToken() {
	this.forgotPasswordOn = null;
	this.forgotPasswordSalt = "";
	this.forgotPasswordToken = "";

	return this;
    }

    @Override
    public boolean isForgotPasswordTokenValid(String token, Id id, Long time) {
	if (token == null || id == null || time == null || this.forgotPasswordToken == null || this.id == null || this.forgotPasswordOn == null) {
	    return false;
	}

	if (!this.forgotPasswordToken.equals(token) || !this.id.equals(id) || this.forgotPasswordOn.getTime() != time) {
	    return false;
	}

	// Token is only valid for 24 hours.
	long now = System.currentTimeMillis();
	int oneDay = 24 * 60 * 60 * 1000;

	if ((now - oneDay) > time) {
	    return false;
	}

	String checkToken = Strings.toMD5(id.toString() + String.valueOf(time) + forgotPasswordSalt);

	return checkToken.equals(this.forgotPasswordToken);
    }

    @Override
    public Account setRequirePasswordChange(Boolean requirePasswordChange) {
	return null;
    }

    @Override
    public boolean getRequirePasswordChange() {
	return false;
    }

    @Override
    public Date getLastLoggedIn() {
	return lastLoggedIn;
    }

    @Override
    public Account setLastLoggedIn(Date lastLoggedIn) {
	this.lastLoggedIn = lastLoggedIn;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.customerId = id_(map.get(Column.CUSTOMER_ID));
	this.merchantIds = idSet_(map.get(GlobalColumn.MERCHANT_ID));
	this.storeIds = idSet_(map.get(GlobalColumn.STORE_ID));
	this.requestContextIds = idSet_(map.get(GlobalColumn.REQUEST_CONTEXT_ID));
	this.username = str_(map.get(Column.USERNAME));
	this.password = bytes_(map.get(Column.PASSWORD));
	this.salt = bytes_(map.get(Column.SALT));
	this.enabled = bool_(map.get(Column.ENABLED));
	this.forgotPasswordToken = str_(map.get(Column.FORGOT_PASSWORD_TOKEN));
	this.forgotPasswordSalt = str_(map.get(Column.FORGOT_PASSWORD_SALT));
	this.forgotPasswordOn = date_(map.get(Column.FORGOT_PASSWORD_ON));
	this.lastLoggedIn = date_(map.get(Column.LAST_LOGGED_IN));
	this.externalIdentifiers = map_(map.get(Column.EXTERNAL_IDENTIFIERS));
	this.requirePasswordChange = bool_(map.get(Column.REQUIRE_PASSWORD_CHANGE), false);
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
	m.put(Column.ID, getId());
	m.put(Column.CUSTOMER_ID, getCustomerId());
	m.put(GlobalColumn.MERCHANT_ID, getMerchantIds());
	m.put(GlobalColumn.STORE_ID, getStoreIds());
	m.put(GlobalColumn.REQUEST_CONTEXT_ID, getRequestContextIds());
	m.put(Column.USERNAME, getUsername());
	m.put(Column.PASSWORD, getPassword());
	m.put(Column.SALT, getSalt());
	m.put(Column.ENABLED, isEnabled());
	m.put(Column.LAST_LOGGED_IN, getLastLoggedIn());
	m.put(Column.REQUIRE_PASSWORD_CHANGE, getRequirePasswordChange());

	if (getExternalIdentifiers() != null)
	    m.put(Column.EXTERNAL_IDENTIFIERS, getExternalIdentifiers());

	if (getForgotPasswordToken() != null) {
	    m.put(Column.FORGOT_PASSWORD_TOKEN, getForgotPasswordToken());
	    m.put(Column.FORGOT_PASSWORD_SALT, getForgotPasswordSalt());
	    m.put(Column.FORGOT_PASSWORD_ON, getForgotPasswordOn());
	}

	return m;
    }
}
