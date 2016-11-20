package com.geecommerce.coupon.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.CustomerGroup;
import com.geecommerce.price.model.PriceType;
import com.owlike.genson.annotation.JsonIgnore;

public interface Coupon extends MultiContextModel {

    public Coupon setId(Id id);

    @JsonIgnore
    public List<CouponCode> getCodes();

    public ContextObject<String> getName();

    public Coupon setName(ContextObject<String> name);

    public ContextObject<String> getDescription();

    public Coupon setDescription(ContextObject<String> description);

    public Date getFromDate();

    public Coupon setFromDate(Date fromDate);

    public Date getToDate();

    public Coupon setToDate(Date toDate);

    public Integer getUsesPerCoupon();

    public Coupon setUsesPerCoupon(Integer uses);

    public Integer getUsesPerCustomer();

    public Coupon setUsesPerCustomer(Integer uses);

    public CouponFilterNode getCouponCondition();

    public Coupon setCouponCondition(CouponFilterNode couponCondition);

    public CouponAction getCouponAction();

    public Coupon setCouponAction(CouponAction couponAction);

    public Boolean getAuto();

    public Coupon setAuto(Boolean auto);

    public String getErpCode();

    public Coupon setErpCode(String erpCode);

    public CouponCodeGeneration getCouponCodeGeneration();

    public Coupon setCouponCodeGeneration(CouponCodeGeneration couponCodeGeneration);

    public Coupon addCustomerGroup(CustomerGroup customerGroup);

    public List<Id> getCustomerGroupIds();

    public Coupon setCustomerGroupIds(List<Id> customerGroupIds);

    public Coupon addPriceType(PriceType priceType);

    public List<Id> getPriceTypeIds();

    public Coupon setPriceTypeIds(List<Id> priceTypeIds);

    public Integer getValidAfterFirstUse();

    public Coupon setValidAfterFirstUse(Integer validAfterFirstUse);

    public ContextObject<Boolean> getEnabled();

    public Coupon setEnabled(ContextObject<Boolean> enabled);

    public boolean getDeleted();

    public Coupon setDeleted(boolean deleted);

    static final class Col {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String ERP_CODE = "erp_code";
        public static final String DESCRIPTION = "descr";
        public static final String ENABLED = "enabled";
        public static final String FROM_DATE = "from_d";
        public static final String TO_DATE = "to_d";
        public static final String USES_PER_COUPON = "u_p_coupon";
        public static final String USES_PER_CUSTOMER = "u_p_customer";
        public static final String CONDITION = "condition";
        public static final String ACTION = "action";
        public static final String AUTO = "auto";
        public static final String CODE_GENERATOR = "code_generator";
        public static final String CUSTOMER_GROUP_IDS = "cust_grp_ids";
        public static final String PRICE_TYPE_IDS = "prc_type_ids";
        public static final String VALID_AFTER_FIRST_USE = "v_a_f_u";
        public static final String DELETED = "deleted";
    }

}
