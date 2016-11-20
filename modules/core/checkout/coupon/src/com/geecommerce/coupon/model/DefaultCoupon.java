package com.geecommerce.coupon.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.customer.model.CustomerGroup;
import com.geecommerce.price.model.PriceType;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model(collection = "coupons", history = true)
@XmlRootElement(name = "coupon")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultCoupon extends AbstractMultiContextModel implements Coupon {

    private static final long serialVersionUID = -6303290523165191464L;

    @Column(Col.ID)
    private Id id;

    private CouponFilterNode couponCondition;

    private CouponAction couponAction;

    private CouponCodeGeneration codeGeneration;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled;

    private List<CouponCode> codes = null;

    private CouponCodes couponCodes;

    @Column(Col.NAME)
    private ContextObject<String> name;

    @Column(Col.DESCRIPTION)
    private ContextObject<String> description;

    @Column(Col.FROM_DATE)
    private Date fromDate;

    @Column(Col.TO_DATE)
    private Date toDate;

    @Column(Col.USES_PER_COUPON)
    private Integer usesPerCoupon;

    @Column(Col.USES_PER_CUSTOMER)
    private Integer usesPerCustomer;

    @Column(Col.AUTO)
    private Boolean auto;

    @Column(Col.DELETED)
    private boolean deleted;

    @Column(Col.ERP_CODE)
    private String erpCode;

    @Column(Col.CUSTOMER_GROUP_IDS)
    private List<Id> customerGroupIds = null;

    @Column(Col.PRICE_TYPE_IDS)
    private List<Id> priceTypeIds = null;

    @Column(Col.VALID_AFTER_FIRST_USE)
    private Integer validAfterFirstUse = null;

    @Inject
    public DefaultCoupon(CouponCodes couponCodes) {
        this.couponCodes = couponCodes;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Coupon setId(Id id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    @Override
    public List<CouponCode> getCodes() {
        if (codes == null) {
            codes = couponCodes.thatBelongTo(this);
        }
        return codes;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public Coupon setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public ContextObject<String> getDescription() {
        return description;
    }

    @Override
    public Coupon setDescription(ContextObject<String> description) {
        this.description = description;
        return this;
    }

    @Override
    public Date getFromDate() {
        return fromDate;
    }

    @Override
    public Coupon setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    @Override
    public Date getToDate() {
        return toDate;
    }

    @Override
    public Coupon setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    @Override
    public Integer getUsesPerCoupon() {
        return usesPerCoupon;
    }

    @Override
    public Coupon setUsesPerCoupon(Integer uses) {
        this.usesPerCoupon = uses;
        return this;
    }

    @Override
    public Integer getUsesPerCustomer() {
        return usesPerCustomer;
    }

    @Override
    public Coupon setUsesPerCustomer(Integer uses) {
        this.usesPerCustomer = uses;
        return this;
    }

    @Override
    public CouponFilterNode getCouponCondition() {
        return couponCondition;
    }

    @Override
    public Coupon setCouponCondition(CouponFilterNode couponCondition) {
        this.couponCondition = couponCondition;
        return this;
    }

    @Override
    public CouponAction getCouponAction() {
        return couponAction;
    }

    @Override
    public Coupon setCouponAction(CouponAction couponAction) {
        this.couponAction = couponAction;
        return this;
    }

    @Override
    public Boolean getAuto() {
        return auto;
    }

    @Override
    public Coupon setAuto(Boolean auto) {
        this.auto = auto;
        return this;
    }

    @Override
    public String getErpCode() {
        return erpCode;
    }

    @Override
    public Coupon setErpCode(String erpCode) {
        this.erpCode = erpCode;
        return this;
    }

    @Override
    public CouponCodeGeneration getCouponCodeGeneration() {
        return codeGeneration;
    }

    @Override
    public Coupon setCouponCodeGeneration(CouponCodeGeneration couponCodeGeneration) {
        this.codeGeneration = couponCodeGeneration;
        return this;
    }

    @Override
    public Coupon addCustomerGroup(CustomerGroup customerGroup) {
        if (customerGroup != null && customerGroup.getId() != null) {
            getCustomerGroupIds().add(customerGroup.getId());
        }
        return this;
    }

    @Override
    public List<Id> getCustomerGroupIds() {
        if (customerGroupIds == null)
            customerGroupIds = new ArrayList<>();
        return customerGroupIds;
    }

    @Override
    public Coupon setCustomerGroupIds(List<Id> customerGroupIds) {
        this.customerGroupIds = customerGroupIds;
        return this;
    }

    @Override
    public Coupon addPriceType(PriceType priceType) {
        if (priceType != null && priceType.getId() != null) {
            getPriceTypeIds().add(priceType.getId());
        }
        return this;
    }

    @Override
    public List<Id> getPriceTypeIds() {
        if (priceTypeIds == null)
            priceTypeIds = new ArrayList<>();
        return priceTypeIds;
    }

    @Override
    public Coupon setPriceTypeIds(List<Id> priceTypeIds) {
        this.priceTypeIds = priceTypeIds;
        return this;
    }

    @Override
    public Integer getValidAfterFirstUse() {
        return validAfterFirstUse;
    }

    @Override
    public Coupon setValidAfterFirstUse(Integer validAfterFirstUse) {
        this.validAfterFirstUse = validAfterFirstUse;
        return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public Coupon setEnabled(ContextObject<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Coupon setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        map = normalize(map);
        this.id = id_(map.get(Col.ID));
        this.name = ctxObj_(map.get(Col.NAME));
        this.description = ctxObj_(map.get(Col.DESCRIPTION));
        this.enabled = ctxObj_(map.get(Col.ENABLED));
        this.deleted = bool_(map.get(Col.DELETED), false);
        this.fromDate = date_(map.get(Col.FROM_DATE));
        this.toDate = date_(map.get(Col.TO_DATE));
        this.usesPerCoupon = int_(map.get(Col.USES_PER_COUPON));
        this.usesPerCustomer = int_(map.get(Col.USES_PER_CUSTOMER));
        this.auto = bool_(map.get(Col.AUTO), false);
        this.erpCode = str_(map.get(Col.ERP_CODE));
        this.customerGroupIds = idList_(map.get(Col.CUSTOMER_GROUP_IDS));
        this.priceTypeIds = idList_(map.get(Col.PRICE_TYPE_IDS));
        this.validAfterFirstUse = int_(map.get(Col.VALID_AFTER_FIRST_USE));

        Map<String, Object> couponConditionMap = map_(map.get(Col.CONDITION));
        if (couponConditionMap != null && couponConditionMap.size() > 0) {
            this.couponCondition = app.model(CouponFilterNode.class);
            this.couponCondition.fromMap(couponConditionMap);
        }

        Map<String, Object> couponActionMap = map_(map.get(Col.ACTION));
        if (couponActionMap != null && couponActionMap.size() > 0) {
            this.couponAction = app.model(CouponAction.class);
            this.couponAction.fromMap(couponActionMap);
        }

        Map<String, Object> codegeneratorMap = map_(map.get(Col.CODE_GENERATOR));
        if (codegeneratorMap != null && codegeneratorMap.size() > 0) {
            this.codeGeneration = app.model(CouponCodeGeneration.class);
            this.codeGeneration.fromMap(codegeneratorMap);
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put(Col.ID, getId());
        m.put(Col.NAME, getName());
        m.put(Col.DESCRIPTION, getDescription());
        m.put(Col.ENABLED, enabled);
        m.put(Col.DELETED, deleted);
        m.put(Col.FROM_DATE, getFromDate());
        m.put(Col.TO_DATE, getToDate());
        m.put(Col.USES_PER_COUPON, getUsesPerCoupon());
        m.put(Col.USES_PER_CUSTOMER, getUsesPerCustomer());
        m.put(Col.AUTO, auto);
        m.put(Col.ERP_CODE, erpCode);
        m.put(Col.VALID_AFTER_FIRST_USE, validAfterFirstUse);

        if (getCouponCondition() != null) {
            m.put(Col.CONDITION, getCouponCondition().toMap());
        } else {
            m.put(Col.CONDITION, null);
        }

        if (getCouponAction() != null) {
            m.put(Col.ACTION, getCouponAction().toMap());
        } else {
            m.put(Col.ACTION, null);
        }

        if (getCouponCodeGeneration() != null) {
            m.put(Col.CODE_GENERATOR, getCouponCodeGeneration().toMap());
        } else {
            m.put(Col.CODE_GENERATOR, null);
        }

        if (customerGroupIds != null) {
            m.put(Col.CUSTOMER_GROUP_IDS, customerGroupIds);
        } else {
            m.put(Col.CUSTOMER_GROUP_IDS, null);
        }

        if (priceTypeIds != null) {
            m.put(Col.PRICE_TYPE_IDS, priceTypeIds);
        } else {
            m.put(Col.PRICE_TYPE_IDS, null);
        }
        return m;
    }

}
