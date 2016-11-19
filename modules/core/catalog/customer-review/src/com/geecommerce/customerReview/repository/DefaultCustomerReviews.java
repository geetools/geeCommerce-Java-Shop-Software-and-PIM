package com.geecommerce.customerReview.repository;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.customerReview.dao.CustomerReviewDao;
import com.google.inject.Inject;

@Repository
public class DefaultCustomerReviews extends AbstractRepository implements CustomerReviews {
    private final CustomerReviewDao customerReviewDao;

    @Inject
    public DefaultCustomerReviews(CustomerReviewDao customerReviewDao) {
        this.customerReviewDao = customerReviewDao;
    }

    @Override
    public Dao dao() {
        return customerReviewDao;
    }
}
