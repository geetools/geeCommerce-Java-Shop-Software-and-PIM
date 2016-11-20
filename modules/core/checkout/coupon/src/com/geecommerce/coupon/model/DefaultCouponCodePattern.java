package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model("coupon_patterns")
public class DefaultCouponCodePattern extends AbstractModel implements CouponCodePattern {
    private Id id;
    private ContextObject<String> name;
    private String terminalString;

    private Boolean isPattern;
    private String pattern;
    private Map<String, String> productionRules;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CouponCodePattern setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public CouponCodePattern setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public String getTerminalString() {
        return terminalString;
    }

    @Override
    public CouponCodePattern setTerminalString(String terminalString) {
        this.terminalString = terminalString;
        return this;
    }

    @Override
    public Boolean getIsPattern() {
        return isPattern;
    }

    @Override
    public CouponCodePattern setIsPattern(Boolean pattern) {
        isPattern = pattern;
        return this;
    }

    @Override
    public CouponCodePattern setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public Map<String, String> getProductionRules() {
        return productionRules;
    }

    @Override
    public CouponCodePattern setProductionRules(Map<String, String> productionRules) {
        this.productionRules = productionRules;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.name = ctxObj_(map.get(Column.NAME));
        this.isPattern = bool_(map.get(Column.IS_PATTERN));
        this.pattern = str_(map.get(Column.PATTERN));
        this.productionRules = map_(map.get(Column.PRODUCTION_RULES));
        this.terminalString = str_(map.get(Column.TERMINAL_STRING));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
        m.put(Column.ID, getId());
        m.put(Column.IS_PATTERN, getIsPattern());
        m.put(Column.PATTERN, getPattern());
        m.put(Column.NAME, getName());
        m.put(Column.PRODUCTION_RULES, getProductionRules());
        m.put(Column.TERMINAL_STRING, getTerminalString());

        return m;
    }

}
