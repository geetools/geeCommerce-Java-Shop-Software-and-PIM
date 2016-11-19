package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model
public class DefaultCouponCodeGeneration extends AbstractModel implements CouponCodeGeneration {

    @Column(Col.AUTO)
    private Boolean auto;
    @Column(Col.CODE)
    private String code;
    @Column(Col.PATTERN)
    private Id pattern;
    @Column(Col.QUANTITY)
    private Integer quantity;
    @Column(Col.LENGTH)
    private Integer length;
    @Column(Col.PREFIX)
    private String prefix;
    @Column(Col.POSTFIX)
    private String postfix;

    @Override
    public Boolean getAuto() {
        return auto;
    }

    @Override
    public CouponCodeGeneration setAuto(Boolean auto) {
        this.auto = auto;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public CouponCodeGeneration setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public CouponCodeGeneration setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public Integer getLength() {
        return length;
    }

    @Override
    public CouponCodeGeneration setLength(Integer length) {
        this.length = length;
        return null;
    }

    @Override
    public Id getPattern() {
        return pattern;
    }

    @Override
    public CouponCodeGeneration setPattern(Id pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public CouponCodeGeneration setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String getPostfix() {
        return postfix;
    }

    @Override
    public CouponCodeGeneration setPostfix(String postfix) {
        this.postfix = postfix;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.auto = bool_(map.get(Col.AUTO));
        this.code = str_(map.get(Col.CODE));
        this.pattern = id_(map.get(Col.PATTERN));
        this.quantity = int_(map.get(Col.QUANTITY));
        this.length = int_(map.get(Col.LENGTH));
        this.prefix = str_(map.get(Col.PREFIX));
        this.postfix = str_(map.get(Col.POSTFIX));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
        m.put(Col.AUTO, getAuto());
        m.put(Col.CODE, getCode());
        m.put(Col.PATTERN, getPattern());
        m.put(Col.QUANTITY, getQuantity());
        m.put(Col.LENGTH, getLength());
        m.put(Col.PREFIX, getPrefix());
        m.put(Col.POSTFIX, getPostfix());

        return m;
    }

}
