package com.geecommerce.customerReview.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;

public interface CustomerReview extends Model {
    public Id getId();

    public CustomerReview setId(Id id);

    public Id getRequestContextId();

    public Id getCustomerId();

    public Customer getCustomer();

    public CustomerReview belongsTo(Customer customer);

    public List<Id> getThinkHelpful();

    public CustomerReview setThinkHelpful(List<Id> customers);

    public List<Id> getThinkUnhelpful();

    public CustomerReview setThinkUnhelpful(List<Id> customers);

    public int getThinkHelpfulCount();

    public int getThinkUnhelpfulCount();

    public Id getProductId();

    public CustomerReview setProductId(Id productId);

    public Product getProduct();

    public Integer getRating();

    public CustomerReview setRating(Integer rating);

    public String getHeadline();

    public CustomerReview setHeadline(String headline);

    public String getReview();

    public String getReviewHtml();

    public CustomerReview setReview(String review);

    public Date getCreatedOn();

    public Date getModifiedOn();

    public Boolean getPublished();

    public CustomerReview setPublished(Boolean published);

    public Boolean getRatedByCustomer(Id customerId);

    public Boolean getRatedByCustomer();

    public List<Abuse> getAbuses();

    public CustomerReview setAbuses(List<Abuse> abuses);

    public CustomerReview addAbuse(Abuse abuse);

    static final class Column {
        public static final String ID = "_id";
        public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String PRODUCT_ID = "product_id";
        public static final String CREATED_ON = "cr_on";
        public static final String MODIFIED_ON = "mod_on";
        public static final String HEADLINE = "headline";
        public static final String REVIEW = "review";
        public static final String RATING = "rating";
        public static final String THINK_HELPFUL = "helpful";
        public static final String THINK_UNHELPFUL = "unhelpful";
        public static final String PUBLISHED = "published";
        public static final String ABUSES = "abuses";
    }
}
