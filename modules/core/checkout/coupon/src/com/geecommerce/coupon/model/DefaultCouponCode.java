package com.geecommerce.coupon.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.service.CouponService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Model(collection = "coupon_codes", optimisticLocking = true)
public class DefaultCouponCode extends AbstractModel implements CouponCode {

    private static final long serialVersionUID = 3888831536611850522L;

    private Id id;
    private String code;
    private Id couponId;
    @JsonIgnore
    private CouponService couponService;
    private List<CouponUsage> couponUsages;
    private Date fromDate;
    private Date toDate;
    private String email;
    private Date exported;

    @Inject
    public DefaultCouponCode(CouponService couponService) {
	this.couponService = couponService;
    }

    @Override
    public CouponCode setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public CouponCode belongsTo(Coupon coupon) {
	this.couponId = coupon.getId();
	return this;
    }

    @Override
    @JsonIgnore
    public Coupon getCoupon() {
	return couponService.getCoupon(couponId);
    }

    @Override
    public Id getCouponId() {
	return couponId;
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public CouponCode setCode(String code) {
	this.code = code;
	return this;
    }

    @Override
    public List<CouponUsage> getCouponUsages() {
	if (couponUsages == null)
	    couponUsages = new ArrayList<>();
	return couponUsages;
    }

    @Override
    public Date getFromDate() {
	return fromDate;
    }

    @Override
    public CouponCode setFromDate(Date fromDate) {
	this.fromDate = fromDate;
	return this;
    }

    @Override
    public Date getToDate() {
	return toDate;
    }

    @Override
    public CouponCode setToDate(Date toDate) {
	this.toDate = toDate;
	return this;
    }

    @Override
    public String getEmail() {
	return email;
    }

    @Override
    public CouponCode setEmail(String email) {
	this.email = email;
	return this;
    }

    @Override
    public Date getExportedDate() {
	return exported;
    }

    @Override
    public CouponCode setExportedDate(Date exported) {
	this.exported = exported;
	return this;
    }

    @Override
    public CouponCode useCoupon(Id orderId, Id customerId) {
	CouponUsage couponUsage = app.getModel(CouponUsage.class);
	couponUsage.setCustomerId(customerId);
	couponUsage.setOrderId(orderId);
	couponUsage.setUsageDate(new Date());
	if (couponUsages == null)
	    couponUsages = new ArrayList<>();

	if (getCoupon().getValidAfterFirstUse() != null) {
	    if (couponUsages.size() == 0) {
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(getCoupon().getValidAfterFirstUse());
		setToDate(dtPlusOne.toDate());
	    }
	}
	couponUsages.add(couponUsage);
	return this;
    }

    @Override
    public CouponCode useCoupon(Id orderId, String email) {
	CouponUsage couponUsage = app.getModel(CouponUsage.class);
	couponUsage.setEmail(email);
	couponUsage.setOrderId(orderId);
	couponUsage.setUsageDate(new Date());
	if (couponUsages == null)
	    couponUsages = new ArrayList<>();

	if (getCoupon().getValidAfterFirstUse() != null) {
	    if (couponUsages.size() == 0) {
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(getCoupon().getValidAfterFirstUse());
		setToDate(dtPlusOne.toDate());
	    }
	}

	couponUsages.add(couponUsage);
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	this.id = id_(map.get(Column.ID));
	this.code = str_(map.get(Column.CODE));
	this.email = str_(map.get(Column.EMAIL));
	this.couponId = id_(map.get(Column.COUPON_ID));
	this.fromDate = date_(map.get(Column.FROM_DATE));
	this.toDate = date_(map.get(Column.TO_DATE));
	this.exported = date_(map.get(Column.EXPORTED));

	List<Map<String, Object>> couponUsagesList = list_(map.get(Column.COUPON_USAGE));
	if (couponUsagesList != null && couponUsagesList.size() > 0) {
	    this.couponUsages = new ArrayList<>();
	    for (Map<String, Object> m : couponUsagesList) {
		CouponUsage couponUsage = app.getModel(CouponUsage.class);
		couponUsage.fromMap(m);
		this.couponUsages.add(couponUsage);
	    }
	}

    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
	m.put(Column.ID, getId());
	m.put(Column.CODE, getCode());
	m.put(Column.EMAIL, getEmail());
	m.put(Column.COUPON_ID, couponId);
	m.put(Column.EXPORTED, exported);
	if (getCouponUsages() != null && getCouponUsages().size() > 0) {
	    List<Map<String, Object>> couponUsagesList = new ArrayList<>();
	    for (CouponUsage couponUsage : getCouponUsages()) {
		couponUsagesList.add(couponUsage.toMap());
	    }
	    m.put(Column.COUPON_USAGE, couponUsagesList);
	}
	m.put(Column.FROM_DATE, getFromDate());
	m.put(Column.TO_DATE, getToDate());

	return m;
    }

}
