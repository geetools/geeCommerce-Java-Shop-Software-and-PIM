package com.geecommerce.core.system.user.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.query.model.QueryNode;
import com.geecommerce.core.system.user.repository.Roles;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model("_users")
@XmlRootElement(name = "user")
public class DefaultUser extends AbstractModel implements User {
    private static final long serialVersionUID = 297135100681943106L;

    @Column(Col.ID)
    private Id id = null;
    @Column(Col.USERNAME)
    private String username = null;
    @Column(Col.EMAIL)
    private String email = null;
    @Column(Col.FORENAME)
    private String forename = null;
    @Column(Col.SURNAME)
    private String surname = null;

    private byte[] password;
    private byte[] salt;
    private String forgotPasswordToken = null;
    private String forgotPasswordSalt = null;
    private Date forgotPasswordOn = null;
    private Date lastLoggedIn = null;
    private boolean enabled = false;

    public QueryNode queryNode;

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<Id> roleIds = new ArrayList<>();

    @XmlElementWrapper(name = "scopes")
    @XmlElement(name = "scope")
    private List<Id> scopeIds = new ArrayList<>();

    // Lazy loaded user roles
    private List<Role> userRoles = null;

    // UserRoles Repository
    private final Roles roles;

    public DefaultUser() {
        this(i(Roles.class));
    }

    @Inject
    private DefaultUser(Roles roles) {
        this.roles = roles;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public User setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String getForename() {
        return forename;
    }

    @Override
    public User setForename(String forename) {
        this.forename = forename;
        return this;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public User setPassword(byte[] password) {
        this.password = password;
        return this;
    }

    @Override
    public byte[] getSalt() {
        return salt;
    }

    @Override
    public User setSalt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    @Override
    public String getForgotPasswordToken() {
        return forgotPasswordToken;
    }

    @Override
    public User setForgotPasswordToken(String forgotPasswordToken) {
        this.forgotPasswordToken = forgotPasswordToken;
        return this;
    }

    @Override
    public String getForgotPasswordSalt() {
        return forgotPasswordSalt;
    }

    @Override
    public User setForgotPasswordSalt(String forgotPasswordSalt) {
        this.forgotPasswordSalt = forgotPasswordSalt;
        return this;
    }

    @Override
    public Date getForgotPasswordOn() {
        return forgotPasswordOn;
    }

    @Override
    public User setForgotPasswordOn(Date forgotPasswordOn) {
        this.forgotPasswordOn = forgotPasswordOn;
        return this;
    }

    @Override
    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    @Override
    public User setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public List<Id> getRoleIds() {
        return roleIds;
    }

    @Override
    public List<Role> getRoles() {
        if (userRoles == null && roleIds != null && roleIds.size() > 0) {
            userRoles = roles.findByIds(Role.class, roleIds.toArray(new Id[roleIds.size()]));
        }

        return userRoles;
    }

    @Override
    public User addRole(Role role) {
        if (role == null || role.getId() == null)
            return this;

        if (!roleIds.contains(role.getId())) {
            roleIds.add(role.getId());

            // Make sure that the list is refreshed on next access.
            this.userRoles = null;
        }

        return this;
    }

    @Override
    public User removeRole(Id roleId) {
        if (roleId == null || roleIds == null)
            return this;

        if (roleIds.contains(roleId)) {
            roleIds.remove(roleId);

            // Make sure that the list is refreshed on next access.
            this.userRoles = null;
        }

        return this;
    }

    @Override
    public List<Id> getScopeIds() {
        return scopeIds;
    }

    @Override
    public User addScopeId(Id scopeId) {
        if (!scopeIds.contains(scopeId)) {
            scopeIds.add(scopeId);
        }

        return this;
    }


    @Override
    public QueryNode getQueryNode() {
        return queryNode;
    }

    @Override
    public User setQueryNode(QueryNode queryNode) {
        this.queryNode = queryNode;
        return this;
    }


    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.username = str_(map.get(Col.USERNAME));
        this.email = str_(map.get(Col.EMAIL));
        this.forename = str_(map.get(Col.FORENAME));
        this.surname = str_(map.get(Col.SURNAME));
        this.password = bytes_(map.get(Col.PASSWORD));
        this.salt = bytes_(map.get(Col.SALT));
        this.enabled = bool_(map.get(Col.ENABLED));
        this.forgotPasswordToken = str_(map.get(Col.FORGOT_PASSWORD_TOKEN));
        this.forgotPasswordSalt = str_(map.get(Col.FORGOT_PASSWORD_SALT));
        this.forgotPasswordOn = date_(map.get(Col.FORGOT_PASSWORD_ON));
        this.lastLoggedIn = date_(map.get(Col.LAST_LOGGED_IN));
        this.roleIds = idList_(map.get(Col.ROLES));
        this.scopeIds = idList_(map.get(Col.SCOPES));

        Map<String, Object> queryNodeMap = map_(map.get(Col.QUERY_NODE));
        if (queryNodeMap != null && queryNodeMap.size() > 0) {
            this.queryNode = app.model(QueryNode.class);
            this.queryNode.fromMap(queryNodeMap);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());

        m.put(Col.ID, getId());
        m.put(Col.USERNAME, getUsername());
        m.put(Col.EMAIL, getEmail());
        m.put(Col.FORENAME, getForename());
        m.put(Col.SURNAME, getSurname());
        m.put(Col.PASSWORD, getPassword());
        m.put(Col.SALT, getSalt());
        m.put(Col.ENABLED, isEnabled());
        m.put(Col.LAST_LOGGED_IN, getLastLoggedIn());
        m.put(Col.ROLES, getRoleIds());
        m.put(Col.SCOPES, getScopeIds());

        if (getForgotPasswordToken() != null) {
            m.put(Col.FORGOT_PASSWORD_TOKEN, getForgotPasswordToken());
            m.put(Col.FORGOT_PASSWORD_SALT, getForgotPasswordSalt());
            m.put(Col.FORGOT_PASSWORD_ON, getForgotPasswordOn());
        }

        if (getQueryNode() != null) {
            m.put(Col.QUERY_NODE, getQueryNode().toMap());
        }

        return m;
    }

    @Override
    public String toString() {
        return "DefaultUser [id=" + id + ", username=" + username + ", email=" + email + ", forename=" + forename
            + ", surname=" + surname + ", lastLoggedIn=" + lastLoggedIn + ", enabled=" + enabled + ", roleIds="
            + roleIds + ", scopeIds=" + scopeIds + "]";
    }
}
