package com.geecommerce.customerReview.service;

import java.util.List;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.customerReview.model.CustomerReview;

public interface CustomerReviewService extends Service {

    public CustomerReview createReview(CustomerReview customerReview);

    public int[] ratingsForProductReviews(Id productId);

    public List<CustomerReview> productReviews(Id productId, QueryOptions queryOptions);

    public List<CustomerReview> customerReviews(Id customerId, Boolean published, QueryOptions queryOptions);

    public Boolean hasReview(Id productId, Id customerId);

    public Integer totalReviews(Id productId);

    public Double averageRating(Id productId);

    public CustomerReview getCustomerReview(Id customerReviewId);

    public void updateReview(CustomerReview customerReview);

    public void deleteReview(CustomerReview customerReview);

}
