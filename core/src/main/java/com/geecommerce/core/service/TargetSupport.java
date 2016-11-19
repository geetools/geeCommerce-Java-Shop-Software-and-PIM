package com.geecommerce.core.service;

import com.geecommerce.core.type.ContextObject;

public interface TargetSupport {
    public ContextObject<String> getLabel();

    public ContextObject<String> getURI();
}
