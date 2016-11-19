package com.geecommerce.core.system.merchant;

public enum ContactRole {
    BUSINESS(1), FINANCE(2), TECHNICAL_LEAD(3), TECHNICAL_SUPPORT(4), CUSTOMER_SUPPORT(5);

    private int id;

    private ContactRole(int id) {
	this.id = id;
    }

    public final int toId() {
	return this.id;
    }

    public static final ContactRole fromId(int id) {
	for (ContactRole contactRole : values()) {
	    if (contactRole.toId() == id) {
		return contactRole;
	    }
	}

	return null;
    }
}
