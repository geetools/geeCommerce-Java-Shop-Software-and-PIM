package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CouponCodePattern extends Model {

    public CouponCodePattern setId(Id id);

    public ContextObject<String> getName();

    public CouponCodePattern setName(ContextObject<String> name);

    public String getTerminalString();

    public CouponCodePattern setTerminalString(String terminalString);

    public Boolean getIsPattern();

    public CouponCodePattern setIsPattern(Boolean pattern);

    public CouponCodePattern setPattern(String pattern);

    public String getPattern();

    public Map<String, String> getProductionRules();

    public CouponCodePattern setProductionRules(Map<String, String> productionRules);

    static final class Column {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String IS_PATTERN = "is_pattern";
        public static final String PATTERN = "pattern";
        public static final String PRODUCTION_RULES = "production_rules";
        public static final String TERMINAL_STRING = "terminal_string";
    }
}
