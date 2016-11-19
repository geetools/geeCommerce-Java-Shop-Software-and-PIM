package com.geecommerce.core.system.user.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "auth_token")
public class AuthenticationToken implements Serializable {
    private static final long serialVersionUID = -1333063685286034846L;
    private String token = null;

    public AuthenticationToken() {
    }

    public AuthenticationToken(String token) {
	this.token = token;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }
}
