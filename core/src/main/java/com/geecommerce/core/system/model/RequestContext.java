package com.geecommerce.core.system.model;

import java.util.Date;
import java.util.Locale;

import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface RequestContext extends Model {
    public Id getId();

    public Id getMerchantId();

    public Id getStoreId();

    public String getLanguage();

    public String getCountry();

    public Id getViewId();

    public String getUrlPrefix();

    public UrlType getUrlType();

    public Integer getSortIndex();

    public Date getCreatedOn();

    public String getCreatedBy();

    public Date getModifiedOn();

    public String getModifiedBy();

    public Locale getLocale();

    public String toKey();

    static final class Column {
        public static final String ID = "_id";
        public static final String MERCHANT_ID = "merch_id";
        public static final String STORE_ID = "store_id";
        public static final String LANGUAGE = "lang";
        public static final String COUNTRY = "country";
        public static final String VIEW_ID = "view_id";
        public static final String URL_PREFIX = "url_prefix";
        public static final String URL_TYPE = "url_type";
        public static final String SORT_INDEX = "sort_idx";
        public static final String CREATED_ON = "cr_on";
        public static final String CREATED_BY = "cr_by";
        public static final String MODIFIED_ON = "mod_on";
        public static final String MODIFIED_BY = "mod_by";
    }
}
