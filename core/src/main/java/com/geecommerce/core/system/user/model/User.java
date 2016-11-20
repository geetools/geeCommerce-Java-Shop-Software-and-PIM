package com.geecommerce.core.system.user.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface User extends Model {
    public Id getId();

    public User setId(Id id);

    public String getUsername();

    public User setUsername(String username);

    public String getEmail();

    public User setEmail(String email);

    public String getForename();

    public User setForename(String forename);

    public String getSurname();

    public User setSurname(String surname);

    public byte[] getPassword();

    public User setPassword(byte[] password);

    public byte[] getSalt();

    public User setSalt(byte[] salt);

    public String getForgotPasswordToken();

    public User setForgotPasswordToken(String forgotPasswordToken);

    public String getForgotPasswordSalt();

    public User setForgotPasswordSalt(String forgotPasswordSalt);

    public Date getForgotPasswordOn();

    public User setForgotPasswordOn(Date forgotPasswordOn);

    public Date getLastLoggedIn();

    public User setLastLoggedIn(Date lastLoggedIn);

    public boolean isEnabled();

    public User setEnabled(boolean enabled);

    public List<Id> getRoleIds();

    @JsonIgnore
    public List<Role> getRoles();

    public User addRole(Role role);

    public User removeRole(Id roleId);

    public List<Id> getScopeIds();

    public User addScopeId(Id scopeId);

    static final class Column {
        public static final String ID = "_id";
        public static final String EMAIL = "email";
        public static final String USERNAME = "username";
        public static final String FORENAME = "forename";
        public static final String SURNAME = "surname";
        public static final String PASSWORD = "password";
        public static final String SALT = "salt";
        public static final String ENABLED = "enabled";
        public static final String FORGOT_PASSWORD_TOKEN = "fp_token";
        public static final String FORGOT_PASSWORD_SALT = "fp_salt";
        public static final String FORGOT_PASSWORD_ON = "fp_on";
        public static final String LAST_LOGGED_IN = "l_login";
        public static final String ROLES = "roles";
        public static final String SCOPES = "scopes";
    }
}
