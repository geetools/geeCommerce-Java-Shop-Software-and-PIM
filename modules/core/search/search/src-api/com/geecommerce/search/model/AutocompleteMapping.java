package com.geecommerce.search.model;

import java.util.List;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AutocompleteMapping extends MultiContextModel {
    public Id getId();

    public AutocompleteMapping setId(Id id);

    public String getKeyword();

    public AutocompleteMapping setKeyword(String keyword);

    public String getLabel();

    public AutocompleteMapping setLabel(ContextObject<String> label);

    public Id getTargetObjectId();

    public AutocompleteMapping setTargetObjectId(Id targetObjectId);

    public ObjectType getTargetObjectType();

    public AutocompleteMapping setTargetObjectType(ObjectType targetObjectType);

    public boolean isUseTargetObjectLabel();

    public AutocompleteMapping setUseTargetObjectLabel(Boolean useTargetObjectLabel);

    public ContextObject<String> getExternalURL();

    public AutocompleteMapping setExternalURL(ContextObject<String> externalURL);

    public ContextObject<String> getLabels();

    public String getDisplayLabel();

    public String getDisplayURI();

    public boolean isForProductList();

    public boolean isForProduct();

    public boolean isForCMS();

    public boolean hasExternalURL();

    public List<String> getDividedKeyword();

    public AutocompleteMapping setDividedKeyword(List<String> dividedKeyword);

    static final class Column {
        public static final String ID = "_id";
        public static final String KEYWORD = "keyword";
        public static final String DIVIDED_KEYWORD = "divided_keyword";
        public static final String LABEL = "label";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TARGET_OBJECT_TYPE = "tar_obj_type";
        public static final String TARGET_OBJECT_LABEL = "tar_obj_label";
        public static final String EXTERNAL_URL = "ext_url";
    }

}
