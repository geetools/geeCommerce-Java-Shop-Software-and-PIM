package com.geecommerce.core.system.merchant;

public enum ContactType {
    PRIMARY(1), SECONDARY(2);

    private int id;

    private ContactType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final ContactType fromId(int id) {
        for (ContactType contactType : values()) {
            if (contactType.toId() == id) {
                return contactType;
            }
        }

        return null;
    }
}
