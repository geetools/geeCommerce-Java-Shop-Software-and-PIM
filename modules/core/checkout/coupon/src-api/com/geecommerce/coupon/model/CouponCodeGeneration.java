package com.geecommerce.coupon.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface CouponCodeGeneration extends Model {

    public Boolean getAuto();

    public CouponCodeGeneration setAuto(Boolean auto);

    public String getCode();

    public CouponCodeGeneration setCode(String code);

    public Integer getQuantity();

    public CouponCodeGeneration setQuantity(Integer quantity);

    public Integer getLength();

    public CouponCodeGeneration setLength(Integer length);

    public Id getPattern();

    public CouponCodeGeneration setPattern(Id pattern);

    public String getPrefix();

    public CouponCodeGeneration setPrefix(String prefix);

    public String getPostfix();

    public CouponCodeGeneration setPostfix(String postfix);

    static final class Col {
        public static final String AUTO = "auto";
        public static final String CODE = "code";
        public static final String PATTERN = "pattern";
        public static final String QUANTITY = "quantity";
        public static final String LENGTH = "length";
        public static final String PREFIX = "prefix";
        public static final String POSTFIX = "postfix";
    }
}
