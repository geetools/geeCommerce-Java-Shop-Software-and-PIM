package com.geecommerce.core.system.user.pojo;

import java.io.Serializable;
import java.util.List;

import com.geecommerce.core.type.Id;

public class ClientSession implements Serializable {
    private static final long serialVersionUID = 3672799788909727802L;
    private final String username;
    private final String name;
    private final List<Id> allowedScopes;
    private final ClientType clientType;

    enum ClientType {
	APPLICATION, USER;
    }

    public ClientSession(String username, String name, List<Id> allowedScopes) {
	super();
	this.username = username;
	this.name = name;
	this.allowedScopes = allowedScopes;
	this.clientType = ClientType.USER;
    }

    public String getUsername() {
	return username;
    }

    public String getName() {
	return name;
    }

    public List<Id> getAllowedScopes() {
	return allowedScopes;
    }

    @Override
    public String toString() {
	return "ClientSession [username=" + username + ", name=" + name + ", allowedScopes=" + allowedScopes + ", clientType=" + clientType + "]";
    }
}
