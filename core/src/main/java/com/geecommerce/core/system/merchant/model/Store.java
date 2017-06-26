package com.geecommerce.core.system.merchant.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface Store extends Model {
    public Id getId();

    public Store setId(Id id);

    public String getCode();

    public Store setCode(String code);

    public String getName();

    public Store setName(String name);

    public String getDefaultLanguage();

    public Store setDefaultLanguage(String defaultLanguage);

    public String getIconPathXS();

    public Store setIconPathXS(String iconPathXS);

    public String getIconPathS();

    public Store setIconPathS(String iconPathS);

    public Id getParentStoreId();

    public Store setParentStoreId(Id parentStoreId);

    public Store belongsTo(Merchant merchant);

    @JsonIgnore
    public String getTemplatesPath();

    @JsonIgnore
    public String getResourcesPath();

    @JsonIgnore
    public String getCertsPath();

    static final class Col {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String DEFAULT_LANGUAGE = "lng";
        public static final String ICON_PATH_XS = "icon_path_xs";
        public static final String ICON_PATH_S = "icon_path_s";
        public static final String PARENT_STORE_ID = "p_store_id";
    }
}
