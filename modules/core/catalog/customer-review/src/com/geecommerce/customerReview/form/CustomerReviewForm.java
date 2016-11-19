package com.geecommerce.customerReview.form;

import com.geemvc.validation.annotation.Check;

public class CustomerReviewForm {

    @Check(required = true, on = {"/add/{id}", "/process-edit/{id}"})
    private String headline = null;

    @Check(required = true, on = {"/add/{id}", "/process-edit/{id}"})
    private String review = null;

    private String rating = null;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
