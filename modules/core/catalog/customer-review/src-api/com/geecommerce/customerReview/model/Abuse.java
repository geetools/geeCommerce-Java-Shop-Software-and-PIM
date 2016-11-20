package com.geecommerce.customerReview.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface Abuse extends Model {

    public Id getId();

    public Abuse setId(Id id);

    public String getHeadline();

    public Abuse setHeadline(String headline);

    public String getText();

    public Abuse setText(String text);

    public Id getCustomerId();

    public Abuse setCustomerId(Id customerId);

    public Id getReviewId();

    public Abuse setReviewId(Id reviewId);

    static final class Col {
        public static final String ID = "_id";
        public static final String HEADLINE = "headline";
        public static final String TEXT = "text";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String REVIEW_ID = "review_id";
        public static final String CREATED_BY = "cr_by";
        public static final String CREATED_ON = "cr_on";
    }

}
