package com.geecommerce.customer.model;

import java.util.Date;
import java.util.Set;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface Account extends Model {
    public Id getId();

    public Account setId(Id id);

    public Id getCustomerId();

    public Account belongsTo(Customer customer);

    public Set<Id> getMerchantIds();

    public Account addTo(Merchant merchant);

    public boolean isIn(Merchant merchant);

    public Set<Id> getStoreIds();

    public Account addTo(Store store);

    public boolean isIn(Store store);

    public Set<Id> getRequestContextIds();

    public Account addTo(RequestContext requestContext);

    public boolean isIn(RequestContext requestContext);

    public boolean hasExternalIdentifier(String key, String value);

    public Account addExternalIdentifier(String key, String value);

    public String getUsername();

    public Account setUsername(String username);

    public byte[] getPassword();

    public Account setPassword(byte[] password);

    public byte[] getSalt();

    public Account setSalt(byte[] salt);

    public Boolean isEnabled();

    public Account enableAccount();

    public Account disableAccount();

    public String getForgotPasswordToken();

    public Date getForgotPasswordOn();

    public Date getLastLoggedIn();

    public Account setLastLoggedIn(Date lastLoggedIn);

    public Date getCreatedOn();

    public Date getModifiedOn();

    public Account createForgotPasswordToken();

    public Account removeForgotPasswordToken();

    public boolean isForgotPasswordTokenValid(String token, Id id, Long time);

    public Account setRequirePasswordChange(Boolean requirePasswordChange);

    public boolean getRequirePasswordChange();

    static final class Column {
	public static final String ID = "_id";
	public static final String CUSTOMER_ID = "customer_id";
	public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
	public static final String STORE_ID = "s";
	public static final String EXTERNAL_IDENTIFIERS = "ext_ids";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String SALT = "salt";
	public static final String ENABLED = "enabled";
	public static final String FORGOT_PASSWORD_TOKEN = "fp_token";
	public static final String FORGOT_PASSWORD_SALT = "fp_salt";
	public static final String FORGOT_PASSWORD_ON = "fp_on";
	public static final String LAST_LOGGED_IN = "l_login";
	public static final String REQUIRE_PASSWORD_CHANGE = "r_p_change";
    }
}
