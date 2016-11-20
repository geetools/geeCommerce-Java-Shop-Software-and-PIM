package com.geecommerce.core.service;

import java.util.HashMap;

public class EmptyFilter extends HashMap<String, Object> {
    private static final long serialVersionUID = -1057103253960715447L;

    private static final EmptyFilter EMPTY_FILTER = new EmptyFilter();

    public static final EmptyFilter get() {
        return EMPTY_FILTER;
    }
}
