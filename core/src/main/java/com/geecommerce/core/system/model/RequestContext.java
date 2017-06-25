package com.geecommerce.core.system.model;

import java.util.Date;
import java.util.Locale;

import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface RequestContext extends Model {
    public Id getId();

    public RequestContext setId(Id id);

    public Id getMerchantId();

    public RequestContext setMerchantId(Id merchantId);

    public Id getStoreId();

    public RequestContext setStoreId(Id storeId);

    public String getLanguage();

    public RequestContext setLanguage(String language);

    public String getCountry();

    public RequestContext setCountry(String country);

    public Id getViewId();

    public RequestContext setViewId(Id viewId);

    public String getUrlPrefix();

    public RequestContext setUrlPrefix(String urlPrefix);

    public UrlType getUrlType();

    public RequestContext setUrlType(UrlType urlType);

    public Integer getSortIndex();

    public RequestContext setSortIndex(Integer sortIndex);

    public Locale getLocale();

    public String toKey();

    static final class Col {
        public static final String ID = "_id";
        public static final String MERCHANT_ID = "merch_id";
        public static final String STORE_ID = "store_id";
        public static final String LANGUAGE = "lang";
        public static final String COUNTRY = "country";
        public static final String VIEW_ID = "view_id";
        public static final String URL_PREFIX = "url_prefix";
        public static final String URL_TYPE = "url_type";
        public static final String SORT_INDEX = "sort_idx";

    }
}
