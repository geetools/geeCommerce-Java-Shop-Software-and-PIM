package com.geecommerce.core.service;

import java.util.Map;

import com.geecommerce.core.type.Id;

public class SimpleModel extends AbstractModel {
    private static final long serialVersionUID = 2816091410375682658L;

    @Override
    public Id getId() {
	throw new IllegalStateException("Not supported by SimpleModel. Use AbstractModel instead.");
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	throw new IllegalStateException("Not supported by SimpleModel. Use AbstractModel instead.");
    }

    @Override
    public Map<String, Object> toMap() {
	throw new IllegalStateException("Not supported by SimpleModel. Use AbstractModel instead.");
    }
}
