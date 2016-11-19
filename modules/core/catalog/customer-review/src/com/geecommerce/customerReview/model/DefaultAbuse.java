package com.geecommerce.customerReview.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Model
public class DefaultAbuse extends AbstractModel implements Abuse {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.HEADLINE)
    private String headline = null;

    @Column(Col.TEXT)
    private String text = null;

    @Column(Col.CUSTOMER_ID)
    private Id customerId = null;

    @Column(Col.REVIEW_ID)
    private Id reviewId = null;

    @Column(Col.CREATED_BY)
    private String createdBy;

    @Column(Col.CREATED_ON)
    private Date createdOn;

    @Override
    public Id getId() {
        return this.id;
    }

    @Override
    public Abuse setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getHeadline() {
        return headline;
    }

    @Override
    public Abuse setHeadline(String headline) {
        this.headline = headline;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    public Abuse setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Abuse setCustomerId(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    public Id getReviewId() {
        return reviewId;
    }

    @Override
    public Abuse setReviewId(Id reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Abuse setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Abuse setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Col.ID));
        this.reviewId = id_(map.get(Col.REVIEW_ID));
        this.customerId = id_(map.get(Col.CUSTOMER_ID));
        this.headline = str_(map.get(Col.HEADLINE));
        this.text = str_(map.get(Col.TEXT));
        this.createdBy = str_(map.get(Col.CREATED_BY));
        this.createdOn = date_(map.get(Col.CREATED_ON));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.ID, getId());
        m.put(Col.REVIEW_ID, getReviewId());
        m.put(Col.CUSTOMER_ID, getCustomerId());
        m.put(Col.HEADLINE, getHeadline());
        m.put(Col.TEXT, getText());
        m.put(Col.CREATED_BY, getCreatedBy());
        m.put(Col.CREATED_ON, getCreatedOn());
        return m;
    }
}
