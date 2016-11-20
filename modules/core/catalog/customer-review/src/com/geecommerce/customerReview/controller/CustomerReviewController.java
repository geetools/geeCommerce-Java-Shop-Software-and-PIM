package com.geecommerce.customerReview.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.customerReview.PagingContext;
import com.geecommerce.customerReview.configuration.Key;
import com.geecommerce.customerReview.form.AbuseForm;
import com.geecommerce.customerReview.form.CustomerReviewForm;
import com.geecommerce.customerReview.model.Abuse;
import com.geecommerce.customerReview.model.CustomerReview;
import com.geecommerce.customerReview.service.CustomerReviewService;
import com.geemvc.Bindings;
import com.geemvc.HttpMethod;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.validation.annotation.Required;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/review")
public class CustomerReviewController extends BaseController {
    private final CustomerReviewService customerReviewService;
    private final ProductService productService;
    private final CustomerService customerService;

    @Inject
    public CustomerReviewController(CustomerReviewService customerReviewService, ProductService productService,
        CustomerService customerService) {
        this.customerReviewService = customerReviewService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @Request("/delete/{id}")
    public Result deleteReview(@PathParam("id") Id id) {
        if (!isCustomerLoggedIn()) {
            return redirect("/customer/account/login");
        }

        if (id == null) {
            if (isCustomerLoggedIn()) {
                return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
            } else {
                return Results.view("/error/404");
            }
        }

        CustomerReview review = customerReviewService.getCustomerReview(id);
        if (review == null || !review.getCustomerId().equals(((Customer) getLoggedInCustomer()).getId()))
            return Results.view("/error/404");

        customerReviewService.deleteReview(review);

        return redirect("/review/customer/" + (((Customer) getLoggedInCustomer()).getId()));

    }

    @Request("/view/{id}")
    public Result viewReviews(@PathParam("id") Id id, @Param("order") String order, @Param("limit") Integer limit,
        @Param("page") Integer page, PagingContext pagingContext) {

        if (id == null)
            return Results.view("/error/404");

        Product product = productService.getProduct(id);

        if (product == null)
            return Results.view("/error/404");

        List<CustomerReview> reviews = getProductReviews(id, StringUtils.isNotBlank(order) ? order : getDefaultOrder());
        pagingContext.setTotalNumResults(reviews != null ? reviews.size() : 0);
        pagingContext.setLimit(limit != null ? limit : 0);
        pagingContext.setPage(page != null ? page : 1);
        List<CustomerReview> pageReviews = getReviews(reviews, pagingContext);

        return Results.view("review/view").bind("reviews", pageReviews).bind("product", product)
            .bind("order", StringUtils.isNotBlank(order) ? order : getDefaultOrder())
            .bind("pagingUri", "/review/view/" + id).bind("stars", getStars(id)).bind("total", getTotal(id))
            .bind("average", getAverage(id)).bind("hasReview", getHasReview(id))
            .bind("pagingContext", pagingContext);
    }

    @Request("/product-view/{id}")
    public Result viewReviewsForProduct(@PathParam("id") Id id) {
        return view("review/product_view").bind("reviews", getProductReviews(id, null));
    }

    @Request("/summary/{id}")
    public Result viewSummaryReviews(@PathParam("id") Id id) {
        Product product = productService.getProduct(id);

        if (product == null)
            return view("/error/404");

        return view("review/summary").bind("product", product).bind("stars", getStars(id)).bind("total", getTotal(id))
            .bind("average", getAverage(id)).bind("hasReview", getHasReview(id));
    }

    @Request("/customer/{id}")
    public Result viewCustomerReviews(@PathParam("id") Id id, @Param("order") String order,
        @Param("limit") Integer limit, @Param("page") Integer page, PagingContext pagingContext) {
        if (id == null)
            return view("/error/404");

        Customer customer = customerService.getCustomer(id);
        if (customer == null)
            return view("/error/404");

        Boolean published = true;
        if (isCustomerLoggedIn()) {
            if (id.equals(((Customer) getLoggedInCustomer()).getId()))
                published = null;
        }

        List<CustomerReview> reviews = getCustomerReviews(published, id, order);
        pagingContext.setTotalNumResults(reviews != null ? reviews.size() : 0);
        pagingContext.setLimit(limit != null ? limit : 0);
        pagingContext.setPage(page != null ? page : 1);
        List<CustomerReview> pageReviews = getReviews(reviews, pagingContext);

        return view("review/customer").bind("reviews", pageReviews)
            .bind("order", order != null ? order : getDefaultOrder()).bind("pagingUri", "/review/customer/" + id)
            .bind("pagingContext", pagingContext).bind("customer", customer).bind("canEdit", getCanEdit(id));
    }

    @Request("/helpful/{id}")
    public Result markHelpful(@PathParam("id") Id id) {
        HashMap<String, String> result = new HashMap<>();
        if (!isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return Results.stream("application/javascript", Json.toJson(result));
        }

        Id customerId = ((Customer) getLoggedInCustomer()).getId();
        CustomerReview customerReview = customerReviewService.getCustomerReview(id);
        if (customerReview != null) {
            if (!customerReview.getRatedByCustomer(customerId)) {
                customerReview.getThinkHelpful().add(((Customer) getLoggedInCustomer()).getId());
                customerReviewService.updateReview(customerReview);
            }
        }

        return Results.stream("application/javascript", Json.toJson(result));
    }

    @Request("/unhelpful/{id}")
    public Result markUnhelpful(@PathParam("id") Id id) {
        HashMap<String, String> result = new HashMap<>();
        if (!isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return Results.stream("application/javascript", Json.toJson(result));
        }

        Id customerId = ((Customer) getLoggedInCustomer()).getId();
        CustomerReview customerReview = customerReviewService.getCustomerReview(id);
        if (customerReview != null) {
            if (!customerReview.getRatedByCustomer(customerId)) {
                customerReview.getThinkUnhelpful().add(customerId);
                customerReviewService.updateReview(customerReview);
            }
        }

        return Results.stream("application/javascript", Json.toJson(result));
    }

    @Request(value = "/new/{id}", method = HttpMethod.GET)
    public Result newReview(@PathParam("id") Id id) {
        if (id == null)
            return view("/error/404");

        Product product = productService.getProduct(id);
        if (product == null)
            return view("/error/404");

        if (isCustomerLoggedIn()) {
            if (customerReviewService.hasReview(id, ((Customer) getLoggedInCustomer()).getId())) {
                return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
            }
        }

        return view("review/review_form").bind("formAction", "/review/add/" + id)
            .bind("redirectUrl", "/review/new/" + id).bind("customerLoggedIn", isCustomerLoggedIn())
            .bind("product", product)
            .bind("customerId", isCustomerLoggedIn() ? ((Customer) getLoggedInCustomer()).getId() : "");

    }

    @Request(value = "/add/{id}", method = HttpMethod.POST)
    public Result processReview(@PathParam("id") Id id, @Valid CustomerReviewForm reviewForm,
        @Param("rating") @Required String rating, Bindings bindings) {

        Product p = productService.getProduct(id);
        if (p == null)
            return view("/error/404");

        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login");

        if (id == null)
            return view("/error/404");

        if (bindings.hasErrors())
            return Results.view("review/review_form").bind(bindings.typedValues())
                .bind("formAction", "/review/add/" + id).bind("redirectUrl", "/review/new/" + id)
                .bind("customerLoggedIn", isCustomerLoggedIn()).bind("product", p)
                .bind("customerId", isCustomerLoggedIn() ? ((Customer) getLoggedInCustomer()).getId() : "");

        if (customerReviewService.hasReview(id, ((Customer) getLoggedInCustomer()).getId())) {
            return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
        }

        CustomerReview customerReview = app.model(CustomerReview.class);
        customerReview.setProductId(id);
        customerReview.belongsTo(getLoggedInCustomer());
        customerReview.setPublished(getAutoPublished());
        customerReview.setHeadline(reviewForm.getHeadline());
        customerReview.setReview(reviewForm.getReview());
        customerReview.setRating(StringUtils.isNotBlank(rating) ? Integer.valueOf(rating) : 1);

        customerReviewService.createReview(customerReview);

        return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
    }

    @Request("/edit/{id}")
    public Result editReview(@PathParam("id") Id id) {
        if (id == null)
            return view("/error/404");

        CustomerReview review = customerReviewService.getCustomerReview(id);
        if (review == null)
            return view("/error/404");

        CustomerReviewForm reviewForm = new CustomerReviewForm();
        populateReviewForm(review, reviewForm);

        return view("review/review_form").bind("formAction", "/review/process-edit/" + id)
            .bind("redirectUrl", "/review/edit/" + id).bind("reviewForm", reviewForm)
            .bind("rating", reviewForm.getRating()).bind("customerLoggedIn", isCustomerLoggedIn())
            .bind("product", productService.getProduct(review.getProductId()))
            .bind("customerId", isCustomerLoggedIn() ? ((Customer) getLoggedInCustomer()).getId() : "");
    }

    @Request(value = "/process-edit/{id}", method = HttpMethod.POST)
    public Result processEditReview(@PathParam("id") Id id, @Valid CustomerReviewForm reviewForm,
        @Param("rating") @Required Integer rating, Bindings bindings) {

        CustomerReview customerReviewSaved = customerReviewService.getCustomerReview(id);

        if (customerReviewSaved == null)
            return view("/error/404");

        if (bindings.hasErrors())
            return Results.view("review/review_form").bind(bindings.typedValues())
                .bind("formAction", "/review/process-edit/" + id).bind("redirectUrl", "/review/edit/" + id)
                .bind("rating", reviewForm.getRating()).bind("customerLoggedIn", isCustomerLoggedIn())
                .bind("product", productService.getProduct(customerReviewSaved.getProductId()))
                .bind("customerId", isCustomerLoggedIn() ? ((Customer) getLoggedInCustomer()).getId() : "");

        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login");

        if (id == null)
            return view("/error/404");

        customerReviewSaved.setRating(rating);
        customerReviewSaved.setHeadline(reviewForm.getHeadline());
        customerReviewSaved.setReview(reviewForm.getReview());
        customerReviewSaved.setPublished(getAutoPublished());

        customerReviewService.updateReview(customerReviewSaved);

        return redirect("/review/customer/" + ((Customer) getLoggedInCustomer()).getId());
    }

    @Request("/abuse/{id}")
    public Result reportAbuse(@PathParam("id") Id id) {
        if (id == null)
            return view("/error/404");

        String redirectUrl = "/review/abuse/" + id;
        if (!isCustomerLoggedIn()) {
            return redirect("/customer/account/login").bind("redirectUrl", redirectUrl);
        }

        CustomerReview review = customerReviewService.getCustomerReview(id);
        if (review == null)
            return view("/error/404");

        Product product = productService.getProduct(review.getProductId());
        return view("review/abuse_form").bind("product", product).bind("formAction", "/review/process-abuse/" + id)
            .bind("redirectUrl", redirectUrl).bind("customerLoggedIn", isCustomerLoggedIn());

    }

    @Request(value = "/process-abuse/{id}", method = HttpMethod.POST)
    public Result processReportAbuse(@PathParam("id") Id id, @Valid AbuseForm abuseForm, Bindings bindings) {

        if (id == null)
            return view("/error/404");

        if (!isCustomerLoggedIn()) {
            return redirect("/customer/account/login");
        }

        CustomerReview review = customerReviewService.getCustomerReview(id);
        if (review == null)
            return view("/error/404");

        Product product = productService.getProduct(review.getProductId());
        String redirectUrl = "/review/abuse/" + id;

        if (bindings.hasErrors())
            return Results.view("review/abuse_form").bind(bindings.typedValues()).bind("product", product)
                .bind("formAction", "/review/process-abuse/" + id).bind("redirectUrl", redirectUrl)
                .bind("customerLoggedIn", isCustomerLoggedIn());

        Abuse abuse = app.model(Abuse.class);
        abuse.setId(app.nextId());
        abuse.setHeadline(abuseForm.getHeadline());
        abuse.setText(abuseForm.getText());
        abuse.setReviewId(review.getId());

        Customer customer = getLoggedInCustomer();
        abuse.setCustomerId(customer.getId());
        abuse.setCreatedBy(customer.getForename() + " " + customer.getSurname());
        abuse.setCreatedOn(new Date());

        review.addAbuse(abuse);
        customerReviewService.updateReview(review);

        return redirect("/review/view/" + product.getId()).bind("product", product);
    }

    protected Boolean getAutoPublished() {
        return app.cpBool_(Key.AUTO_PUBLISH, false);
    }

    protected int[] getStars(Id id) {
        return customerReviewService.ratingsForProductReviews(id);
    }

    protected Integer getTotal(Id id) {
        Integer total = 0;
        int[] stars = getStars(id);
        for (int star : stars) {
            total += star;
        }
        return total;
    }

    protected String getAverage(Id id) {
        Double average = null;

        int total_count = getTotal(id);
        int[] stars = getStars(id);
        double total_rating = 0;
        int star_value = 1;

        for (int star : stars) {
            total_rating += star_value * star;
            star_value++;
        }

        average = total_rating / total_count;

        return String.format(Locale.ENGLISH, "%.2f", average);
    }

    public List<CustomerReview> getReviews(List<CustomerReview> reviews, PagingContext pagingContext) {
        return subList(reviews, pagingContext.getOffset(), pagingContext.getNumResultsPerPage());
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

    public List<CustomerReview> getProductReviews(Id id, String orderColumn) {
        return customerReviewService.productReviews(id,
            QueryOptions.builder().sortBy(getOrderColumn(orderColumn)).build());
    }

    public List<CustomerReview> getCustomerReviews(Boolean published, Id id, String orderColumn) {
        return customerReviewService.customerReviews(id, published,
            QueryOptions.builder().sortByDesc(getOrderColumn(orderColumn)).build());
    }

    private boolean getCanEdit(Id id) {
        if (!isCustomerLoggedIn() || id == null)
            return false;
        return id.equals(((Customer) getLoggedInCustomer()).getId());
    }
    //
    // public Customer getCustomer() {
    // if (getId() == null)
    // return null;
    // return customerService.getCustomer(getId());
    // }

    // public long getTotalNumResults() {
    // return reviews == null ? 0 : reviews.size();
    // }

    // @Override
    // public int getDefaultNumResultsPerPage() {
    // return 5;
    // }
    //
    // @Override
    // public int[] getNumResultsPerPageList() {
    // return new int[]{5, 10, 20};
    // }
    //
    // @Override
    // public String getPagingURI() {
    // return pagingUri;
    // }
    //
    // public String getOrder() {
    // if (order == null)
    // return getDefaultOrder();
    // return order;
    // }

    // public void setOrder(String order) {
    // this.order = order;
    // }

    private String getDefaultOrder() {
        return "helpful";
    }

    private String getOrderColumn(String order) {
        if ("helpful".equals(order))
            return CustomerReview.Column.THINK_HELPFUL;

        return CustomerReview.Column.CREATED_ON;
    }

    public Id getCustomerId() {
        Customer customer = getLoggedInCustomer();

        if (customer == null)
            return null;

        return customer.getId();
    }

    protected Boolean getHasReview(Id id) {
        if (id == null)
            return null;

        Customer customer = getLoggedInCustomer();

        if (customer == null)
            return null;

        return customerReviewService.hasReview(id, customer.getId());
    }

    protected void populateReviewForm(CustomerReview review, CustomerReviewForm reviewForm) {
        if (reviewForm == null || review == null)
            return;

        if (review != null) {
            reviewForm.setHeadline(review.getHeadline());
            reviewForm.setReview(review.getReview());
            reviewForm.setRating(String.valueOf(review.getRating()));
        }
    }

}
