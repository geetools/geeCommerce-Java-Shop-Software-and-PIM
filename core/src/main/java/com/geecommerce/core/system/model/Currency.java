package com.geecommerce.core.system.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface Currency extends Model {
    public Id getId();

    public String getCode();

    public String getName();

    public String getSymbol();

    public String getCountry();

    class Col {
        public static final String CODE = "code";

        public static final String NAME = "name";

        public static final String SYMBOL = "symbol";

        public static final String COUNTRY = "country";
    }
}
