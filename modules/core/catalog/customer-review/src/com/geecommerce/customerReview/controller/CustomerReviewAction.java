package com.geecommerce.customerReview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BasePagingActionBean;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.customerReview.configuration.Key;
import com.geecommerce.customerReview.model.Abuse;
import com.geecommerce.customerReview.model.CustomerReview;
import com.geecommerce.customerReview.service.CustomerReviewService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/review/{$event}/{id}")
public class CustomerReviewAction extends BasePagingActionBean {
    @Validate(required = true, on = "add")
    private String headline = null;

    @Validate(required = true, on = "add")
    private String review = null;

    @Validate(required = true, on = "add")
    private Integer rating = null;

    @Validate(required = true, on = "process-abuse")
    private String abuseHeadline = null;

    @Validate(required = true, on = "process-abuse")
    private String abuseText = null;

    private final CustomerReviewService customerReviewService;
    private final ProductService productService;
    private final CustomerService customerService;

    private int[] stars = null;
    private Integer total = null;
    private Double average = null;

    private Product product = null;
    private String pagingUri = null;

    private String order = null;

    private String formAction;
    private String redirectUrl;

    List<CustomerReview> reviews = null;

    @Inject
    public CustomerReviewAction(CustomerReviewService customerReviewService, ProductService productService,
        CustomerService customerService) {
        this.customerReviewService = customerReviewService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @HandlesEvent("new")
    public Resolution newReview() {
        if (getId() == null)
            return new ErrorResolution(404);
        Product product = productService.getProduct(getId());
        if (product == null)
            return new ErrorResolution(404);

        if (isCustomerLoggedIn()) {
            if (customerReviewService.hasReview(getId(), ((Customer) getLoggedInCustomer()).getId())) {
                return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
            }
        }

        formAction = "/review/add/" + getId();
        redirectUrl = "/review/new/" + getId();
        return view("review/review_form");
    }

    @HandlesEvent("delete")
    public Resolution deleteReview() {
        if (!isCustomerLoggedIn()) {
            return redirect("/customer/account/login");
        }

        if (getId() == null) {
            if (isCustomerLoggedIn()) {
                return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
            } else {
                return new ErrorResolution(404);
            }
        }

        CustomerReview review = customerReviewService.getCustomerReview(getId());
        if (review == null || !review.getCustomerId().equals(((Customer) getLoggedInCustomer()).getId()))
            return new ErrorResolution(404);

        customerReviewService.deleteReview(review);

        return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
    }

    @HandlesEvent("edit")
    public Resolution editReview() {
        if (getId() == null)
            return new ErrorResolution(404);
        formAction = "/review/process-edit/" + getId();
        redirectUrl = "/review/edit/" + getId();
        CustomerReview review = customerReviewService.getCustomerReview(getId());
        if (review == null)
            return new ErrorResolution(404);
        rating = review.getRating();
        this.review = review.getReview();
        headline = review.getHeadline();
        product = productService.getProduct(review.getProductId());
        return view("review/review_form");
    }

    @HandlesEvent("view")
    public Resolution viewReviews() {
        if (getId() == null)
            return new ErrorResolution(404);
        Product product = productService.getProduct(getId());
        if (product == null)
            return new ErrorResolution(404);

        reviews = getProductReviews();
        pagingUri = "/review/view/" + getId();
        return view("review/view");
    }

    @HandlesEvent("product-view")
    public Resolution viewReviewsForProduct() {
        reviews = getProductReviews();
        return view("review/product_view");
    }

    @HandlesEvent("summary")
    public Resolution viewSummaryReviews() {
        return view("review/summary");
    }

    @HandlesEvent("customer")
    public Resolution viewCustomerReviews() {
        if (getId() == null)
            return new ErrorResolution(404);
        Customer customer = customerService.getCustomer(getId());
        if (customer == null)
            return new ErrorResolution(404);

        Boolean published = true;
        if (isCustomerLoggedIn()) {
            if (getId().equals(((Customer) getLoggedInCustomer()).getId()))
                published = null;
        }

        reviews = getCustomerReviews(published);
        pagingUri = "/review/customer/" + getId();
        return view("review/customer");
    }

    @HandlesEvent("helpful")
    public Resolution markHelpful() {
        HashMap<String, String> result = new HashMap<>();
        if (!isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return json(Json.toJson(result));
        }
        Id customerId = ((Customer) getLoggedInCustomer()).getId();
        CustomerReview customerReview = customerReviewService.getCustomerReview(getId());
        if (customerReview != null) {
            if (!customerReview.getRatedByCustomer(customerId)) {
                customerReview.getThinkHelpful().add(((Customer) getLoggedInCustomer()).getId());
                customerReviewService.updateReview(customerReview);
            }
        }

        return json(Json.toJson(result));
    }

    @HandlesEvent("unhelpful")
    public Resolution markUnhelpful() {
        HashMap<String, String> result = new HashMap<>();
        if (!isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return json(Json.toJson(result));
        }
        Id customerId = ((Customer) getLoggedInCustomer()).getId();
        CustomerReview customerReview = customerReviewService.getCustomerReview(getId());
        if (customerReview != null) {
            if (!customerReview.getRatedByCustomer(customerId)) {
                customerReview.getThinkUnhelpful().add(customerId);
                customerReviewService.updateReview(customerReview);
            }
        }
        return json(Json.toJson(result));
    }

    @HandlesEvent("add")
    public Resolution processReview() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login");

        if (getId() == null)
            return new ErrorResolution(404);

        Product p = productService.getProduct(getId());
        if (p == null)
            return new ErrorResolution(404);

        if (customerReviewService.hasReview(getId(), ((Customer) getLoggedInCustomer()).getId())) {
            return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
        }

        CustomerReview customerReview = app.model(CustomerReview.class);
        customerReview.setProductId(getId());
        customerReview.setRating(getRating());
        customerReview.setHeadline(getHeadline());
        customerReview.setReview(getReview());
        customerReview.belongsTo(this.<Customer>getLoggedInCustomer());
        customerReview.setPublished(getAutoPublished());

        customerReviewService.createReview(customerReview);

        return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
    }

    private Boolean getAutoPublished() {
        return app.cpBool_(Key.AUTO_PUBLISH, false);
    }

    @HandlesEvent("process-edit")
    public Resolution processEditReview() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login");

        if (getId() == null)
            return new ErrorResolution(404);

        CustomerReview customerReview = customerReviewService.getCustomerReview(getId());

        if (customerReview == null)
            return new ErrorResolution(404);

        customerReview.setRating(getRating());
        customerReview.setHeadline(getHeadline());
        customerReview.setReview(getReview());
        customerReview.setPublished(getAutoPublished());

        customerReviewService.updateReview(customerReview);

        return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
    }

    @HandlesEvent("abuse")
    public Resolution reportAbuse() {
        if (getId() == null)
            return new ErrorResolution(404);

        if (!isCustomerLoggedIn()) {
            redirectUrl = "/review/abuse/" + getId();
            return redirect("/customer/account/login");
        }

        CustomerReview review = customerReviewService.getCustomerReview(getId());
        if (review == null)
            return new ErrorResolution(404);
        product = productService.getProduct(review.getProductId());
        return view("review/abuse_form");
    }

    @HandlesEvent("process-abuse")
    public Resolution processReportAbuse() {
        if (getId() == null)
            return new ErrorResolution(404);

        if (!isCustomerLoggedIn()) {
            return redirect("/customer/account/login");
        }

        CustomerReview review = customerReviewService.getCustomerReview(getId());
        if (review == null)
            return new ErrorResolution(404);
        product = productService.getProduct(review.getProductId());

        Abuse abuse = app.model(Abuse.class);
        abuse.setId(app.nextId());
        abuse.setHeadline(abuseHeadline);
        abuse.setText(abuseText);
        abuse.setCustomerId(((Customer) getLoggedInCustomer()).getId());
        abuse.setReviewId(review.getId());

        review.addAbuse(abuse);
        customerReviewService.updateReview(review);

        return redirect("/review/view/" + product.getId());
    }

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Product getProduct() {
        if (product != null)
            return product;
        if (getId() == null)
            return null;
        if (product == null)
            product = productService.getProduct(getId());
        return product;
    }

    public int[] getStars() {
        if (stars == null)
            stars = customerReviewService.ratingsForProductReviews(getId());
        return stars;
    }

    public Integer getTotal() {
        if (total == null) {
            int[] stars = getStars();
            total = 0;
            for (int star : stars) {
                total += star;
            }
        }
        return total;
    }

    public String getAverage() {
        if (average == null) {
            int total_count = getTotal();
            int[] stars = getStars();
            double total_rating = 0;
            int star_value = 1;
            for (int star : stars) {
                total_rating += star_value * star;
                star_value++;
            }
            average = total_rating / total_count;
        }
        return String.format(Locale.ENGLISH, "%.2f", average);
    }

    public List<CustomerReview> getReviews() {
        return subList(reviews, getOffset(), getNumResultsPerPage());
    }

    public List<CustomerReview> subList(List<CustomerReview> list, int offset, int limit) {
        if (offset < 0 || limit < -1 || list == null)
            return null;

        if (offset > 0) {
            if (offset >= list.size()) {
                return list.subList(0, 0); // return empty.
            }
            if (limit > -1) {
                // apply offset and limit
                return list.subList(offset, Math.min(offset + limit, list.size()));
            } else {
                // apply just offset
                return list.subList(offset, list.size());
            }
        } else if (limit > -1) {
            // apply just limit
            return list.subList(0, Math.min(limit, list.size()));
        } else {
            return list.subList(0, list.size());
        }
    }

    public List<CustomerReview> getProductReviews() {
        return customerReviewService.productReviews(getId(), QueryOptions.builder().sortBy(getOrderColumn()).build());
    }

    public List<CustomerReview> getCustomerReviews(Boolean published) {
        return customerReviewService.customerReviews(getId(), published,
            QueryOptions.builder().sortByDesc(getOrderColumn()).build());
    }

    public boolean getCanEdit() {
        if (!isCustomerLoggedIn() || getId() == null)
            return false;
        return getId().equals(((Customer) getLoggedInCustomer()).getId());
    }

    public Customer getCustomer() {
        if (getId() == null)
            return null;
        return customerService.getCustomer(getId());
    }

    @Override
    public long getTotalNumResults() {
        return reviews == null ? 0 : reviews.size();
    }

    @Override
    public int getDefaultNumResultsPerPage() {
        return 5;
    }

    @Override
    public int[] getNumResultsPerPageList() {
        return new int[] { 5, 10, 20 };
    }

    @Override
    public String getPagingURI() {
        return pagingUri;
    }

    public String getOrder() {
        if (order == null)
            return getDefaultOrder();
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    private String getDefaultOrder() {
        return "helpful";
    }

    private String getOrderColumn() {
        if (getOrder().equals("helpful"))
            return CustomerReview.Column.THINK_HELPFUL;
        return CustomerReview.Column.CREATED_ON;
    }

    public String getFormAction() {
        return formAction;
    }

    public void setFormAction(String formAction) {
        this.formAction = formAction;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Id getCustomerId() {
        Customer customer = getLoggedInCustomer();
        if (customer == null)
            return null;
        return customer.getId();
    }

    public Boolean getHasReview() {
        if (getId() == null)
            return null;
        Customer customer = getLoggedInCustomer();
        if (customer == null)
            return null;
        return customerReviewService.hasReview(getId(), customer.getId());
    }

    public String getAbuseHeadline() {
        return abuseHeadline;
    }

    public void setAbuseHeadline(String abuseHeadline) {
        this.abuseHeadline = abuseHeadline;
    }

    public String getAbuseText() {
        return abuseText;
    }

    public void setAbuseText(String abuseText) {
        this.abuseText = abuseText;
    }
}
