package com.geecommerce.core.customer.account;

import com.geecommerce.core.type.Id;

public class LoginResponse {
    private boolean loginSuccessful = false;

    private String externalIdKey = null;

    private String externalIdValue = null;

    private Id internalCustomerId = null;

    private LoginResponse() {

    }

    private LoginResponse(boolean loginSuccessful, Id internalCustomerId) {
        this.loginSuccessful = loginSuccessful;
        this.internalCustomerId = internalCustomerId;
    }

    private LoginResponse(boolean loginSuccessful, String externalIdKey, String externalIdValue) {
        this.loginSuccessful = loginSuccessful;
        this.externalIdKey = externalIdKey;
        this.externalIdValue = externalIdValue;
    }

    public static LoginResponse loginSucceeded(Id internalCustomerId) {
        return new LoginResponse(true, internalCustomerId);
    }

    public static LoginResponse loginSucceeded(String externalIdKey, String externalIdValue) {
        return new LoginResponse(true, externalIdKey, externalIdValue);
    }

    public static LoginResponse loginFailed() {
        return new LoginResponse();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getExternalIdKey() {
        return externalIdKey;
    }

    public String getExternalIdValue() {
        return externalIdValue;
    }

    public Id getInternalCustomerId() {
        return internalCustomerId;
    }
}
