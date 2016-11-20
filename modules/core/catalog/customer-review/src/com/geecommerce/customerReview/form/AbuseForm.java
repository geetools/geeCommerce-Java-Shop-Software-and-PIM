package com.geecommerce.customerReview.form;

import com.geemvc.validation.annotation.Check;

public class AbuseForm {

    @Check(required = true, on = { "/process-abuse/{id}" })
    private String headline = null;

    @Check(required = true, on = { "/process-abuse/{id}" })
    private String text = null;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
