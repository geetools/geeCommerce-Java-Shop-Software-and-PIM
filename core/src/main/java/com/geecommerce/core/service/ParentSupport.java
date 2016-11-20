package com.geecommerce.core.service;

import com.geecommerce.core.service.api.MultiContextModel;
import com.owlike.genson.annotation.JsonIgnore;

public interface ParentSupport<T> extends MultiContextModel {
    @JsonIgnore
    public T getParent();
}
