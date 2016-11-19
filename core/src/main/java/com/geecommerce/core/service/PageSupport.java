package com.geecommerce.core.service;

import com.geecommerce.core.type.ContextObject;

public interface PageSupport {
    public ContextObject<String> getTitle();

    public ContextObject<String> getURI();

    public ContextObject<String> getCanonicalURI();

    public ContextObject<String> getMetaDescription();

    public ContextObject<String> getMetaRobots();
}
