package com.geecommerce.customerReview.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.customerReview.model.CustomerReview;
import com.geecommerce.customerReview.repository.CustomerReviews;
import com.google.inject.Inject;

@Service
public class DefaultCustomerReviewService implements CustomerReviewService {

    private final CustomerReviews customerReviews;

    @Inject
    public DefaultCustomerReviewService(CustomerReviews customerReviews) {
        this.customerReviews = customerReviews;
    }

    @Override
    public CustomerReview createReview(CustomerReview customerReview) {
        return customerReviews.add(customerReview);
    }

    @Override
    public int[] ratingsForProductReviews(Id productId) {
        List<CustomerReview> customerReviews = productReviews(productId, null);
        if (customerReviews == null || customerReviews.size() == 0)
            return new int[5];

        int[] stars = new int[5];
        for (CustomerReview review : customerReviews) {
            stars[review.getRating() - 1]++;
        }
        return stars;
    }

    @Override
    public List<CustomerReview> productReviews(Id productId, QueryOptions queryOptions) {
        if (productId == null)
            return null;
        Map<String, Object> filter = new HashMap<>();
        filter.put(CustomerReview.Column.PRODUCT_ID, productId);
        filter.put(CustomerReview.Column.PUBLISHED, true);
        return customerReviews.find(CustomerReview.class, filter, queryOptions);
    }

    @Override
    public List<CustomerReview> customerReviews(Id customerId, Boolean published, QueryOptions queryOptions) {
        if (customerId == null)
            return null;
        Map<String, Object> filter = new HashMap<>();
        filter.put(CustomerReview.Column.CUSTOMER_ID, customerId);
        if (published != null)
            filter.put(CustomerReview.Column.PUBLISHED, published);
        return customerReviews.find(CustomerReview.class, filter, queryOptions);
    }

    @Override
    public Boolean hasReview(Id productId, Id customerId) {
        if (customerId == null || productId == null)
            return null;
        Map<String, Object> filter = new HashMap<>();
        filter.put(CustomerReview.Column.CUSTOMER_ID, customerId);
        filter.put(CustomerReview.Column.PRODUCT_ID, productId);
        CustomerReview customerReview = customerReviews.findOne(CustomerReview.class, filter);
        if (customerReview != null)
            return true;
        return false;
    }

    @Override
    public Integer totalReviews(Id productId) {
        List<CustomerReview> customerReviews = productReviews(productId, null);
        if (customerReviews == null)
            return 0;
        return customerReviews.size();
    }

    @Override
    public Double averageRating(Id productId) {
        List<CustomerReview> customerReviews = productReviews(productId, null);
        if (customerReviews == null || customerReviews.size() == 0)
            return 0.0;

        int total_review = customerReviews.size();
        double total_rating = 0.0;
        for (CustomerReview customerReview : customerReviews) {
            total_rating += customerReview.getRating();
        }

        return total_rating / total_review;
    }

    @Override
    public CustomerReview getCustomerReview(Id customerReviewId) {
        return customerReviews.findById(CustomerReview.class, customerReviewId);
    }

    @Override
    public void updateReview(CustomerReview customerReview) {
        customerReviews.update(customerReview);
    }

    @Override
    public void deleteReview(CustomerReview customerReview) {
        customerReviews.remove(customerReview);
    }
}
