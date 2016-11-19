package com.geecommerce.core.service;

import com.owlike.genson.annotation.JsonIgnore;
import com.geecommerce.core.service.api.MultiContextModel;

public interface ParentSupport<T> extends MultiContextModel {
    @JsonIgnore
    public T getParent();
}
