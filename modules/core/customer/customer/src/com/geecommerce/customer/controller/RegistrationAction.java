package com.geecommerce.customer.controller;

import com.geecommerce.core.util.BasicValidator;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.customer.configuration.Key;
import com.geecommerce.customer.form.RegistrationForm;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

@UrlBinding("/customer/registration/{$event}")
public class RegistrationAction extends BaseActionBean {
    @ValidateNestedProperties({ @Validate(field = "email", required = true, on = "update"), @Validate(field = "forename", required = true, minlength = 2, maxlength = 50, on = { "update" }),
        @Validate(field = "surname", required = true, minlength = 5, maxlength = 50, on = { "update" }),

        @Validate(field = "invoice.firstName", required = true, minlength = 2, maxlength = 50, on = "update"),
        @Validate(field = "invoice.lastName", required = true, minlength = 2, maxlength = 50, on = "update"),
        @Validate(field = "invoice.address1", required = true, minlength = 2, maxlength = 128, on = "update"),
        @Validate(field = "invoice.city", required = true, minlength = 2, maxlength = 45, on = "update"),
        @Validate(field = "invoice.country", required = true, on = "update"), @Validate(field = "invoice.zip", required = true, on = "update"), })
    private RegistrationForm customerForm = null;

    private Customer customer = null;

    private final CustomerService customerService;

    @Inject
    public RegistrationAction(CustomerService customerService) {
        this.customerService = customerService;
    }

    @HandlesEvent("edit")
    public Resolution edit() {
        if (customerForm == null) {
            this.customerForm = new RegistrationForm();

            if (isCustomerLoggedIn()) {
                Customer customer = getLoggedInCustomer();

                this.customerForm.setEmail(customer.getEmail());
                this.customerForm.setPhone(customer.getPhone());

                this.customerForm.setForename(customer.getForename());
                this.customerForm.setSurname(customer.getSurname());
            }
        }

        return view("customer/registration/edit_form");
    }

    @HandlesEvent("update")
    public Resolution update() {
        Customer customer = getLoggedInCustomer();

        if (customer != null) {
            customer.setForename(getCustomerForm().getForename()).setSurname(getCustomerForm().getSurname()).setEmail(getCustomerForm().getEmail());

            customerService.updateCustomer(customer);
        }

        return view("customer/registration/edit_form");
    }

    public Boolean getUseEmail() {
        return app.cpBool_(Key.USE_EMAIL, false);
    }

    public RegistrationForm getCustomerForm() {
        return this.customerForm;
    }

    public void setCustomerForm(RegistrationForm customerForm) {
        this.customerForm = customerForm;
    }

    public Customer getCustomer() {
        return app.getModel(Customer.class);
    }

    @ValidationMethod(when = ValidationState.ALWAYS, on = { "update" })
    public void validateEmail(ValidationErrors errors) {
        String email = customerForm.getEmail();

        if (!BasicValidator.isValidEmail(email)) {
            errors.add("email", new LocalizableError("form.email.errorMessage"));
        }
    }
}
