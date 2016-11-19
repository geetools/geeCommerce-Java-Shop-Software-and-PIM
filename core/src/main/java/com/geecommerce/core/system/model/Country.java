package com.geecommerce.core.system.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface Country extends Model {
    public Id getId();

    public String getCode();

    public String getCode3();

    public ContextObject<String> getName();

    public Double getLatitude();

    public Double getLongitude();

    public String getCurrency();

    public String getTimezone();

    String getPhoneCode();

    class Col {
        public static final String CODE = "code";

        public static final String CODE3 = "code3";

        public static final String NAME = "name";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        public static final String CURRENCY = "currency";

        public static final String TIMEZONE = "timezone";

        public static final String PHONE_CODE = "phone_code";
    }
}
