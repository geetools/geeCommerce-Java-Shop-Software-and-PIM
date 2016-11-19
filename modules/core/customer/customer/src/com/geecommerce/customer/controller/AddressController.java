package com.geecommerce.customer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.country.service.CountryService;
import com.geecommerce.customer.configuration.Key;
import com.geecommerce.customer.form.AddressForm;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geemvc.Bindings;
import com.geemvc.HttpMethod;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/customer/address")
public class AddressController extends BaseController {
    @Inject
    protected App app;
    
    protected final CustomerService customerService;
    protected final CountryService countryService;

    private static final Logger LOG = LogManager.getLogger(AccountController.class);

    @Inject
    public AddressController(CustomerService customerService, CountryService countryService) {
        this.customerService = customerService;
        this.countryService = countryService;
    }

    @Request("/overview")
    public Result overview() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        return view("customer/address/overview").bind("addresses", getAddresses());
    }

    @Request("/new")
    public Result newAddress() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        return view("customer/address/new_form").bind("countries", getCountries());
    }

    @Request(value = "/add", method = HttpMethod.POST)
    public Result createAddress(@Valid AddressForm addressForm, Bindings bindings) {

        if (bindings.hasErrors())
            return Results.view("customer/address/new_form")
                .bind(bindings.typedValues())
                .bind("countries", getCountries());

        Customer customer = getLoggedInCustomer();
        try {
            if (customer != null) {
                Address address = app.getModel(Address.class);
                address.belongsTo(customer)
                    .setCountry(addressForm.getCountry())
                    .setCompany(addressForm.getCompany())
                    .setCity(addressForm.getCity())
                    .setDistrict(addressForm.getDistrict())
                    .setHouseNumber(addressForm.getHouseNumber())
                    .setZip(addressForm.getZip())
                    .setFax(addressForm.getFax())
                    .setMobile(addressForm.getMobile())
                    .setSalutation(addressForm.getSalutation())
                    .setForename(addressForm.getForename())
                    .setSurname(addressForm.getSurname())
                    .setTelephone(addressForm.getPhone())
                    .setState(addressForm.getState())
                    .setAddressLines(addressForm.getStreet());

                if (addressForm.getDefaultInvoiceAddress() != null && addressForm.getDefaultInvoiceAddress()) {
                    resetDefaultInvoiceAddress(address);
                } else {
                    address.unmarkAsDefaultInvoiceAddress();
                }

                if (addressForm.getDefaultDeliveryAddress() != null && addressForm.getDefaultDeliveryAddress()) {
                    resetDefaultDeliveryAddress(address);
                } else {
                    address.unmarkAsDefaultDeliveryAddress();
                }

                customerService.createAddress(address);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            LOG.error("An error occured when trying to create a new address: customer=" + customer);
            // addValidationError(app.message("Unfortunately there was an error
            // when creating a new address. Please try again later."));
            return view("customer/address/new_form").bind("addressForm", addressForm);
        }

        return redirect("/customer/address/overview/");
    }

    @Request("/edit/{id}")
    public Result edit(@PathParam("id") Id id) {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        Address address = customerService.getAddress(id);
        if (address != null) {
            return view("customer/address/edit_form")
                .bind("addressForm", populateAddressForm(address))
                .bind("id", id)
                .bind("countries", getCountries());
        } else {
            // addValidationError(app.message("Unfortunately there was an error
            // when editing the address. Please try again later."));
            LOG.error("An error occured when trying to edit the address: address ID=" + id + ", customer=" + getLoggedInCustomer());
            return redirect("/customer/account/overview/");
        }
    }

    @Request(value = "/edit-confirm/{id}", method = HttpMethod.POST)
    public Result editConfirm(@PathParam("id") Id id, @Valid AddressForm addressForm, Bindings bindings) {

        Customer loggedInCustomer = getLoggedInCustomer();
        Address address = null;

        if (bindings.hasErrors())
            return Results.view("customer/address/edit_form")
                .bind(bindings.typedValues())
                .bind("id", id)
                .bind("countries", getCountries());

        try {
            if (loggedInCustomer != null) {
                address = customerService.getAddress(id);
                address.belongsTo(loggedInCustomer)
                    .setCountry(addressForm.getCountry())
                    .setCompany(addressForm.getCompany())
                    .setCity(addressForm.getCity())
                    .setDistrict(addressForm.getDistrict())
                    .setHouseNumber(addressForm.getHouseNumber())
                    .setZip(addressForm.getZip())
                    .setFax(addressForm.getFax())
                    .setMobile(addressForm.getMobile())
                    .setSalutation(addressForm.getSalutation())
                    .setForename(addressForm.getForename())
                    .setSurname(addressForm.getSurname())
                    .setTelephone(addressForm.getPhone())
                    .setState(addressForm.getState())
                    .setAddressLines(addressForm.getStreet());

                if (addressForm.getDefaultInvoiceAddress() != null && addressForm.getDefaultInvoiceAddress()) {
                    resetDefaultInvoiceAddress(address);
                } else {
                    address.unmarkAsDefaultInvoiceAddress();
                }

                if (addressForm.getDefaultDeliveryAddress() != null && addressForm.getDefaultDeliveryAddress()) {
                    resetDefaultDeliveryAddress(address);
                } else {
                    address.unmarkAsDefaultDeliveryAddress();
                }

                customerService.updateAddress(address);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            LOG.error("An error occured when trying to update an address: customer=" + loggedInCustomer + ", address id=" + address.getId());
            // addValidationError(app.message("Unfortunately there was an error
            // when updating the address. Please try again later."));
            return view("customer/address/edit_form").bind("addressForm", addressForm);
        }

        return redirect("/customer/address/overview/");
    }

    @Request("/delete/{id}")
    public Result deleteAddress(@PathParam("id") Id id) {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        Address address = customerService.getAddress(id);
        return view("/customer/address/delete_form")
            .bind("address", address)
            .bind("id", id);
    }

    @Request("delete-confirm/{id}")
    public Result deleteConfirm(@PathParam("id") Id id) {

        Address address = null;
        try {
            address = customerService.getAddress(id);
            if (address != null) {
                customerService.removeAddress(address);
            } else {
                LOG.error("An error occured when trying to delete an address. The address doesn't exist: customer=" + getLoggedInCustomer() + ", address id=" + address.getId());
            }
        } catch (Throwable t) {
            t.printStackTrace();
            LOG.error("An error occured when trying to delete an address: customer=" + getLoggedInCustomer() + ", address id=" + address.getId());
            LOG.throwing(t);
        }

        return redirect("/customer/address/overview/");
    }

    @Request("/detail/{id}")
    public Result viewDetails(@PathParam("id") Id id) {

        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        Address address = customerService.getAddress(id);
        return view("/customer/address/detail")
            .bind("address", address)
            .bind("id", id);

    }

    @Request("/default-delivery/{id}")
    public Result markAsDefaultDeliveryAddress(@PathParam("id") Id id) {

        Address address = customerService.getAddress(id);
        if (address != null) {
            resetDefaultDeliveryAddress(address);
        }

        return redirect("/customer/address/overview/");
    }

    @Request("/default-invoice/{id}")
    public Result markAsDefaultInvoiceAddress(@PathParam("id") Id id) {

        Address address = customerService.getAddress(id);
        if (address != null) {
            resetDefaultInvoiceAddress(address);
        }

        return redirect("/customer/address/overview/");
    }

    public Boolean getUseEmail() {
        return app.cpBool_(Key.USE_EMAIL, false);
    }

    private Map<String, String> getCountries() {
        List<Country> countries = countryService.getAll();
        Map<String, String> countryMap = new HashMap<>();
        countries.stream().forEach(country -> countryMap.put(country.getCode(), country.getName().getStr()));
        return countryMap;
    }

    private List<Address> getAddresses() {
        return customerService.getAddressesFor(getLoggedInCustomer());
    }

    private void resetDefaultInvoiceAddress(Address defAddress) {
        List<Address> addresses = customerService.getAddressesFor(getLoggedInCustomer());
        if (addresses != null && !addresses.isEmpty())
            addresses.stream().filter(Address::isDefaultInvoiceAddress).forEach(address -> {
                address.unmarkAsDefaultInvoiceAddress();
                customerService.updateAddress(address);
            });

        defAddress.markAsDefaultInvoiceAddress();
        customerService.updateAddress(defAddress);
    }

    private void resetDefaultDeliveryAddress(Address defAddress) {
        List<Address> addresses = customerService.getAddressesFor(getLoggedInCustomer());
        if (addresses != null && !addresses.isEmpty())
            addresses.stream().filter(Address::isDefaultDeliveryAddress).forEach(address -> {
                address.unmarkAsDefaultDeliveryAddress();
                customerService.updateAddress(address);
            });

        defAddress.markAsDefaultDeliveryAddress();
        customerService.updateAddress(defAddress);
    }

    private AddressForm populateAddressForm(Address address) {

        AddressForm form = new AddressForm();
        form.setSalutation(address.getSalutation());
        form.setForename(address.getForename());
        form.setSurname(address.getSurname());
        form.setCompany(address.getCompany());
        form.setCountry(address.getCountry());
        form.setCity(address.getCity());
        form.setHouseNumber(address.getHouseNumber());
        form.setDistrict(address.getDistrict());
        form.setPhone(address.getTelephone());
        form.setMobile(address.getMobile());
        form.setFax(address.getFax());
        form.setState(address.getState());
        form.setZip(address.getZip());

        List<String> addressLines = address.getAddressLines();
        if (addressLines != null && !addressLines.isEmpty()) {
            form.setStreet(addressLines.get(0));
        }

        form.setDefaultDeliveryAddress(address.isDefaultDeliveryAddress());
        form.setDefaultInvoiceAddress(address.isDefaultInvoiceAddress());

        return form;
    }

    // @ValidationMethod(when = ValidationState.ALWAYS, on = { "update" })
    // public void validateEmail(ValidationErrors errors)
    // {
    // String email = customerForm.getEmail();
    //
    // if (!BasicValidator.isValidEmail(email))
    // {
    // errors.add("email", new LocalizableError("form.email.errorMessage"));
    // }
    // }
}
