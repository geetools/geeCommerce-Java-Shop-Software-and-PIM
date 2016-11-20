package com.geecommerce.core.service;

import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.owlike.genson.annotation.JsonIgnore;

public interface ChildSupport<T> extends MultiContextModel {
    public enum Lookup {
        NONE, FIRST, ANY;
    }

    @JsonIgnore
    public List<T> getChildren();

    @JsonIgnore
    public List<T> getAnyChildren();

    @JsonIgnore
    public boolean isValidChild();
}
