package com.geecommerce.customerReview.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.google.inject.Inject;

@Model
public class DefaultCustomerReview extends AbstractModel implements CustomerReview {
    private Id id = null;
    private Id requestContextId = null;
    private Id customerId = null;
    private Id productId = null;
    private Date createdOn = null;
    private Date modifiedOn = null;
    private String headline = null;
    private String review = null;
    private Integer rating = null;
    private List<Id> thinkHelpful = null;
    private List<Id> thinkUnhelpful = null;
    private Customer customer = null;
    private Boolean published = null;
    private List<Abuse> abuses = new ArrayList<>();

    private final CustomerService customerService;
    private final ProductService productService;

    @Inject
    public DefaultCustomerReview(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CustomerReview setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Customer getCustomer() {
        if (customer == null)
            customer = customerService.getCustomer(customerId);
        return customer;
    }

    @Override
    public CustomerReview belongsTo(Customer customer) {
        if (customer != null) {
            customerId = customer.getId();
        }
        return this;
    }

    @Override
    public List<Id> getThinkHelpful() {
        if (thinkHelpful == null)
            thinkHelpful = new ArrayList<>();
        return thinkHelpful;
    }

    @Override
    public CustomerReview setThinkHelpful(List<Id> customers) {
        this.thinkHelpful = customers;
        return this;
    }

    @Override
    public List<Id> getThinkUnhelpful() {
        if (thinkUnhelpful == null)
            thinkUnhelpful = new ArrayList<>();
        return thinkUnhelpful;
    }

    @Override
    public CustomerReview setThinkUnhelpful(List<Id> customers) {
        this.thinkUnhelpful = customers;
        return this;
    }

    @Override
    public int getThinkHelpfulCount() {
        if (getThinkHelpful() == null)
            return 0;
        return getThinkHelpful().size();
    }

    @Override
    public int getThinkUnhelpfulCount() {
        if (getThinkUnhelpful() == null)
            return 0;
        return getThinkUnhelpful().size();
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public CustomerReview setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Product getProduct() {
        return productService.getProduct(getProductId());
    }

    @Override
    public Integer getRating() {
        return rating;
    }

    @Override
    public CustomerReview setRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public String getHeadline() {
        return headline;
    }

    @Override
    public CustomerReview setHeadline(String headline) {
        this.headline = headline;
        return this;
    }

    @Override
    public String getReview() {
        return review;
    }

    @Override
    public String getReviewHtml() {
        return review.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
    }

    @Override
    public CustomerReview setReview(String review) {
        this.review = review;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.requestContextId = id_(map.get(Column.REQUEST_CONTEXT_ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.createdOn = date_(map.get(Column.CREATED_ON));
        this.modifiedOn = date_(map.get(Column.MODIFIED_ON));
        this.review = str_(map.get(Column.REVIEW));
        this.headline = str_(map.get(Column.HEADLINE));
        this.rating = int_(map.get(Column.RATING));
        this.thinkHelpful = list_(map.get(Column.THINK_HELPFUL));
        this.thinkUnhelpful = list_(map.get(Column.THINK_UNHELPFUL));
        this.published = bool_(map.get(Column.PUBLISHED));

        List<Map<String, Object>> abuseList = list_(map.get(Column.ABUSES));
        if (abuseList != null) {
            this.abuses = new ArrayList<>();
            for (Map<String, Object> abuse : abuseList) {
                Abuse a = app.model(Abuse.class);
                a.fromMap(abuse);
                this.abuses.add(a);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.ID, getId());
        m.put(Column.REQUEST_CONTEXT_ID, getRequestContextId());

        if (getCustomerId() != null) {
            m.put(Column.CUSTOMER_ID, getCustomerId());
        }

        m.put(Column.CREATED_ON, getCreatedOn());
        m.put(Column.MODIFIED_ON, getCreatedOn());
        m.put(Column.REVIEW, getReview());
        m.put(Column.HEADLINE, getHeadline());
        m.put(Column.RATING, getRating());
        m.put(Column.THINK_HELPFUL, getThinkHelpful());
        m.put(Column.THINK_UNHELPFUL, getThinkUnhelpful());
        m.put(Column.PRODUCT_ID, getProductId());
        m.put(Column.PUBLISHED, getPublished());

        List<Map<String, Object>> abuseList = new ArrayList<>();
        if (getAbuses() != null) {
            abuseList.addAll(getAbuses().stream().map(Abuse::toMap).collect(Collectors.toList()));
            m.put(Column.ABUSES, abuseList);
        }

        return m;
    }

    @Override
    public CustomerReview setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public Boolean getPublished() {
        return published;
    }

    @Override
    public CustomerReview setPublished(Boolean published) {
        this.published = published;
        return this;
    }

    @Override
    public Boolean getRatedByCustomer(Id customerId) {
        if (getThinkHelpful().contains(customerId) || getThinkUnhelpful().contains(customerId)
            || customerId.equals(getCustomerId()))
            return true;
        return false;
    }

    @Override
    public Boolean getRatedByCustomer() {
        Customer customer = app.getLoggedInCustomer();
        if (customer == null)
            return null;
        return getRatedByCustomer(customer.getId());
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public CustomerReview setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public List<Abuse> getAbuses() {
        if (abuses == null)
            abuses = new ArrayList<>();
        return abuses;
    }

    @Override
    public CustomerReview setAbuses(List<Abuse> abuses) {
        this.abuses = abuses;
        return this;
    }

    @Override
    public CustomerReview addAbuse(Abuse abuse) {
        if (abuses == null)
            abuses = new ArrayList<>();
        abuses.add(abuse);
        return this;
    }
}
